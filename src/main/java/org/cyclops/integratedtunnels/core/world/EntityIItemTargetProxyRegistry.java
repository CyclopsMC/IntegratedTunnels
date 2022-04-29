package org.cyclops.integratedtunnels.core.world;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import javax.annotation.Nullable;

import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.api.world.IEntityIItemTargetProxy;
import org.cyclops.integratedtunnels.api.world.IEntityIItemTargetProxyRegistry;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.ITunnelTransfer;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

/**
 * Implementation of {@link IEntityIItemTargetProxyRegistry}
 * @author met4000
 */
public class EntityIItemTargetProxyRegistry implements IEntityIItemTargetProxyRegistry {
    
    private static EntityIItemTargetProxyRegistry INSTANCE = new EntityIItemTargetProxyRegistry();
    
    // linked list used for constant append time
    private final Collection<IEntityIItemTargetProxy> proxies = new LinkedList<IEntityIItemTargetProxy>();

    private EntityIItemTargetProxyRegistry() {}
    
    public static EntityIItemTargetProxyRegistry getInstance() {
        return INSTANCE;
    }
    
    @Override
    public IEntityIItemTargetProxy register(IEntityIItemTargetProxy proxy) {
        proxies.add(proxy);
        return proxy;
    }

    @Override
    public Collection<IEntityIItemTargetProxy> getProxies() {
        return Collections.unmodifiableCollection(proxies);
    }
    
    @Nullable
    @Override
    public IEntityIItemTargetProxy getHandler(ITunnelTransfer transfer, INetwork network, @Nullable Entity entity,
            Direction side, int slot, IngredientPredicate<ItemStack, Integer> itemStackMatcher, PartTarget partTarget,
            IAspectProperties properties, @Nullable PartStateRoundRobin<?> partState) {
        for (IEntityIItemTargetProxy proxy : getProxies()) {
            if (proxy.shouldApply(transfer, network, entity, side, slot, itemStackMatcher, partTarget, properties, partState)) {
                return proxy;
            }
        }
        return null;
    }

}
