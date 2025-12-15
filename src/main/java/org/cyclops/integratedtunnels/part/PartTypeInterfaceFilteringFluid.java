package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddonFiltering;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * Interface for filtering fluid handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceFilteringFluid extends PartTypeInterfacePositionedAddonFiltering<IFluidNetwork, ResourceHandler<FluidResource>, PartTypeInterfaceFilteringFluid, PartTypeInterfaceFilteringFluid.State> {
    public PartTypeInterfaceFilteringFluid(String name) {
        super(name);
        AspectRegistry.getInstance().register(this, Lists.<IAspect>newArrayList(
                TunnelAspects.Write.FluidFilter.BOOLEAN_SET_FILTER,
                TunnelAspects.Write.FluidFilter.FLUIDSTACK_SET_FILTER,
                TunnelAspects.Write.FluidFilter.LIST_SET_FILTER,
                TunnelAspects.Write.FluidFilter.PREDICATE_SET_FILTER,
                TunnelAspects.Write.FluidFilter.NBT_SET_FILTER
        ));
    }

    @Override
    public NetworkCapability<IFluidNetwork> getNetworkCapability() {
        return Capabilities.FluidNetwork.NETWORK;
    }

    @Override
    public PartCapability<ResourceHandler<FluidResource>> getPartCapability() {
        return Capabilities.Fluid.PART;
    }

    @Override
    public BlockCapability<ResourceHandler<FluidResource>, Direction> getBlockCapability() {
        return net.neoforged.neoforge.capabilities.Capabilities.Fluid.BLOCK;
    }

    @Override
    protected PartTypeInterfaceFilteringFluid.State constructDefaultState() {
        return new PartTypeInterfaceFilteringFluid.State(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceFluidBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddonFiltering.State<IFluidNetwork, ResourceHandler<FluidResource>, PartTypeInterfaceFilteringFluid, PartTypeInterfaceFilteringFluid.State> {

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        public PartCapability<ResourceHandler<FluidResource>> getTargetCapability() {
            return Capabilities.Fluid.PART;
        }

        @Override
        public ResourceHandler<FluidResource> getCapabilityInstance() {
            return new PartTypeInterfaceFluid.FluidHandler(this);
        }
    }
}
