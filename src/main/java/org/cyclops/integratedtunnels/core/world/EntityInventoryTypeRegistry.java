package org.cyclops.integratedtunnels.core.world;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.cyclops.integratedtunnels.api.world.EntityInventoryTypeBase;
import org.cyclops.integratedtunnels.api.world.IEntityInventoryTypeRegistry;

/**
 * Implements {@link IEntityInventoryTypeRegistry}
 * @author met4000
 */
public class EntityInventoryTypeRegistry implements IEntityInventoryTypeRegistry {

    private static EntityInventoryTypeRegistry INSTANCE = new EntityInventoryTypeRegistry();
    
    // linked list used for constant append time
    private final Collection<EntityInventoryTypeBase> types = new LinkedList<EntityInventoryTypeBase>();

    private EntityInventoryTypeRegistry() {}
    
    public static EntityInventoryTypeRegistry getInstance() {
        return INSTANCE;
    }
    
    @Override
    public EntityInventoryTypeBase register(EntityInventoryTypeBase type) {
        types.add(type);
        return type;
    }

    @Override
    public Collection<EntityInventoryTypeBase> getTypes() {
        return Collections.unmodifiableCollection(types);
    }

}
