package org.cyclops.integratedtunnels.core.world;

import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.api.world.IEntityIItemTargetProxyRegistry;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.IItemTarget;
import org.cyclops.integratedtunnels.part.aspect.ITunnelTransfer;
import org.cyclops.integratedtunnels.part.aspect.ItemTargetCapabilityProvider;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

/**
 * Collection of Entity IItemTarget proxies.
 * @author met4000
 */
public class EntityIItemTargetProxies {
    
    public static final IEntityIItemTargetProxyRegistry REGISTRY = IntegratedTunnels._instance.getRegistryManager()
            .getRegistry(IEntityIItemTargetProxyRegistry.class);
    
    public static void load() {
        REGISTRY.register(new EntityIItemTargetProxyInventoryTypeBase(EntityInventoryTypes.SIDED) {
            @Override
            public IItemTarget evaluate(ITunnelTransfer transfer, INetwork network, Entity entity, Direction side, int slot,
                    IngredientPredicate<ItemStack, Integer> itemStackMatcher, PartTarget partTarget,
                    IAspectProperties properties, PartStateRoundRobin<?> partState) {
                return new ItemTargetCapabilityProvider(transfer, network, entity, side,
                        slot, itemStackMatcher, partTarget, properties, partState);
            }
        });
        
        REGISTRY.register(new EntityIItemTargetProxySided(EntityInventoryTypes.ARMOR, Reference.ENTITY_ARMOR_SIDE));
        REGISTRY.register(new EntityIItemTargetProxySided(EntityInventoryTypes.INVENTORY, Reference.ENTITY_INVENTORY_SIDE));
    }
    
}
