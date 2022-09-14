package org.cyclops.integratedtunnels.api.world;

import javax.annotation.Nullable;

import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.IItemTarget;
import org.cyclops.integratedtunnels.part.aspect.ITunnelTransfer;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

/**
 * Proxy that can produce an IItemTarget for an Entity.
 * @author met4000
 */
public interface IEntityIItemTargetProxy {
    
    public boolean shouldApply(ITunnelTransfer transfer, INetwork network,
            @Nullable Entity entity, Direction side, int slot,
            IngredientPredicate<ItemStack, Integer> itemStackMatcher, PartTarget partTarget,
            IAspectProperties properties, @Nullable PartStateRoundRobin<?> partState);
    
    public IItemTarget evaluate(ITunnelTransfer transfer, INetwork network,
            @Nullable Entity entity, Direction side, int slot,
            IngredientPredicate<ItemStack, Integer> itemStackMatcher, PartTarget partTarget,
            IAspectProperties properties, @Nullable PartStateRoundRobin<?> partState);
    
}
