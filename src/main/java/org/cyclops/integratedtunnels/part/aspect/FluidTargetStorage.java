package org.cyclops.integratedtunnels.part.aspect;

import net.neoforged.neoforge.fluids.FluidStack;
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
public class FluidTargetStorage extends ChanneledTarget<IFluidNetwork, FluidStack> implements IFluidTarget {

    private final ITunnelConnection connection;
    private final IIngredientComponentStorage<FluidStack, Integer> storage;
    private final IngredientPredicate<FluidStack, Integer> fluidStackMatcher;
    private final PartTarget partTarget;
    private final IAspectProperties properties;

    public FluidTargetStorage(ITunnelTransfer transfer, INetwork network,
                              IIngredientComponentStorage<FluidStack, Integer> storage,
                              IngredientPredicate<FluidStack, Integer> fluidStackMatcher, PartTarget partTarget,
                              IAspectProperties properties, @Nullable PartStateRoundRobin<?> partState) {
        super(network, network.getCapability(Capabilities.FluidNetwork.NETWORK).orElse(null), partState,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO).getRawValue());
        this.connection = new TunnelConnectionPositionedNetwork(network, getChannel(), partTarget.getTarget(), transfer);
        this.storage = storage;
        this.fluidStackMatcher = fluidStackMatcher;
        this.partTarget = partTarget;
        this.properties = properties;
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
    public boolean hasValidTarget() {
        return storage != null;
    }

    @Override
    public IIngredientComponentStorage<FluidStack, Integer> getStorage() {
        return storage;
    }

    @Override
    public IngredientPredicate<FluidStack, Integer> getFluidStackMatcher() {
        return fluidStackMatcher;
    }

    @Override
    public PartTarget getPartTarget() {
        return partTarget;
    }

    @Override
    public IAspectProperties getProperties() {
        return properties;
    }

    @Override
    public ITunnelConnection getConnection() {
        return connection;
    }
}
