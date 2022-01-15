package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Lists;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.cyclops.integrateddynamics.api.part.aspect.IAspect;
import org.cyclops.integrateddynamics.core.part.aspect.AspectRegistry;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.capability.network.FluidNetworkConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddonFiltering;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

/**
 * Interface for filtering fluid handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceFilteringFluid extends PartTypeInterfacePositionedAddonFiltering<IFluidNetwork, IFluidHandler, PartTypeInterfaceFilteringFluid, PartTypeInterfaceFilteringFluid.State> {
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
    public Capability<IFluidNetwork> getNetworkCapability() {
        return FluidNetworkConfig.CAPABILITY;
    }

    @Override
    public Capability<IFluidHandler> getTargetCapability() {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    protected PartTypeInterfaceFilteringFluid.State constructDefaultState() {
        return new PartTypeInterfaceFilteringFluid.State(Aspects.REGISTRY.getWriteAspects(this).size());
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceFluidBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddonFiltering.State<IFluidNetwork, IFluidHandler, PartTypeInterfaceFilteringFluid, PartTypeInterfaceFilteringFluid.State> {

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        public Capability<IFluidHandler> getTargetCapability() {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
        }

        @Override
        public IFluidHandler getCapabilityInstance() {
            return new PartTypeInterfaceFluid.FluidHandler(this);
        }
    }
}
