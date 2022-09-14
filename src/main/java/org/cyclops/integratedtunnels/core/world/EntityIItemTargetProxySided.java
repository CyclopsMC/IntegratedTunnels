package org.cyclops.integratedtunnels.core.world;

import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.api.world.EntityInventoryTypeBase;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.IItemTarget;
import org.cyclops.integratedtunnels.part.aspect.ITunnelTransfer;
import org.cyclops.integratedtunnels.part.aspect.ItemTargetCapabilityProvider;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders.World;

import lombok.NonNull;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

/**
 * An IItemTarget proxy to access a sided entity.
 * Uses the specified side when interacting with the capability provider.
 * @author met4000
 */
public class EntityIItemTargetProxySided extends EntityIItemTargetProxyInventoryTypeBase {
    
    @NonNull
    protected final Direction side;
    
    public EntityIItemTargetProxySided(EntityInventoryTypeBase inventoryType, Direction side) {
        super(inventoryType);
        this.side = side;
    }

    @Override
    public IItemTarget evaluate(ITunnelTransfer transfer, INetwork network, Entity entity, Direction side, int slot,
            IngredientPredicate<ItemStack, Integer> itemStackMatcher, PartTarget partTarget,
            IAspectProperties properties, PartStateRoundRobin<?> partState) {
        return new ItemTargetCapabilityProvider(transfer, network, entity, this.side,
                slot, itemStackMatcher, partTarget, properties, partState);
    }

}
