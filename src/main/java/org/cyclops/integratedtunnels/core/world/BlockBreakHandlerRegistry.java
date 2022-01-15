package org.cyclops.integratedtunnels.core.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandler;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandlerRegistry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of {@link IBlockBreakHandlerRegistry}.
 * @author rubensworks
 */
public class BlockBreakHandlerRegistry implements IBlockBreakHandlerRegistry {

    private static BlockBreakHandlerRegistry INSTANCE = new BlockBreakHandlerRegistry();

    private final Multimap<Block, IBlockBreakHandler> handlers = Multimaps.newSetMultimap(Maps.<Block, Collection<IBlockBreakHandler>>newIdentityHashMap(), Sets::newIdentityHashSet);

    private BlockBreakHandlerRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static BlockBreakHandlerRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public IBlockBreakHandler register(Block block, IBlockBreakHandler breakAction) {
        handlers.put(block, breakAction);
        return breakAction;
    }

    @Override
    public Collection<IBlockBreakHandler> getHandlers() {
        return Collections.unmodifiableCollection(handlers.values());
    }

    @Override
    public Collection<IBlockBreakHandler> getHandlers(Block block) {
        return Collections.unmodifiableCollection(handlers.get(block));
    }

    @Nullable
    @Override
    public IBlockBreakHandler getHandler(BlockState blockState, Level world, BlockPos pos, Player player) {
        for (IBlockBreakHandler breakHandler : getHandlers(blockState.getBlock())) {
            if (breakHandler.shouldApply(blockState, world, pos, player)) {
                return breakHandler;
            }
        }
        return null;
    }
}
