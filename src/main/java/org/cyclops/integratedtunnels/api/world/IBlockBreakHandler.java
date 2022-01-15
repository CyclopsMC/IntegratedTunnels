package org.cyclops.integratedtunnels.api.world;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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
    public boolean shouldApply(BlockState blockState, Level world, BlockPos pos, Player player);

    /**
     * Get the dropping items of the given block.
     * @param blockState The block state.
     * @param world The world.
     * @param pos The block position.
     * @param player The breaking player.
     * @return A list of itemstacks where each element must be removable.
     */
    public NonNullList<ItemStack> getDrops(BlockState blockState, Level world, BlockPos pos, Player player);

    /**
     * Break the given block.
     * @param blockState The block state.
     * @param world The world.
     * @param pos The block position.
     * @param player The breaking player.
     */
    public void breakBlock(BlockState blockState, Level world, BlockPos pos, Player player);

}
