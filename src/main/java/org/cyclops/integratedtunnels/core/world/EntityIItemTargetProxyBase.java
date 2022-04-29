package org.cyclops.integratedtunnels.core.world;

import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.api.world.EntityInventoryTypeBase;
import org.cyclops.integratedtunnels.api.world.IEntityIItemTargetProxy;
import org.cyclops.integratedtunnels.api.world.IEntityInventoryTypeRegistry;

/**
 * Registers the IEntityInventoryType with the EntityInventoryTypeRegistry.
 * @author met4000
 */
public abstract class EntityIItemTargetProxyBase implements IEntityIItemTargetProxy {
    
    private static final IEntityInventoryTypeRegistry REGISTRY = IntegratedTunnels._instance.getRegistryManager()
            .getRegistry(IEntityInventoryTypeRegistry.class);
    
    protected final EntityInventoryTypeBase inventoryType;

    public EntityIItemTargetProxyBase(EntityInventoryTypeBase inventoryType) {
        this.inventoryType = REGISTRY.register(inventoryType);
    }

}
