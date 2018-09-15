package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.core.IngredientPredicate;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public interface IFluidTarget extends IChanneledTarget<IFluidNetwork> {

    public IIngredientComponentStorage<FluidStack, Integer> getFluidChannel();

    public IIngredientComponentStorage<FluidStack, Integer> getStorage();

    public IngredientPredicate<FluidStack, Integer> getFluidStackMatcher();

    public PartTarget getPartTarget();

    public IAspectProperties getProperties();

    public int getConnectionHash();

    public static FluidTargetCapabilityProvider ofCapabilityProvider(int transferHash, PartTarget partTarget, IAspectProperties properties,
                                                                     IngredientPredicate<FluidStack, Integer> fluidStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        TileEntity tile = target.getPos().getWorld().getTileEntity(target.getPos().getBlockPos());
        PartStateRoundRobin<?> partState = (PartStateRoundRobin<?>) PartHelpers.getPart(center).getState();
        return new FluidTargetCapabilityProvider(transferHash, network, tile, target.getSide(),
                fluidStackMatcher, partTarget, properties, partState);
    }

    public static FluidTargetCapabilityProvider ofEntity(int transferHash, PartTarget partTarget, @Nullable Entity entity,
                                                         IAspectProperties properties,
                                                         IngredientPredicate<FluidStack, Integer> fluidStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = (PartStateRoundRobin<?>) PartHelpers.getPart(center).getState();
        return new FluidTargetCapabilityProvider(transferHash, network, entity, target.getSide(),
                fluidStackMatcher, partTarget, properties, partState);
    }

    public static FluidTargetCapabilityProvider ofBlock(int transferHash, PartTarget partTarget, IAspectProperties properties,
                                                        IngredientPredicate<FluidStack, Integer> fluidStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        PartStateRoundRobin<?> partState = (PartStateRoundRobin<?>) PartHelpers.getPart(center).getState();
        return new FluidTargetCapabilityProvider(transferHash, network, null, target.getSide(),
                fluidStackMatcher, partTarget, properties, partState);
    }

    public static FluidTargetStorage ofStorage(int transferHash, INetwork network, PartTarget partTarget,
                                               IAspectProperties properties,
                                               IngredientPredicate<FluidStack, Integer> fluidStackMatcher,
                                               IIngredientComponentStorage<FluidStack, Integer> storage) {
        PartPos center = partTarget.getCenter();
        PartStateRoundRobin<?> partState = (PartStateRoundRobin<?>) PartHelpers.getPart(center).getState();
        return new FluidTargetStorage(transferHash, network, storage,
                fluidStackMatcher, partTarget, properties, partState);
    }

}
