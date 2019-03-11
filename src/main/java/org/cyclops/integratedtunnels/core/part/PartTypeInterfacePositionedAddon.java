package org.cyclops.integratedtunnels.core.part;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
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

    @Override
    public Class<? extends GuiScreen> getGui() {
        return GuiInterfaceSettings.class;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerInterfaceSettings.class;
    }

    protected abstract Capability<N> getNetworkCapability();
    protected abstract Capability<T> getTargetCapability();
    protected boolean isTargetCapabilityValid(T capability) {
        return capability != null;
    }

    @Override
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.afterNetworkReAlive(network, partNetwork, target, state);
        addTargetToNetwork(network, target.getTarget(), state.getPriority(), state.getChannelInterface(), state);
    }

    protected void scheduleNetworkObservation(PartTarget target, S state) {
        IPositionedAddonsNetwork positionedAddonsNetwork = state.getPositionedAddonsNetwork();
        if (positionedAddonsNetwork instanceof IPositionedAddonsNetworkIngredients) {
            ((IPositionedAddonsNetworkIngredients) positionedAddonsNetwork).scheduleObservationForced(
                    state.getChannelInterface(), target.getTarget());
        }
    }

    @Override
    public void onNetworkRemoval(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkRemoval(network, partNetwork, target, state);
        scheduleNetworkObservation(target, state);
        removeTargetFromNetwork(network, target.getTarget(), state);
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkAddition(network, partNetwork, target, state);
        addTargetToNetwork(network, target.getTarget(), state.getPriority(), state.getChannelInterface(), state);
        scheduleNetworkObservation(target, state);
    }

    @Override
    public void onBlockNeighborChange(@Nullable INetwork network, @Nullable IPartNetwork partNetwork, PartTarget target, S state, IBlockAccess world, Block neighborBlock) {
        super.onBlockNeighborChange(network, partNetwork, target, state, world, neighborBlock);
        if (network != null) {
            updateTargetInNetwork(network, target.getTarget(), state.getPriority(), state.getChannelInterface(), state);
        }
    }

    @Override
    public void setPriorityAndChannel(INetwork network, IPartNetwork partNetwork, PartTarget target, S state, int priority, int channel) {
        // We need to do this because the energy network is not automagically aware of the priority changes,
        // so we have to re-add it.
        removeTargetFromNetwork(network, target.getTarget(), state);
        super.setPriorityAndChannel(network, partNetwork, target, state, priority, channel);
        addTargetToNetwork(network, target.getTarget(), priority, state.getChannelInterface(), state);
    }

    protected T getTargetCapabilityInstance(PartPos pos) {
        return TileHelpers.getCapability(pos.getPos(), pos.getSide(), getTargetCapability());
    }

    protected void addTargetToNetwork(INetwork network, PartPos pos, int priority, int channelInterface, S state) {
        if (network.hasCapability(getNetworkCapability())) {
            T capability = getTargetCapabilityInstance(pos);
            boolean validTargetCapability = isTargetCapabilityValid(capability);
            if (validTargetCapability) {
                N networkCapability = network.getCapability(getNetworkCapability());
                networkCapability.addPosition(pos, priority, channelInterface);
            }
            state.setPositionedAddonsNetwork(network.getCapability(getNetworkCapability()));
            state.setPos(pos);
            state.setValidTargetCapability(validTargetCapability);
        }
    }

    protected void removeTargetFromNetwork(INetwork network, PartPos pos, S state) {
        if (network.hasCapability(getNetworkCapability())) {
            N networkCapability = network.getCapability(getNetworkCapability());
            networkCapability.removePosition(pos);
        }
        state.setPositionedAddonsNetwork(null);
        state.setPos(null);
        state.setValidTargetCapability(false);
    }

    protected void updateTargetInNetwork(INetwork network, PartPos pos, int priority, int channelInterface, S state) {
        if (network.hasCapability(getNetworkCapability())) {
            T capability = getTargetCapabilityInstance(pos);
            boolean validTargetCapability = isTargetCapabilityValid(capability);
            boolean wasValidTargetCapability = state.isValidTargetCapability();
            // Only trigger a change if the capability presence has changed.
            if (validTargetCapability != wasValidTargetCapability) {
                removeTargetFromNetwork(network, pos, state);
                addTargetToNetwork(network, pos, priority, channelInterface, state);
            }
        }
    }

    public static abstract class State<P extends IPartType, N extends IPositionedAddonsNetwork, T> extends PartStateBase<P> {

        private N positionedAddonsNetwork = null;
        private PartPos pos = null;
        private boolean validTargetCapability = false;
        private int channelInterface = 0;

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            super.readFromNBT(tag);
            // TODO: backwards compat for old channel saving, simplify in 1.13
            if (tag.hasKey("channelInterface", Constants.NBT.TAG_INT)) {
                this.channelInterface = tag.getInteger("channelInterface");
            } else {
                this.channelInterface = getChannel();
            }
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            super.writeToNBT(tag);
            tag.setInteger("channelInterface", channelInterface);
        }

        public void setChannelInterface(int channelInterface) {
            this.channelInterface = channelInterface;
            sendUpdate();
        }

        public int getChannelInterface() {
            return channelInterface;
        }

        protected abstract Capability<T> getTargetCapability();

        public N getPositionedAddonsNetwork() {
            return positionedAddonsNetwork;
        }

        public void setPositionedAddonsNetwork(N positionedAddonsNetwork) {
            this.positionedAddonsNetwork = positionedAddonsNetwork;
        }

        public boolean isValidTargetCapability() {
            return validTargetCapability;
        }

        public void setValidTargetCapability(boolean validTargetCapability) {
            this.validTargetCapability = validTargetCapability;
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

        protected boolean isPositionEnabled() {
            N positionedNetwork = getPositionedAddonsNetwork();
            PartPos pos = getPos();
            if (positionedNetwork != null) {
                return !positionedNetwork.isPositionDisabled(pos);
            }
            return true;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, IPartNetwork network, PartTarget target) {
            return (getPositionedAddonsNetwork() != null && capability == getTargetCapability() && isPositionEnabled())
                    || super.hasCapability(capability, network, target);
        }

        @Override
        public <T2> T2 getCapability(Capability<T2> capability, IPartNetwork network, PartTarget target) {
            if (getPositionedAddonsNetwork() != null && capability == getTargetCapability()) {
                if (!isPositionEnabled()) {
                    return null;
                }
                return (T2) this;
            }
            return super.getCapability(capability, network, target);
        }
    }

}
