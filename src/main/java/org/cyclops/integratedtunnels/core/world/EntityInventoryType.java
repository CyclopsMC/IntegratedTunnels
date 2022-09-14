package org.cyclops.integratedtunnels.core.world;

import javax.annotation.Nonnull;

import org.cyclops.integratedtunnels.api.world.EntityInventoryTypeBase;

/**
 * Implements {@link EntityInventoryTypeBase}
 * @author met4000
 */
public class EntityInventoryType extends EntityInventoryTypeBase {
    
    private final String name;
    
    public EntityInventoryType(String name) {
        this.name = name;
    };
    
    public static EntityInventoryType fromString(String name) {
        return new EntityInventoryType(name);
    }

    @Override @Nonnull
    public String getName() {
        return name;
    }

}
