package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public interface IItemTarget extends IChanneledTarget<IItemNetwork> {

    public IIngredientComponentStorage<ItemStack, Integer> getItemChannel();

    public IIngredientComponentStorage<ItemStack, Integer> getStorage();

    public int getSlot();

    public IngredientPredicate<ItemStack, Integer> getItemStackMatcher();

    public PartTarget getPartTarget();

    public IAspectProperties getProperties();

    public ITunnelConnection getConnection();

    public static ItemTargetCapabilityProvider ofCapabilityProvider(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
                                                                    IngredientPredicate<ItemStack, Integer> itemStackMatcher, int slot) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        TileEntity tile = target.getPos().getWorld().getTileEntity(target.getPos().getBlockPos());
        PartStateRoundRobin<?> partState = (PartStateRoundRobin<?>) PartHelpers.getPart(center).getState();
        return new ItemTargetCapabilityProvider(transfer, network, tile, target.getSide(),
                slot, itemStackMatcher, partTarget, properties, partState);
    }

    public static ItemTargetCapabilityProvider ofEntity(ITunnelTransfer transfer, PartTarget partTarget,
                                                        @Nullable Entity entity, IAspectProperties properties,
                                                        IngredientPredicate<ItemStack, Integer> itemStackMatcher, int slot) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = (PartStateRoundRobin<?>) PartHelpers.getPart(center).getState();
        return new ItemTargetCapabilityProvider(transfer, network, entity, target.getSide(),
                slot, itemStackMatcher, partTarget, properties, partState);
    }

    public static ItemTargetCapabilityProvider ofBlock(ITunnelTransfer transfer, PartTarget partTarget, IAspectProperties properties,
                                                       IngredientPredicate<ItemStack, Integer> itemStackMatcher, int slot) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = (PartStateRoundRobin<?>) PartHelpers.getPart(center).getState();
        return new ItemTargetCapabilityProvider(transfer, network, null, target.getSide(),
                slot, itemStackMatcher, partTarget, properties, partState);
    }

    public static ItemTargetStorage ofStorage(ITunnelTransfer transfer, INetwork network, PartTarget partTarget,
                                              IAspectProperties properties, IngredientPredicate<ItemStack, Integer> itemStackMatcher,
                                              IIngredientComponentStorage<ItemStack, Integer> storage, int slot) {
        PartPos center = partTarget.getCenter();
        PartStateRoundRobin<?> partState = (PartStateRoundRobin<?>) PartHelpers.getPart(center).getState();
        return new ItemTargetStorage(transfer, network, storage,
                slot, itemStackMatcher, partTarget, properties, partState);
    }

}
