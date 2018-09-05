package org.cyclops.integratedtunnels.part.aspect;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.capability.network.FluidNetworkConfig;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import java.util.function.Predicate;

/**
 * @author rubensworks
 */
public interface IFluidTarget extends IChanneledTarget<IFluidNetwork> {

    public PartTarget getPartTarget();

    public IFluidHandler getFluidHandler();

    public IIngredientComponentStorage<FluidStack, Integer> getFluidChannel();

    public IFluidHandler getFluidChannelExternal();

    public int getAmount();

    public boolean isExactAmount();

    public Predicate<FluidStack> getFluidStackMatcher();

    public IAspectProperties getProperties();

    public static FluidTargetCapabilityProvider ofCapabilityProvider(PartTarget partTarget, IAspectProperties properties, int amount,
                                                                     Predicate<FluidStack> fluidStackMatcher) {
        PartPos center = partTarget.getCenter();
        PartPos target = partTarget.getTarget();
        INetwork network = IChanneledTarget.getNetworkChecked(center);
        IFluidHandler fluidHandler = FluidUtil.getFluidHandler(target.getPos().getWorld(), target.getPos().getBlockPos(), target.getSide());
        PartStateRoundRobin<?> partState = (PartStateRoundRobin<?>) PartHelpers.getPart(center).getState();
        return new FluidTargetCapabilityProvider(partTarget, network.getCapability(FluidNetworkConfig.CAPABILITY), fluidHandler,
                amount, fluidStackMatcher, properties, partState);
    }

}
