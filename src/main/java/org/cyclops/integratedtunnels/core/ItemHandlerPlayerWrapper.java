package org.cyclops.integratedtunnels.core;

import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandler;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.cyclopscore.inventory.PlayerInventoryIterator;
import org.cyclops.integratedtunnels.core.helper.obfuscation.ObfuscationHelpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * An item handler for player interaction simulation
 * @author rubensworks
 */
public class ItemHandlerPlayerWrapper implements IItemHandler {

    private static final Predicate<Entity> CAN_BE_ATTACKED = new Predicate<Entity>() {
        public boolean apply(@Nullable Entity entity) {
            return entity.canBeAttackedWithItem();
        }
    };

    private final ExtendedFakePlayer player;
    private final WorldServer world;
    private final BlockPos pos;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final EnumFacing side;
    private final EnumHand hand;
    private final boolean rightClick;
    private final boolean sneaking;
    private final boolean continuousClick;
    private final int entityIndex;
    private final ISlotlessItemHandler playerReturnHandler;

    public ItemHandlerPlayerWrapper(ExtendedFakePlayer player, WorldServer world, BlockPos pos,
                                    double offsetX, double offsetY, double offsetZ, EnumFacing side, EnumHand hand,
                                    boolean rightClick, boolean sneaking, boolean continuousClick, int entityIndex,
                                    ISlotlessItemHandler playerReturnHandler) {
        this.player = player;
        this.world = world;
        this.pos = pos;
        this.continuousClick = continuousClick;
        this.entityIndex = entityIndex;
        this.offsetX = (float) offsetX;
        this.offsetY = (float) offsetY;
        this.offsetZ = (float) offsetZ;
        this.side = side;
        this.hand = hand;
        this.rightClick = rightClick;
        this.sneaking = sneaking;
        this.playerReturnHandler = playerReturnHandler;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (simulate) {
            // We can ALWAYS click with items, so consume the whole item when simulating.
            return ItemStack.EMPTY;
        }

        PlayerHelpers.setPlayerState(player, hand, pos, side, sneaking);
        PlayerHelpers.setHeldItemSilent(player, hand, stack);
        if (!continuousClick) {
            cancelDestroyingBlock(player);
        }

        if (rightClick) {
            // Use item first
            if (!stack.isEmpty()) {
                EnumActionResult actionResult = stack.getItem().onItemUseFirst(player, world, pos, side,
                        offsetX, offsetY, offsetZ, hand);
                if (actionResult == EnumActionResult.FAIL) {
                    return stack;
                } else if (actionResult == EnumActionResult.SUCCESS) {
                    returnPlayerInventory(player);
                    return ItemStack.EMPTY;
                }
                // Otherwise, PASS the logic
            }

            // Activate block
            IBlockState blockState = world.getBlockState(pos);
            if (!player.isSneaking() || stack.isEmpty()) {
                if (blockState.getBlock().onBlockActivated(world, pos, blockState, player, hand, side,
                        offsetX, offsetY, offsetZ)) {
                    returnPlayerInventory(player);
                    return ItemStack.EMPTY;
                }
            }

            // Use itemstack
            if (!stack.isEmpty()) {
                EnumActionResult cancelResult = net.minecraftforge.common.ForgeHooks.onItemRightClick(player, hand);
                if (cancelResult != null)  {
                    if (cancelResult == EnumActionResult.FAIL) {
                        return stack;
                    } else if (cancelResult == EnumActionResult.SUCCESS) {
                        returnPlayerInventory(player);
                        return ItemStack.EMPTY;
                    }
                    // Otherwise, PASS the logic
                } else {
                    ItemStack copyBeforeUse = stack.copy();
                    ActionResult<ItemStack> actionresult = stack.useItemRightClick(world, player, hand);
                    if (actionresult.getType() == EnumActionResult.FAIL) {
                        return stack;
                    }
                    if (actionresult.getResult().isEmpty()) {
                        PlayerHelpers.setHeldItemSilent(player, hand, ItemStack.EMPTY);
                        net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, hand);
                    } else {
                        PlayerHelpers.setHeldItemSilent(player, hand, actionresult.getResult());
                    }
                    if (actionresult.getType() == EnumActionResult.SUCCESS) {
                        returnPlayerInventory(player);
                        return ItemStack.EMPTY;
                    }
                }
            }

            // Use item
            if (!stack.isEmpty()) {
                // Increase reach position.
                BlockPos targetPos = pos;
                int reachDistance = MathHelper.clamp((int) player.interactionManager.getBlockReachDistance(), 0, 10);
                int i = 0;
                while (i-- < reachDistance && world.isAirBlock(targetPos)) {
                    targetPos = targetPos.offset(side.getOpposite());
                }

                EnumActionResult actionResult = stack.getItem().onItemUse(player, world, targetPos, hand, side,
                        offsetX, offsetY, offsetZ);
                if (actionResult == EnumActionResult.FAIL) {
                    return stack;
                } else if (actionResult == EnumActionResult.SUCCESS) {
                    returnPlayerInventory(player);
                    return ItemStack.EMPTY;
                }
                // Otherwise, PASS the logic
            }

            // Interact with entity
            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos));
            if (entities.size() > 0) {
                Entity entity = getEntity(entities);
                EnumActionResult actionResult = player.interactOn(entity, hand);
                if (actionResult == EnumActionResult.FAIL) {
                    return stack;
                } else if (actionResult == EnumActionResult.SUCCESS) {
                    returnPlayerInventory(player);
                    return ItemStack.EMPTY;
                }
            } else {
                return stack;
            }
        } else {
            if (!world.isAirBlock(pos)) {
                // Break block
                int durabilityRemaining = ObfuscationHelpers.getDurabilityRemaining(player.interactionManager);
                if (durabilityRemaining < 0) {
                    player.interactionManager.onBlockClicked(pos, side);
                } else if (durabilityRemaining >= 9) {
                    player.interactionManager.tryHarvestBlock(pos);
                    cancelDestroyingBlock(player);
                } else {
                    player.interactionManager.updateBlockRemoving();
                }
                returnPlayerInventory(player);
                return ItemStack.EMPTY;
            } else {
                // Attack entity
                cancelDestroyingBlock(player);

                // Interact with entity
                List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos), CAN_BE_ATTACKED);
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

    public static void cancelDestroyingBlock(EntityPlayerMP player) {
        player.interactionManager.cancelDestroyingBlock();
        ObfuscationHelpers.setDurabilityRemaining(player.interactionManager, -1);
    }

    protected Entity getEntity(List<Entity> entities) {
        if (this.entityIndex < 0) {
            return entities.get(world.rand.nextInt(entities.size()));
        }
        return entities.get(Math.min(this.entityIndex, entities.size() - 1));
    }

    private void returnPlayerInventory(EntityPlayer player) {
        PlayerInventoryIterator it = new PlayerInventoryIterator(player);
        while (it.hasNext()) {
            ItemStack itemStack = it.next();
            if (!itemStack.isEmpty()) {
                ItemStack remaining = this.playerReturnHandler.insertItem(itemStack, false);
                ItemStackHelpers.spawnItemStackToPlayer(world, pos, remaining, player);
                it.remove();
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
