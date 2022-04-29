package org.cyclops.integratedtunnels.core.world;

import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.api.world.EntityInventoryTypeBase;
import org.cyclops.integratedtunnels.api.world.IEntityInventoryTypeRegistry;

import net.minecraft.util.Direction;

/**
 * Collection of {@link EntityInventoryTypeBase}s.
 * @author met4000
 */
public class EntityInventoryTypes {
    
    // TYPES NOT ADDED TO REGISTRY HERE: delegated to {@link EntityIItemTargetProxyBase}
    public static final IEntityInventoryTypeRegistry REGISTRY = IntegratedTunnels._instance.getRegistryManager()
            .getRegistry(IEntityInventoryTypeRegistry.class);
    
    // default inventory type (accesses either armor or inventory based on the side)
    public static final EntityInventoryTypeBase SIDED = new EntityInventoryType("sided");
    
    // the armor inventory of an entity
    public static final EntityInventoryTypeBase ARMOR = new EntityInventoryType("armor");
    public static final Direction ARMOR_SIDE = Direction.UP; // TODO verify
    
    // the main inventory of an entity
    public static final EntityInventoryTypeBase INVENTORY = new EntityInventoryType("inventory");
    public static final Direction INVENTORY_SIDE = Direction.NORTH; // TODO verify
    
    // TODO: ENDERINVENTORY, for players ?
    
    public static void load() {}
    
}
