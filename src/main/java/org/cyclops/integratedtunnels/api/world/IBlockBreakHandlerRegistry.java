package org.cyclops.integratedtunnels.api.world;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.cyclops.cyclopscore.init.IRegistry;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A registry for block breaking actions.
 * @author rubensworks
 */
public interface IBlockBreakHandlerRegistry extends IRegistry {

    /**
     * Add a block breaking handler.
     * Multiple handlers can exist for a block.
     * @param block A block.
     * @param breakAction A handler.
     * @return The registered handler.
     */
    public IBlockBreakHandler register(Block block, IBlockBreakHandler breakAction);

    /**
     * @return All registered block breaking handlers.
     */
    public Collection<IBlockBreakHandler> getHandlers();

    /**
     * @param block A block.
     * @return All registered block breaking handlers for the given block.
     */
    public Collection<IBlockBreakHandler> getHandlers(Block block);

    /**
     * Get the first possible block breaking handler for the given block state.
     * @param blockState The block state.
     * @param world The world.
     * @param pos The block position.
     * @param player The breaking player.
     * @return A block breaking handler or null.
     */
    @Nullable
    public IBlockBreakHandler getHandler(BlockState blockState, Level world, BlockPos pos, Player player);

}
