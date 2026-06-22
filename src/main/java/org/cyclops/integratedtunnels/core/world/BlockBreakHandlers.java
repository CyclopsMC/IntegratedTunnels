package org.cyclops.integratedtunnels.core.world;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandler;
import org.cyclops.integratedtunnels.api.world.IBlockBreakHandlerRegistry;

/**
 * Collection of block break handlers
 * @author rubensworks
 */
public class BlockBreakHandlers {

    public static final IBlockBreakHandlerRegistry REGISTRY = IntegratedTunnels._instance.getRegistryManager()
            .getRegistry(IBlockBreakHandlerRegistry.class);

    public static void load() {
        IBlockBreakHandler blockBreakHandlerShulkerBox = new BlockBreakHandlerShulkerBox();
        REGISTRY.register(Blocks.SHULKER_BOX, blockBreakHandlerShulkerBox);
        for (Block block : Blocks.DYED_SHULKER_BOX.asList()) {
            REGISTRY.register(block, blockBreakHandlerShulkerBox);
        }
    }

}
