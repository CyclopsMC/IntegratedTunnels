package org.cyclops.integratedtunnels.core;

import com.google.common.collect.Iterators;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.cyclopscore.inventory.PlayerInventoryIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * An item storage for player interaction simulation
 * @author rubensworks
 */
public class ItemStoragePlayerWrapper implements IIngredientComponentStorage<ItemStack, Integer> {

    private static final Predicate<Entity> CAN_BE_ATTACKED = Entity::canBeAttackedWithItem;

    private final ExtendedFakePlayer player;
    private final ServerWorld world;
    private final BlockPos pos;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final Direction side;
    private final Hand hand;
    private final boolean rightClick;
    private final boolean sneaking;
    private final boolean continuousClick;
    private final int entityIndex;
    private final IIngredientComponentStorage<ItemStack, Integer> playerReturnHandler;

    public ItemStoragePlayerWrapper(@Nullable ExtendedFakePlayer player, ServerWorld world, BlockPos pos,
                                    double offsetX, double offsetY, double offsetZ, Direction side, Hand hand,
                                    boolean rightClick, boolean sneaking, boolean continuousClick, int entityIndex,
                                    IIngredientComponentStorage<ItemStack, Integer> playerReturnHandler) {
        this.player = player;
        this.world = world;
        this.pos = pos;
        this.continuousClick = continuousClick;
        this.entityIndex = entityIndex;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.side = side;
        this.hand = hand;
        this.rightClick = rightClick;
        this.sneaking = sneaking;
        this.playerReturnHandler = playerReturnHandler;
    }

    public static void cancelDestroyingBlock(ServerPlayerEntity player) {
        player.interactionManager.isDestroyingBlock = false;
        player.interactionManager.durabilityRemainingOnBlock = -1;
    }

    protected Entity getEntity(List<Entity> entities) {
        if (this.entityIndex < 0) {
            return entities.get(world.rand.nextInt(entities.size()));
        }
        return entities.get(Math.min(this.entityIndex, entities.size() - 1));
    }

    private void returnPlayerInventory(PlayerEntity player) {
        PlayerInventoryIterator it = new PlayerInventoryIterator(player);
        while (it.hasNext()) {
            ItemStack itemStack = it.next();
            if (!itemStack.isEmpty()) {
                ItemStack remaining = this.playerReturnHandler.insert(itemStack, false);
                ItemStackHelpers.spawnItemStackToPlayer(world, pos, remaining, player);
                it.remove();
            }
        }
    }

    @Override
    public IngredientComponent<ItemStack, Integer> getComponent() {
        return IngredientComponent.ITEMSTACK;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return Iterators.forArray();
    }

    @Override
    public Iterator<ItemStack> iterator(@Nonnull ItemStack prototype, Integer matchCondition) {
        return iterator();
    }

    @Override
    public long getMaxQuantity() {
        return 1;
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, boolean simulate) {
        if (simulate) {
            // We can ALWAYS click with items, so consume the whole item when simulating.
            return ItemStack.EMPTY;
        }
        if (player == null) {
            return stack;
        }

        PlayerHelpers.setPlayerState(player, hand, pos, offsetX, offsetY, offsetZ, side, sneaking);
        PlayerHelpers.setHeldItemSilent(player, hand, stack);

        if (!continuousClick) {
            cancelDestroyingBlock(player);
        }

        if (rightClick) {
            /* Inspired by PlayerInteractionManager#func_219441_a (line 324) */

            // Send block right click event
            BlockRayTraceResult blockRayTraceResult = new BlockRayTraceResult(new Vector3d(offsetX, offsetY, offsetZ), side, pos, false);
            PlayerInteractEvent.RightClickBlock rightClickBlockActionResult = ForgeHooks.onRightClickBlock(player, hand, pos, blockRayTraceResult);
            if (rightClickBlockActionResult.isCanceled()) {
                return stack;
            }

            // Use item first
            if (rightClickBlockActionResult.getUseItem() != Event.Result.DENY) {
                if (!stack.isEmpty()) {
                    ItemUseContext itemUseContext = new ItemUseContext(player, hand, blockRayTraceResult);
                    ActionResultType actionResult = stack.getItem().onItemUseFirst(stack, itemUseContext);
                    if (actionResult == ActionResultType.FAIL) {
                        return stack;
                    } else if (actionResult.isSuccessOrConsume()) {
                        returnPlayerInventory(player);
                        return ItemStack.EMPTY;
                    }
                    // Otherwise, PASS the logic
                }
            }

            // Activate block
            boolean playerHasHeldItem = !player.getHeldItemMainhand().isEmpty() || !player.getHeldItemOffhand().isEmpty();
            boolean flag1 = (player.isSecondaryUseActive() && playerHasHeldItem)
                    && !(player.getHeldItemMainhand().doesSneakBypassUse(world, pos, player)
                    && player.getHeldItemOffhand().doesSneakBypassUse(world, pos, player));
            if (rightClickBlockActionResult.getUseBlock() == Event.Result.ALLOW
                    || (rightClickBlockActionResult.getUseBlock() != Event.Result.DENY && !flag1)) {
                BlockState blockState = world.getBlockState(pos);
                if (!player.isCrouching() || stack.isEmpty()) {
                    if (blockState.onBlockActivated(world, player, hand, blockRayTraceResult).isSuccess()) {
                        returnPlayerInventory(player);
                        return ItemStack.EMPTY;
                    }
                }
            }

            // Interact with entity
            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos));
            if (entities.size() > 0) {
                Entity entity = getEntity(entities);
                ActionResultType actionResult = player.interactOn(entity, hand);
                if (actionResult == ActionResultType.FAIL) {
                    return stack;
                } else if (actionResult.isSuccessOrConsume()) {
                    returnPlayerInventory(player);
                    return ItemStack.EMPTY;
                }
            }

            // Use itemstack
            if (rightClickBlockActionResult.getUseItem() != Event.Result.DENY && !stack.isEmpty()) {
                ActionResultType cancelResult = ForgeHooks.onItemRightClick(player, hand);
                if (cancelResult != null)  {
                    if (cancelResult == ActionResultType.FAIL) {
                        return stack;
                    } else if (cancelResult.isSuccessOrConsume()) {
                        returnPlayerInventory(player);
                        return ItemStack.EMPTY;
                    }
                    // Otherwise, PASS the logic
                } else {
                    ItemStack copyBeforeUse = stack.copy();
                    ActionResult<ItemStack> actionresult = stack.useItemRightClick(world, player, hand);
                    if (actionresult.getType() == ActionResultType.FAIL) {
                        return stack;
                    }
                    if (actionresult.getResult().isEmpty()) {
                        PlayerHelpers.setHeldItemSilent(player, hand, ItemStack.EMPTY);
                        ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, hand);
                    } else {
                        PlayerHelpers.setHeldItemSilent(player, hand, actionresult.getResult());
                    }
                    if (actionresult.getType().isSuccessOrConsume()) {
                        // If the hand was activated, simulate the activated hand for a number of ticks, and deactivate.
                        if (player.isHandActive()) {
                            player.updateActiveHandSimulated();
                            player.stopActiveHand();
                        }
                        returnPlayerInventory(player);
                        return ItemStack.EMPTY;
                    }
                }
            }

            // Use item
            if (rightClickBlockActionResult.getUseItem() != Event.Result.DENY && !stack.isEmpty()) {
                // Increase reach position.
                BlockPos targetPos = pos;
                double reachDistance = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue() + 3;
                int i = 0;
                while (i++ < reachDistance && world.isAirBlock(targetPos)) {
                    targetPos = targetPos.offset(side.getOpposite());
                }

                ItemUseContext itemUseContextReach = new ItemUseContext(player, hand,
                        new BlockRayTraceResult(new Vector3d(offsetX, offsetY, offsetZ), side, targetPos, false));
                ActionResultType actionResult = stack.onItemUse(itemUseContextReach);
                if (actionResult == ActionResultType.FAIL) {
                    return stack;
                } else if (actionResult.isSuccessOrConsume()) {
                    returnPlayerInventory(player);
                    return ItemStack.EMPTY;
                }
                // Otherwise, PASS the logic
            }
        } else {
            /* Inspired by PlayerInteractionManager#func_225416_a (line 120) */

            // Check if left clicking is allowed
            PlayerInteractEvent.LeftClickBlock event = net.minecraftforge.common.ForgeHooks.onLeftClickBlock(player, pos, side);
            BlockState blockState = world.getBlockState(pos);
            if (event.isCanceled() || (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY)) { // Restore block and te data
                world.notifyBlockUpdate(pos, blockState, world.getBlockState(pos), 3);
                return stack;
            }

            if (!world.isAirBlock(pos)) {
                // Break block
                int durabilityRemaining = player.interactionManager.durabilityRemainingOnBlock;
                if (durabilityRemaining < 0) {
                    world.getBlockState(pos).onBlockClicked(world, pos, player);
                    float relativeBlockHardness = blockState.getPlayerRelativeBlockHardness(this.player, this.player.world, pos);
                    if (relativeBlockHardness >= 1.0F) {
                        // Insta-mine
                        player.interactionManager.tryHarvestBlock(pos);
                    } else {
                        // Initiate break progress
                        player.interactionManager.initialDamage = player.interactionManager.ticks;
                        player.interactionManager.isDestroyingBlock = true;
                        player.interactionManager.destroyPos = pos.toImmutable();
                        player.interactionManager.durabilityRemainingOnBlock = (int) (relativeBlockHardness * 10.0F);
                    }
                } else if (durabilityRemaining >= 9) {
                    player.interactionManager.tryHarvestBlock(pos);
                    cancelDestroyingBlock(player);
                } else {
                    player.interactionManager.tick();
                }
                returnPlayerInventory(player);
                return ItemStack.EMPTY;
            } else {
                // Attack entity
                cancelDestroyingBlock(player);

                // Interact with entity
                List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos), CAN_BE_ATTACKED::test);
                if (entities.size() > 0) {
                    Entity entity = getEntity(entities);
                    player.attackTargetEntityWithCurrentItem(entity);
                    returnPlayerInventory(player);
                    return ItemStack.EMPTY;
                } else {
                    return stack;
                }
            }
        }

        return stack;
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack prototype, Integer matchCondition, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extract(long maxQuantity, boolean simulate) {
        return ItemStack.EMPTY;
    }
}
