package org.cyclops.integratedtunnels.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.capability.network.FluidNetworkConfig;
import org.cyclops.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.cyclops.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon.IState;

/**
 * Interface for fluid handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceFluid extends PartTypeInterfacePositionedAddon<IFluidNetwork, IFluidHandler, PartTypeInterfaceFluid, PartTypeInterfaceFluid.State> {
    public PartTypeInterfaceFluid(String name) {
        super(name);
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
    protected PartTypeInterfaceFluid.State constructDefaultState() {
        return new PartTypeInterfaceFluid.State();
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceFluidBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<IFluidNetwork, IFluidHandler, PartTypeInterfaceFluid, PartTypeInterfaceFluid.State> {

        @Override
        public Capability<IFluidHandler> getTargetCapability() {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
        }

        @Override
        public IFluidHandler getCapabilityInstance() {
            return new PartTypeInterfaceFluid.FluidHandler(this);
        }
    }

    public static class FluidHandler implements IFluidHandler {
        private final IPartTypeInterfacePositionedAddon.IState<IFluidNetwork, IFluidHandler, ?, ?> state;

        public FluidHandler(IState<IFluidNetwork, IFluidHandler, ?, ?> state) {
            this.state = state;
        }

        protected IFluidHandler getFluidHandler() {
            return state.getPositionedAddonsNetwork().getChannelExternal(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, state.getChannel());
        }

        @Override
        public int getTanks() {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getFluidHandler().getTanks();
            state.enablePosition();
            return ret;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            if (!state.isNetworkAndPositionValid()) {
                return FluidStack.EMPTY;
            }
            state.disablePosition();
            FluidStack ret = getFluidHandler().getFluidInTank(tank);
            state.enablePosition();
            return ret;
        }

        @Override
        public int getTankCapacity(int tank) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getFluidHandler().getTankCapacity(tank);
            state.enablePosition();
            return ret;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            if (!state.isNetworkAndPositionValid()) {
                return false;
            }
            state.disablePosition();
            boolean ret = getFluidHandler().isFluidValid(tank, stack);
            state.enablePosition();
            return ret;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getFluidHandler().fill(resource, action);
            state.enablePosition();
            return ret;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (!state.isNetworkAndPositionValid()) {
                return FluidStack.EMPTY;
            }
            state.disablePosition();
            FluidStack ret = getFluidHandler().drain(resource, action);
            state.enablePosition();
            return ret;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (!state.isNetworkAndPositionValid()) {
                return FluidStack.EMPTY;
            }
            state.disablePosition();
            FluidStack ret = getFluidHandler().drain(maxDrain, action);
            state.enablePosition();
            return ret;
        }
    }
}
