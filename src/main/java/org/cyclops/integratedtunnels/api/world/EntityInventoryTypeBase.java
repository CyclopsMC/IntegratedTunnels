package org.cyclops.integratedtunnels.api.world;

import javax.annotation.Nonnull;

/**
 * An entity inventory type.
 * For example: armor slots, inventory slots, ender chest.
 * Other mods may add extra inventory types.
 * 
 * Effectively an enum key; used as entries in a registry.
 * 
 * @author met4000
 */
public abstract class EntityInventoryTypeBase {
    
    @Nonnull
    public abstract String getName(); // TODO allow for localisation
    
    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof EntityInventoryTypeBase)) return false;
        return getName().equals(((EntityInventoryTypeBase)o).getName());
    }
    
}
