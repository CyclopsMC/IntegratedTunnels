package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.capability.network.FluidNetworkConfig;
import org.cyclops.integratedtunnels.core.IngredientPredicate;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class FluidTargetCapabilityProvider extends ChanneledTargetCapabilityProvider<IFluidNetwork, FluidStack, Integer>
        implements IFluidTarget {

    private final int connectionHash;
    private final PartTarget partTarget;
    private final IngredientPredicate<FluidStack, Integer> fluidStackMatcher;
    private final IAspectProperties properties;

    public FluidTargetCapabilityProvider(int transferHash, INetwork network, @Nullable ICapabilityProvider capabilityProvider,
                                         EnumFacing side, IngredientPredicate<FluidStack, Integer> fluidStackMatcher,
                                         PartTarget partTarget, IAspectProperties properties,
                                         PartStateRoundRobin<?> partState) {
        super(capabilityProvider, side, network.getCapability(FluidNetworkConfig.CAPABILITY), partState,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue());
        int storagePosHash = partTarget.getTarget().hashCode();
        this.connectionHash = transferHash << 4 + storagePosHash ^ System.identityHashCode(getChanneledNetwork());
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
    public IngredientPredicate<FluidStack, Integer> getFluidStackMatcher() {
        return fluidStackMatcher;
    }

    @Override
    public IAspectProperties getProperties() {
        return properties;
    }

    @Override
    public int getConnectionHash() {
        return connectionHash;
    }

    @Override
    protected IngredientComponent<FluidStack, Integer> getComponent() {
        return IngredientComponent.FLUIDSTACK;
    }
}
