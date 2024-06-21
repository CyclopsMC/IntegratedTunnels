package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.capability.ICapabilityGetter;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageSlotted;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class FluidTargetCapabilityProvider extends ChanneledTargetCapabilityProvider<IFluidHandler, IFluidNetwork, FluidStack, Integer>
        implements IFluidTarget {

    private final ITunnelConnection connection;
    private final PartTarget partTarget;
    private final IngredientPredicate<FluidStack, Integer> fluidStackMatcher;
    private final IAspectProperties properties;

    public FluidTargetCapabilityProvider(ITunnelTransfer transfer, INetwork network, Class<?> capabilityType, @Nullable ICapabilityGetter<Direction> capabilityGetter,
                                         Object capabilityProvider,
                                         Direction side, IngredientPredicate<FluidStack, Integer> fluidStackMatcher,
                                         PartTarget partTarget, IAspectProperties properties,
                                         @Nullable PartStateRoundRobin<?> partState) {
        super(network, capabilityType, capabilityGetter, side, network.getCapability(Capabilities.FluidNetwork.NETWORK).orElse(null), partState,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO).getRawValue());
        this.connection = new TunnelConnectionPositionedNetworkCapabilityProvider(network, getChannel(), partTarget.getTarget(), transfer, capabilityProvider);
        this.fluidStackMatcher = fluidStackMatcher;
        this.partTarget = partTarget;
        this.properties = properties;
    }

    @Override
    public PartTarget getPartTarget() {
        return partTarget;
    }

    @Override
    public IIngredientComponentStorage<FluidStack, Integer> getFluidChannel() {
        return getChanneledNetwork().getChannel(getChannel());
    }

    @Override
    public IIngredientComponentStorageSlotted<FluidStack, Integer> getFluidChannelSlotted() {
        return getChanneledNetwork().getChannelSlotted(getChannel());
    }

    @Override
    public IngredientPredicate<FluidStack, Integer> getFluidStackMatcher() {
        return fluidStackMatcher;
    }

    @Override
    public IAspectProperties getProperties() {
        return properties;
    }

    @Override
    public ITunnelConnection getConnection() {
        return connection;
    }

    @Override
    protected IngredientComponent<FluidStack, Integer> getComponent() {
        return IngredientComponent.FLUIDSTACK;
    }
}
