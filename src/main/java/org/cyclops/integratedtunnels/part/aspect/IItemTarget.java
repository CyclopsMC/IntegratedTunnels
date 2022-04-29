package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.api.world.IEntityIItemTargetProxy;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.core.world.EntityIItemTargetProxies;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public interface IItemTarget extends IChanneledTarget<IItemNetwork, ItemStack> {

    public IIngredientComponentStorage<ItemStack, Integer> getItemChannel();

    public IIngredientComponentStorage<ItemStack, Integer> getStorage();

    public int getSlot();

    public IngredientPredicate<ItemStack, Integer> getItemStackMatcher();

    public PartTarget getPartTarget();

    public IAspectProperties getProperties();

    public ITunnelConnection getConnection();

    public static IItemTarget ofCapabilityProvider(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
                                                                    IngredientPredicate<ItemStack, Integer> itemStackMatcher, int slot) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        TileEntity tile = target.getPos().getWorld(true).getTileEntity(target.getPos().getBlockPos());
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ItemTargetCapabilityProvider(transfer, network, tile, target.getSide(),
                slot, itemStackMatcher, partTarget, properties, partState);
    }

    public static IItemTarget ofEntity(ITunnelTransfer transfer, PartTarget partTarget,
                                                        @Nullable Entity entity, IAspectProperties properties,
                                                        IngredientPredicate<ItemStack, Integer> itemStackMatcher, int slot) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        
        IEntityIItemTargetProxy proxy = EntityIItemTargetProxies.REGISTRY.getHandler(transfer, network, entity,
                target.getSide(), slot, itemStackMatcher, partTarget, properties, partState);
        if (proxy == null) {
            return null; // is there a special null IItemTarget?
        }
        
        return proxy.evaluate(transfer, network, entity, target.getSide(),
                slot, itemStackMatcher, partTarget, properties, partState);
    }

    public static IItemTarget ofBlock(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
                                                       IngredientPredicate<ItemStack, Integer> itemStackMatcher, int slot) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ItemTargetCapabilityProvider(transfer, network, null, target.getSide(),
                slot, itemStackMatcher, partTarget, properties, partState);
    }

    public static IItemTarget ofStorage(ITunnelTransfer transfer, INetwork network, PartTarget partTarget,
                                              IAspectProperties properties, IngredientPredicate<ItemStack, Integer> itemStackMatcher,
                                              IIngredientComponentStorage<ItemStack, Integer> storage, int slot) {
        PartPos center = partTarget.getCenter();
        PartStateRoundRobin<?> partState = IChanneledTarget.getPartState(center);
        return new ItemTargetStorage(transfer, network, storage,
                slot, itemStackMatcher, partTarget, properties, partState);
    }

}
