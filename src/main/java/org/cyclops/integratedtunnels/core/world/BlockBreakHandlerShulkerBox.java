package org.cyclops.integratedtunnels.core.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandler;

/**
 * A block break handler for shulker boxes.
 * @author rubensworks
 */
public class BlockBreakHandlerShulkerBox implements IBlockBreakHandler {
    @Override
    public boolean shouldApply(IBlockState blockState, World world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getDrops(IBlockState blockState, World world, BlockPos pos, EntityPlayer player) {
        TileEntityShulkerBox tile = TileHelpers.getSafeTile(world, pos, TileEntityShulkerBox.class);
        if (tile != null) {
            if (!tile.isCleared() && tile.shouldDrop()) {
                ItemStack itemStack = new ItemStack(Item.getItemFromBlock(blockState.getBlock()));
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound.setTag("BlockEntityTag", tile.saveToNbt(nbttagcompound1));
                itemStack.setTagCompound(nbttagcompound);

                if (tile.hasCustomName()) {
                    itemStack.setStackDisplayName(tile.getName());
                    tile.setCustomName("");
                }

                NonNullList<ItemStack> list = NonNullList.create();
                list.add(itemStack);
                return list;
            }
        }

        return NonNullList.create();
    }

    @Override
    public void breakBlock(IBlockState blockState, World world, BlockPos pos, EntityPlayer player) {
        TileEntityShulkerBox tile = TileHelpers.getSafeTile(world, pos, TileEntityShulkerBox.class);
        if (tile != null) {
            tile.clear();
        }
        blockState.getBlock().removedByPlayer(blockState, world, pos, player, false);
    }
}
