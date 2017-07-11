package org.cyclops.integratedtunnels.core;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.cyclops.cyclopscore.helper.BlockHelpers;

import javax.annotation.Nonnull;

/**
 * An item handler for world block placement.
 * @author rubensworks
 */
public class ItemHandlerBlockWrapper implements IItemHandlerModifiable {

    private final WorldServer world;
    private final BlockPos pos;
    private final EnumFacing side;
    private final EnumHand hand;
    private final boolean blockUpdate;

    public ItemHandlerBlockWrapper(WorldServer world, BlockPos pos, EnumFacing side, EnumHand hand, boolean blockUpdate) {
        this.world = world;
        this.pos = pos;
        this.side = side;
        this.hand = hand;
        this.blockUpdate = blockUpdate;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot == 0 && setItemStack(stack, true).isEmpty()) {
            setItemStack(stack, false);
        }
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? getItemStack(true) : ItemStack.EMPTY;
    }

    protected ItemStack getItemStack(boolean simulate) {
        if (world.isAirBlock(pos)) {
            return ItemStack.EMPTY;
        }
        IBlockState blockState = world.getBlockState(pos);
        if (!simulate) {
            world.setBlockToAir(pos);
        }
        // TODO: improve either by acting on block breakage with tool or by silk touch
        return BlockHelpers.getItemStackFromBlockState(blockState);
    }

    protected ItemStack setItemStack(ItemStack itemStack, boolean simulate) {
        if (!itemStack.isEmpty() && itemStack.getCount() == 1) {
            Item item = itemStack.getItem();
            if (item instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) item;
                EntityPlayer player = PlayerHelpers.getFakePlayer(world);
                IBlockState blockState = itemBlock.getBlock().getStateForPlacement(world, pos, side.getOpposite(),
                        0, 0, 0, itemStack.getMetadata(), player, hand);
                if (itemBlock.canPlaceBlockOnSide(world, pos, side.getOpposite(), player, itemStack)
                        && (simulate || itemBlock
                            .placeBlockAt(itemStack, player, world, pos, side.getOpposite(), 0, 0, 0, blockState))) {
                    if (!simulate && blockUpdate) {
                        world.neighborChanged(pos, Blocks.AIR, pos);
                    }
                    return ItemStack.EMPTY;
                }
            }
        }
        return itemStack;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot != 0) {
            return stack;
        }
        ItemStack itemStack = getItemStack(true);
        if (!itemStack.isEmpty() || stack.isEmpty()) {
            return stack;
        }
        ItemStack remaining = stack.copy();
        if (!setItemStack(remaining.splitStack(1), simulate).isEmpty()) {
            return stack;
        }
        return remaining;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = getItemStack(true);
        if (amount < itemStack.getCount()) {
            return ItemStack.EMPTY;
        }
        return getItemStack(simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
