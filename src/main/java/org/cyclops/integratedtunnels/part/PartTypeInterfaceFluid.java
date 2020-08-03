package org.cyclops.integratedtunnels.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.capability.network.FluidNetworkConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

import javax.annotation.Nonnull;

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
        public int getTanks() {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getFluidHandler().getTanks();
            enablePosition();
            return ret;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            if (!isNetworkAndPositionValid()) {
                return FluidStack.EMPTY;
            }
            disablePosition();
            FluidStack ret = getFluidHandler().getFluidInTank(tank);
            enablePosition();
            return ret;
        }

        @Override
        public int getTankCapacity(int tank) {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getFluidHandler().getTankCapacity(tank);
            enablePosition();
            return ret;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            if (!isNetworkAndPositionValid()) {
                return false;
            }
            disablePosition();
            boolean ret = getFluidHandler().isFluidValid(tank, stack);
            enablePosition();
            return ret;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getFluidHandler().fill(resource, action);
            enablePosition();
            return ret;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (!isNetworkAndPositionValid()) {
                return FluidStack.EMPTY;
            }
            disablePosition();
            FluidStack ret = getFluidHandler().drain(resource, action);
            enablePosition();
            return ret;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (!isNetworkAndPositionValid()) {
                return FluidStack.EMPTY;
            }
            disablePosition();
            FluidStack ret = getFluidHandler().drain(maxDrain, action);
            enablePosition();
            return ret;
        }
    }
}
