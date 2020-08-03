package org.cyclops.integratedtunnels.core.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
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
    public boolean shouldApply(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getDrops(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        return TileHelpers.getSafeTile(world, pos, ShulkerBoxTileEntity.class)
                .map(tile -> {
                    if (!tile.isEmpty()) {
                        ItemStack itemStack = ShulkerBoxBlock.getColoredItemStack(tile.getColor());
                        CompoundNBT compoundnbt = tile.saveToNbt(new CompoundNBT());
                        if (!compoundnbt.isEmpty()) {
                            itemStack.setTagInfo("BlockEntityTag", compoundnbt);
                        }

                        if (tile.hasCustomName()) {
                            itemStack.setDisplayName(tile.getName());
                        }

                        NonNullList<ItemStack> list = NonNullList.create();
                        list.add(itemStack);
                        return list;
                    }
                    return NonNullList.<ItemStack>create();
                })
                .orElseGet(NonNullList::create);
    }

    @Override
    public void breakBlock(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        TileHelpers.getSafeTile(world, pos, ShulkerBoxTileEntity.class)
                .ifPresent(LockableLootTileEntity::clear);
        blockState.getBlock().removedByPlayer(blockState, world, pos, player, false, world.getFluidState(pos));
    }
}
