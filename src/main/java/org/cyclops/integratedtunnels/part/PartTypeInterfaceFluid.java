package org.cyclops.integratedtunnels.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.capability.network.FluidNetworkConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

import javax.annotation.Nullable;

/**
 * Interface for fluid handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceFluid extends PartTypeInterfacePositionedAddon<IFluidNetwork, IFluidHandler, PartTypeInterfaceFluid, PartTypeInterfaceFluid.State> {
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
    protected PartTypeInterfaceFluid.State constructDefaultState() {
        return new PartTypeInterfaceFluid.State();
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<PartTypeInterfaceFluid, IFluidNetwork, IFluidHandler> implements IFluidHandler {

        @Override
        protected Capability<IFluidHandler> getTargetCapability() {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            disablePosition();
            IFluidTankProperties[] ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().getTankProperties() : new IFluidTankProperties[0];
            enablePosition();
            return ret;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            disablePosition();
            int ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().fill(resource, doFill) : 0;
            enablePosition();
            return ret;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            disablePosition();
            FluidStack ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().drain(resource, doDrain) : null;
            enablePosition();
            return ret;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            disablePosition();
            FluidStack ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().drain(maxDrain, doDrain) : null;
            enablePosition();
            return ret;
        }

    }
}
