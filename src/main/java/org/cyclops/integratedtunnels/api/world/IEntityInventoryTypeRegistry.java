package org.cyclops.integratedtunnels.api.world;

import java.util.Collection;

import org.cyclops.cyclopscore.init.IRegistry;

/**
 * Collection of IEntityInventoryTypes.
 * @author met4000
 */
public interface IEntityInventoryTypeRegistry extends IRegistry {
    
    public EntityInventoryTypeBase register(EntityInventoryTypeBase type);

    public Collection<EntityInventoryTypeBase> getTypes();
    
}
