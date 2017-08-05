package org.cyclops.integratedtunnels.api.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An interface for custom block breaking actions.
 * @author rubensworks
 */
public interface IBlockBreakHandler {

    /**
     * If this can handle the given block state.
     * @param blockState The block state.
     * @param world The world.
     * @param pos The block position.
     * @param player The breaking player.
     * @return If this can handle the given block state.
     */
    public boolean shouldApply(IBlockState blockState, World world, BlockPos pos, EntityPlayer player);

    /**
     * Get the dropping items of the given block.
     * @param blockState The block state.
     * @param world The world.
     * @param pos The block position.
     * @param player The breaking player.
     * @return A list of itemstacks where each element must be removable.
     */
    public NonNullList<ItemStack> getDrops(IBlockState blockState, World world, BlockPos pos, EntityPlayer player);

    /**
     * Break the given block.
     * @param blockState The block state.
     * @param world The world.
     * @param pos The block position.
     * @param player The breaking player.
     */
    public void breakBlock(IBlockState blockState, World world, BlockPos pos, EntityPlayer player);

}
