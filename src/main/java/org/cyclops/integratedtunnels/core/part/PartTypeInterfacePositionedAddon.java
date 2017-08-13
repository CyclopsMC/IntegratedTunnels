package org.cyclops.integratedtunnels.core.part;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.part.PartStateBase;

import javax.annotation.Nullable;

/**
 * Interface for positioned network addons.
 * @author rubensworks
 */
public abstract class PartTypeInterfacePositionedAddon<N extends IPositionedAddonsNetwork, T, P extends IPartType<P, S>, S extends PartTypeInterfacePositionedAddon.State<P, N, T>> extends PartTypeTunnel<P, S> {
    public PartTypeInterfacePositionedAddon(String name) {
        super(name);
    }

    protected abstract Capability<N> getNetworkCapability();
    protected abstract Capability<T> getTargetCapability();
    protected boolean isTargetCapabilityValid(T capability) {
        return capability != null;
    }

    @Override
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.afterNetworkReAlive(network, partNetwork, target, state);
        addTargetToNetwork(network, target.getTarget(), state.getPriority(), state);
    }

    @Override
    public void onNetworkRemoval(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkRemoval(network, partNetwork, target, state);
        removeTargetFromNetwork(network, target.getTarget(), state);
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkAddition(network, partNetwork, target, state);
        addTargetToNetwork(network, target.getTarget(), state.getPriority(), state);
    }

    @Override
    public void onBlockNeighborChange(@Nullable INetwork network, @Nullable IPartNetwork partNetwork, PartTarget target, S state, IBlockAccess world, Block neighborBlock) {
        super.onBlockNeighborChange(network, partNetwork, target, state, world, neighborBlock);
        if (network != null) {
            removeTargetFromNetwork(network, target.getTarget(), state);
            addTargetToNetwork(network, target.getTarget(), state.getPriority(), state);
        }
    }

    @Override
    public void setPriority(INetwork network, IPartNetwork partNetwork, PartTarget target, S state, int priority) {
        // We need to do this because the energy network is not automagically aware of the priority changes,
        // so we have to re-add it.
        removeTargetFromNetwork(network, target.getTarget(), state);
        super.setPriority(network, partNetwork, target, state, priority);
        addTargetToNetwork(network, target.getTarget(), priority, state);
    }

    protected T getTargetCapabilityInstance(PartPos pos) {
        return TileHelpers.getCapability(pos.getPos(), pos.getSide(), getTargetCapability());
    }

    protected void addTargetToNetwork(INetwork network, PartPos pos, int priority, S state) {
        if (network.hasCapability(getNetworkCapability())) {
            T capability = getTargetCapabilityInstance(pos);
            if (isTargetCapabilityValid(capability)) {
                N networkCapability = network.getCapability(getNetworkCapability());
                networkCapability.addPosition(pos, priority);
            }
            state.setPositionedAddonsNetwork(network.getCapability(getNetworkCapability()));
            state.setPos(pos);
        }
    }

    protected void removeTargetFromNetwork(INetwork network, PartPos pos, S state) {
        if (network.hasCapability(getNetworkCapability())) {
            N networkCapability = network.getCapability(getNetworkCapability());
            networkCapability.removePosition(pos);
        }
        state.setPositionedAddonsNetwork(null);
        state.setPos(null);
    }

    public static abstract class State<P extends IPartType, N extends IPositionedAddonsNetwork, T> extends PartStateBase<P> {

        private N positionedAddonsNetwork = null;
        private PartPos pos = null;

        protected abstract Capability<T> getTargetCapability();

        public N getPositionedAddonsNetwork() {
            return positionedAddonsNetwork;
        }

        public void setPositionedAddonsNetwork(N positionedAddonsNetwork) {
            this.positionedAddonsNetwork = positionedAddonsNetwork;
        }

        public PartPos getPos() {
            return pos;
        }

        public void setPos(PartPos pos) {
            this.pos = pos;
        }

        protected void disablePosition() {
            N positionedNetwork = getPositionedAddonsNetwork();
            PartPos pos = getPos();
            if (positionedNetwork != null) {
                positionedNetwork.disablePosition(pos);
            }
        }

        protected void enablePosition() {
            N positionedNetwork = getPositionedAddonsNetwork();
            PartPos pos = getPos();
            if (positionedNetwork != null) {
                positionedNetwork.enablePosition(pos);
            }
        }

        @Override
        public boolean hasCapability(Capability<?> capability) {
            return (getPositionedAddonsNetwork() != null && capability == getTargetCapability())
                    || super.hasCapability(capability);
        }

        @Override
        public <T2> T2 getCapability(Capability<T2> capability) {
            if (getPositionedAddonsNetwork() != null && capability == getTargetCapability()) {
                return (T2) this;
            }
            return super.getCapability(capability);
        }
    }
}
