package org.cyclops.integratedtunnels.part.aspect;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import java.util.function.Predicate;

/**
 * @author rubensworks
 */
public class FluidTargetCapabilityProvider extends ChanneledTarget<IFluidNetwork> implements IFluidTarget {

    private final PartTarget partTarget;
    private final IFluidHandler fluidHandler;
    private final int amount;
    private final Predicate<FluidStack> fluidStackMatcher;
    private final IAspectProperties properties;
    private final boolean exactAmount;

    public FluidTargetCapabilityProvider(PartTarget partTarget, IFluidNetwork fluidNetwork, IFluidHandler fluidHandler,
                                         int amount, Predicate<FluidStack> fluidStackMatcher, IAspectProperties properties,
                                         PartStateRoundRobin<?> partState) {
        super(fluidNetwork, partState, properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue());
        this.partTarget = partTarget;
        this.fluidHandler = fluidHandler;
        this.amount = amount;
        this.exactAmount = properties.getValue(TunnelAspectWriteBuilders.PROP_EXACTAMOUNT).getRawValue();
        this.fluidStackMatcher = fluidStackMatcher;
        this.properties = properties;
    }

    @Override
    public PartTarget getPartTarget() {
        return partTarget;
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public IIngredientComponentStorage<FluidStack, Integer> getFluidChannel() {
        return getChanneledNetwork().getChannel(getChannel());
    }

    @Override
    public IFluidHandler getFluidChannelExternal() {
        return getChanneledNetwork().getChannelExternal(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getChannel());
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isExactAmount() {
        return exactAmount;
    }

    @Override
    public Predicate<FluidStack> getFluidStackMatcher() {
        return fluidStackMatcher;
    }

    @Override
    public IAspectProperties getProperties() {
        return properties;
    }
}
