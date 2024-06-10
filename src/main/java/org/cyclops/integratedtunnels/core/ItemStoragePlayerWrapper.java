package org.cyclops.integratedtunnels.core;

import com.google.common.collect.Iterators;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
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

    private static final Predicate<Entity> CAN_BE_ATTACKED = Entity::isAttackable;

    private final ExtendedFakePlayer player;
    private final ServerLevel world;
    private final BlockPos pos;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final Direction side;
    private final InteractionHand hand;
    private final boolean rightClick;
    private final boolean sneaking;
    private final boolean continuousClick;
    private final int entityIndex;
    private final IIngredientComponentStorage<ItemStack, Integer> playerReturnHandler;

    public ItemStoragePlayerWrapper(@Nullable ExtendedFakePlayer player, ServerLevel world, BlockPos pos,
                                    double offsetX, double offsetY, double offsetZ, Direction side, InteractionHand hand,
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

    public static void cancelDestroyingBlock(ServerPlayer player) {
        player.gameMode.isDestroyingBlock = false;
        player.gameMode.lastSentState = -1;
    }

    protected Entity getEntity(List<Entity> entities) {
        if (this.entityIndex < 0) {
            return entities.get(world.random.nextInt(entities.size()));
        }
        return entities.get(Math.min(this.entityIndex, entities.size() - 1));
    }

    private void returnPlayerInventory(Player player) {
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
            /* Inspired by PlayerInteractionManager#useItemOn (line 324) */

            // Send block right click event
            BlockHitResult blockRayTraceResult = new BlockHitResult(new Vec3(offsetX, offsetY, offsetZ), side, pos, false);
            PlayerInteractEvent.RightClickBlock rightClickBlockActionResult = CommonHooks.onRightClickBlock(player, hand, pos, blockRayTraceResult);
            if (rightClickBlockActionResult.isCanceled()) {
                return stack;
            }

            // Use item first
            if (rightClickBlockActionResult.getUseItem() != Event.Result.DENY) {
                if (!stack.isEmpty()) {
                    UseOnContext itemUseContext = new UseOnContext(player, hand, blockRayTraceResult);
                    InteractionResult actionResult = stack.getItem().onItemUseFirst(stack, itemUseContext);
                    if (actionResult == InteractionResult.FAIL) {
                        return stack;
                    } else if (actionResult.consumesAction()) {
                        returnPlayerInventory(player);
                        return ItemStack.EMPTY;
                    }
                    // Otherwise, PASS the logic
                }
            }

            // Activate block
            boolean playerHasHeldItem = !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty();
            boolean flag1 = (player.isSecondaryUseActive() && playerHasHeldItem)
                    && !(player.getMainHandItem().doesSneakBypassUse(world, pos, player)
                    && player.getOffhandItem().doesSneakBypassUse(world, pos, player));
            if (rightClickBlockActionResult.getUseBlock() == Event.Result.ALLOW
                    || (rightClickBlockActionResult.getUseBlock() != Event.Result.DENY && !flag1)) {
                BlockState blockState = world.getBlockState(pos);
                if (!player.isCrouching() || stack.isEmpty()) {
                    if (blockState.use(world, player, hand, blockRayTraceResult).consumesAction()) {
                        returnPlayerInventory(player);
                        return ItemStack.EMPTY;
                    }
                }
            }

            // Interact with entity
            List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(pos));
            if (entities.size() > 0) {
                Entity entity = getEntity(entities);
                InteractionResult actionResult = player.interactOn(entity, hand);
                if (actionResult == InteractionResult.FAIL) {
                    return stack;
                } else if (actionResult.consumesAction()) {
                    returnPlayerInventory(player);
                    return ItemStack.EMPTY;
                }
            }

            // Use itemstack
            if (rightClickBlockActionResult.getUseItem() != Event.Result.DENY && !stack.isEmpty()) {
                InteractionResult cancelResult = CommonHooks.onItemRightClick(player, hand);
                if (cancelResult != null)  {
                    if (cancelResult == InteractionResult.FAIL) {
                        return stack;
                    } else if (cancelResult.consumesAction()) {
                        returnPlayerInventory(player);
                        return ItemStack.EMPTY;
                    }
                    // Otherwise, PASS the logic
                } else {
                    ItemStack copyBeforeUse = stack.copy();
                    InteractionResultHolder<ItemStack> actionresult = stack.use(world, player, hand);
                    if (actionresult.getResult() == InteractionResult.FAIL) {
                        return stack;
                    }
                    if (actionresult.getObject().isEmpty()) {
                        PlayerHelpers.setHeldItemSilent(player, hand, ItemStack.EMPTY);
                        EventHooks.onPlayerDestroyItem(player, copyBeforeUse, hand);
                    } else {
                        PlayerHelpers.setHeldItemSilent(player, hand, actionresult.getObject());
                    }
                    if (actionresult.getResult().consumesAction()) {
                        // If the hand was activated, simulate the activated hand for a number of ticks, and deactivate.
                        if (player.isUsingItem()) {
                            player.updateActiveHandSimulated();
                            player.releaseUsingItem();
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
                double reachDistance = player.getAttribute(NeoForgeMod.BLOCK_REACH.value()).getValue() + 3;
                int i = 0;
                while (i++ < reachDistance && world.isEmptyBlock(targetPos)) {
                    targetPos = targetPos.relative(side.getOpposite());
                }

                UseOnContext itemUseContextReach = new UseOnContext(player, hand,
                        new BlockHitResult(new Vec3(offsetX, offsetY, offsetZ), side, targetPos, false));
                InteractionResult actionResult = stack.useOn(itemUseContextReach);
                if (actionResult == InteractionResult.FAIL) {
                    return stack;
                } else if (actionResult.consumesAction()) {
                    returnPlayerInventory(player);
                    return ItemStack.EMPTY;
                }
                // Otherwise, PASS the logic
            }
        } else {
            /* Inspired by PlayerInteractionManager#handleBlockBreakAction (line 120) */

            // Check if left clicking is allowed
            PlayerInteractEvent.LeftClickBlock event = CommonHooks.onLeftClickBlock(player, pos, side, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK);
            BlockState blockState = world.getBlockState(pos);
            if (event.isCanceled() || (event.getUseItem() == Event.Result.DENY)) { // Restore block and te data
                world.sendBlockUpdated(pos, blockState, world.getBlockState(pos), 3);
                return stack;
            }

            if (!world.isEmptyBlock(pos)) {
                // Break block
                int durabilityRemaining = player.gameMode.lastSentState;
                if (durabilityRemaining < 0) {
                    world.getBlockState(pos).attack(world, pos, player);
                    float relativeBlockHardness = blockState.getDestroyProgress(this.player, this.player.level(), pos);
                    if (relativeBlockHardness >= 1.0F) {
                        // Insta-mine
                        player.gameMode.destroyBlock(pos);
                    } else {
                        // Initiate break progress
                        player.gameMode.destroyProgressStart = player.gameMode.gameTicks;
                        player.gameMode.isDestroyingBlock = true;
                        player.gameMode.destroyPos = pos.immutable();
                        player.gameMode.lastSentState = (int) (relativeBlockHardness * 10.0F);
                    }
                } else if (durabilityRemaining >= 9) {
                    player.gameMode.destroyBlock(pos);
                    cancelDestroyingBlock(player);
                } else {
                    player.gameMode.tick();
                }
                returnPlayerInventory(player);
                return ItemStack.EMPTY;
            } else {
                // Attack entity
                cancelDestroyingBlock(player);

                // Interact with entity
                List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(pos), CAN_BE_ATTACKED::test);
                if (entities.size() > 0) {
                    Entity entity = getEntity(entities);
                    EquipmentSlot equipmentSlotType = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                    player.getAttributes().addTransientAttributeModifiers(stack.getAttributeModifiers(equipmentSlotType));
                    player.attack(entity);
                    player.getAttributes().removeAttributeModifiers(stack.getAttributeModifiers(equipmentSlotType));
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
