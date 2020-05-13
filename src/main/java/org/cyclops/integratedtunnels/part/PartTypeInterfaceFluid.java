package org.cyclops.integratedtunnels.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.cyclops.integratedtunnels.GeneralConfig;
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
    
    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceFluidBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<PartTypeInterfaceFluid, IFluidNetwork, IFluidHandler> implements IFluidHandler {

        @Override
        protected Capability<IFluidHandler> getTargetCapability() {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
        }

        protected IFluidHandler getFluidHandler() {
            return getPositionedAddonsNetwork().getChannelExternal(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getChannel());
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            if (!isNetworkAndPositionValid()) {
                return new IFluidTankProperties[0];
            }
            disablePosition();
            IFluidTankProperties[] ret = getFluidHandler().getTankProperties();
            enablePosition();
            return ret;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getFluidHandler().fill(resource, doFill);
            enablePosition();
            return ret;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (!isNetworkAndPositionValid()) {
                return null;
            }
            disablePosition();
            FluidStack ret = getFluidHandler().drain(resource, doDrain);
            enablePosition();
            return ret;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            if (!isNetworkAndPositionValid()) {
                return null;
            }
            disablePosition();
            FluidStack ret = getFluidHandler().drain(maxDrain, doDrain);
            enablePosition();
            return ret;
        }

    }
}
