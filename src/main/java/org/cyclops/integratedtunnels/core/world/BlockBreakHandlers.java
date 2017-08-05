package org.cyclops.integratedtunnels.core.world;

import net.minecraft.init.Blocks;
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
        REGISTRY.register(Blocks.WHITE_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.ORANGE_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.MAGENTA_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.LIGHT_BLUE_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.YELLOW_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.LIME_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.PINK_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.GRAY_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.SILVER_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.CYAN_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.PURPLE_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.BLUE_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.BROWN_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.GREEN_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.RED_SHULKER_BOX, blockBreakHandlerShulkerBox);
        REGISTRY.register(Blocks.BLACK_SHULKER_BOX, blockBreakHandlerShulkerBox);
    }

}
