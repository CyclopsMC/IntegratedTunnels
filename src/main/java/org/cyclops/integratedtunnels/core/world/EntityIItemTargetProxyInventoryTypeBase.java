package org.cyclops.integratedtunnels.core.world;

import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.api.world.EntityInventoryTypeBase;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.ITunnelTransfer;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders.World;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

/**
 * An IItemTarget proxy to access a sided entity.
 * @author met4000
 */
public abstract class EntityIItemTargetProxyInventoryTypeBase extends EntityIItemTargetProxyBase {

    public EntityIItemTargetProxyInventoryTypeBase(EntityInventoryTypeBase inventoryType) {
        super(inventoryType);
    }

    @Override
    public boolean shouldApply(ITunnelTransfer transfer, INetwork network, Entity entity, Direction side, int slot,
            IngredientPredicate<ItemStack, Integer> itemStackMatcher, PartTarget partTarget,
            IAspectProperties properties, PartStateRoundRobin<?> partState) {
        return inventoryType.getName().equals(properties.getValue(World.PROPERTY_ENTITYINVENTORY).getRawValue());
    }

}
