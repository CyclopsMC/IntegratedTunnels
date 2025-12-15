package org.cyclops.integratedtunnels.part;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

import javax.annotation.Nonnull;

/**
 * Interface for fluid handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceFluid extends PartTypeInterfacePositionedAddon<IFluidNetwork, ResourceHandler<FluidResource>, PartTypeInterfaceFluid, PartTypeInterfaceFluid.State> {
    public PartTypeInterfaceFluid(String name) {
        super(name);
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
    protected PartTypeInterfaceFluid.State constructDefaultState() {
        return new PartTypeInterfaceFluid.State();
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceFluidBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<IFluidNetwork, ResourceHandler<FluidResource>, PartTypeInterfaceFluid, PartTypeInterfaceFluid.State> {

        @Override
        public PartCapability<ResourceHandler<FluidResource>> getTargetCapability() {
            return Capabilities.Fluid.PART;
        }

        @Override
        public ResourceHandler<FluidResource> getCapabilityInstance() {
            return new PartTypeInterfaceFluid.FluidHandler(this);
        }
    }

    public static class FluidHandler implements ResourceHandler<FluidResource> {
        private final IPartTypeInterfacePositionedAddon.IState<IFluidNetwork, ResourceHandler<FluidResource>, ?, ?> state;

        public FluidHandler(IState<IFluidNetwork, ResourceHandler<FluidResource>, ?, ?> state) {
            this.state = state;
        }

        protected ResourceHandler<FluidResource> getFluidHandler() {
            return state.getPositionedAddonsNetwork().getChannelExternal(net.neoforged.neoforge.capabilities.Capabilities.Fluid.BLOCK, state.getChannel());
        }

        @Override
        public int size() {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getFluidHandler().size();
            state.enablePosition();
            return ret;
        }

        @Nonnull
        @Override
        public FluidResource getResource(int tank) {
            if (!state.isNetworkAndPositionValid()) {
                return FluidResource.EMPTY;
            }
            state.disablePosition();
            FluidResource ret = getFluidHandler().getResource(tank);
            state.enablePosition();
            return ret;
        }

        @Override
        public long getAmountAsLong(int tank) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            long ret = getFluidHandler().getAmountAsLong(tank);
            state.enablePosition();
            return ret;
        }

        @Override
        public long getCapacityAsLong(int tank, FluidResource fluidResource) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            long ret = getFluidHandler().getCapacityAsLong(tank, fluidResource);
            state.enablePosition();
            return ret;
        }

        @Override
        public boolean isValid(int tank, FluidResource fluidResource) {
            if (!state.isNetworkAndPositionValid()) {
                return false;
            }
            state.disablePosition();
            boolean ret = getFluidHandler().isValid(tank, fluidResource);
            state.enablePosition();
            return ret;
        }

        @Override
        public int insert(int tank, FluidResource resource, int amount, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getFluidHandler().insert(tank, resource, amount, transaction);
            state.enablePosition();
            return ret;
        }

        @Override
        public int insert(FluidResource resource, int amount, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getFluidHandler().insert(resource, amount, transaction);
            state.enablePosition();
            return ret;
        }

        @Override
        public int extract(int tank, FluidResource resource, int amount, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getFluidHandler().extract(tank, resource, amount, transaction);
            state.enablePosition();
            return ret;
        }

        @Override
        public int extract(FluidResource resource, int amount, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getFluidHandler().extract(resource, amount, transaction);
            state.enablePosition();
            return ret;
        }
    }
}
