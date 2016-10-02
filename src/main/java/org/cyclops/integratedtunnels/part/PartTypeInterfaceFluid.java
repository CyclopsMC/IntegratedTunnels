package org.cyclops.integratedtunnels.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.cyclops.integrateddynamics.core.part.PartStateEmpty;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.capability.network.FluidNetworkConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

/**
 * Interface for fluid handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceFluid extends PartTypeInterfacePositionedAddon<IFluidNetwork, IFluidHandler, PartTypeInterfaceFluid, PartStateEmpty<PartTypeInterfaceFluid>> {
    public PartTypeInterfaceFluid(String name) {
        super(name);
    }

    @Override
    protected Capability<IFluidNetwork> getNetworkCapability() {
        return FluidNetworkConfig.CAPABILITY;
    }

    @Override
    protected Capability<IFluidHandler> getTargetCapability() {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    protected PartStateEmpty<PartTypeInterfaceFluid> constructDefaultState() {
        return new PartStateEmpty<PartTypeInterfaceFluid>();
    }
}
