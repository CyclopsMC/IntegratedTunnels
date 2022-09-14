package org.cyclops.integratedtunnels.api.world;

import java.util.Collection;

import javax.annotation.Nullable;

import org.cyclops.cyclopscore.init.IRegistry;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.ITunnelTransfer;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

/**
 * A registry for entity IItemTarget proxies.
 * @author met4000
 */
public interface IEntityIItemTargetProxyRegistry extends IRegistry {

    // Multiple handlers may exist that return `true` for #shouldApply; the first one found should be used.
    public IEntityIItemTargetProxy register(IEntityIItemTargetProxy proxy);

    public Collection<IEntityIItemTargetProxy> getProxies();

    @Nullable
    public IEntityIItemTargetProxy getHandler(ITunnelTransfer transfer, INetwork network, @Nullable Entity entity,
            Direction side, int slot, IngredientPredicate<ItemStack, Integer> itemStackMatcher, PartTarget partTarget,
            IAspectProperties properties, @Nullable PartStateRoundRobin<?> partState);

}

