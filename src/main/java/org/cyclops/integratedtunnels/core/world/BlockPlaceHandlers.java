package org.cyclops.integratedtunnels.core.world;

import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.api.world.IBlockPlaceHandlerRegistry;

/**
 * Collection of block place handlers
 * @author rubensworks
 */
public class BlockPlaceHandlers {

    public static final IBlockPlaceHandlerRegistry REGISTRY = IntegratedTunnels._instance.getRegistryManager()
            .getRegistry(IBlockPlaceHandlerRegistry.class);

    public static void load() {}

}
