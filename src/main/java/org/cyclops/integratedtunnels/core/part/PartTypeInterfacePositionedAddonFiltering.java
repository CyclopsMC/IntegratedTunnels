package org.cyclops.integratedtunnels.core.part;

import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.api.network.PositionedAddonsNetworkIngredientsFilter;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;

import javax.annotation.Nullable;

/**
 * Interface for positioned network addons that have a filter.
 * @author rubensworks
 */
public abstract class PartTypeInterfacePositionedAddonFiltering<N extends IPositionedAddonsNetwork, T, P extends PartTypeInterfacePositionedAddonFiltering<N, T, P, S>, S extends PartTypeInterfacePositionedAddonFiltering.State<N, T, P, S>>
        extends PartTypeTunnelAspects<P, S>
        implements IPartTypeInterfacePositionedAddon<N, T, P, S> {

    public PartTypeInterfacePositionedAddonFiltering(String name) {
        super(name);
    }

    @Override
    public void update(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        if (state.isRequireAspectUpdateAndReset()) {
            // For filter interfaces, we assume that targetFilters are set upon each aspect exec, which we only need to do once.
            super.update(network, partNetwork, target, state);
        }
    }

    @Override
    public void onAddingPositionToNetwork(N networkCapability, INetwork network, PartPos pos, int priority, int channelInterface, S state) {
        if (state.getTargetFilter() != null) {
            networkCapability.addPosition(pos, priority, channelInterface);
            ((IPositionedAddonsNetworkIngredients<T, ?>) state.getPositionedAddonsNetwork()).setPositionedStorageFilter(pos, state.getTargetFilter());
        }
    }

    @Override
    public void onRemovingPositionFromNetwork(N networkCapability, INetwork network, PartPos pos, S state) {
        networkCapability.removePosition(pos);
        ((IPositionedAddonsNetworkIngredients<T, ?>) state.getPositionedAddonsNetwork()).setPositionedStorageFilter(pos, null);
    }

    // Methods below copied from PartTypeInterfacePositionedAddon

    @Override
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.afterNetworkReAlive(network, partNetwork, target, state);
        addTargetToNetwork(network, target.getTarget(), state.getPriority(), state.getChannelInterface(), state);
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
    public void onBlockNeighborChange(INetwork network, IPartNetwork partNetwork, PartTarget target, S state, IBlockReader world, Block neighbourBlock, BlockPos neighbourBlockPos) {
        super.onBlockNeighborChange(network, partNetwork, target, state, world, neighbourBlock, neighbourBlockPos);
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

    public static abstract class State<N extends IPositionedAddonsNetwork, T, P extends PartTypeInterfacePositionedAddonFiltering<N, T, P, S>, S extends PartTypeInterfacePositionedAddonFiltering.State<N, T, P, S>>
            extends PartStateWriterBase<P>
            implements IPartTypeInterfacePositionedAddon.IState<N, T, P, S> {
        private N positionedAddonsNetwork = null;
        private PartPos pos = null;
        private boolean validTargetCapability = false;
        private int channelInterface = 0;

        private PositionedAddonsNetworkIngredientsFilter<T> targetFilter = null;
        private INetwork network;
        private IPartNetwork partNetwork;
        private boolean requireAspectUpdate = true;

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        protected int getDefaultUpdateInterval() {
            return 10;
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            super.readFromNBT(tag);
            if (tag.contains("channelInterface", Constants.NBT.TAG_INT)) {
                this.channelInterface = tag.getInt("channelInterface");
            }
        }

        @Override
        public void writeToNBT(CompoundNBT tag) {
            super.writeToNBT(tag);
            tag.putInt("channelInterface", channelInterface);
        }

        @Override
        public void setChannelInterface(int channelInterface) {
            this.channelInterface = channelInterface;
            sendUpdate();
        }

        @Override
        public int getChannelInterface() {
            return channelInterface;
        }

        @Override
        @Nullable
        public N getPositionedAddonsNetwork() {
            return positionedAddonsNetwork;
        }

        @Override
        public void setPositionedAddonsNetwork(N positionedAddonsNetwork) {
            this.positionedAddonsNetwork = positionedAddonsNetwork;
        }

        @Override
        public boolean isValidTargetCapability() {
            return validTargetCapability;
        }

        @Override
        public void setValidTargetCapability(boolean validTargetCapability) {
            this.validTargetCapability = validTargetCapability;
        }

        @Override
        public PartPos getPos() {
            return pos;
        }

        @Override
        public void setPos(PartPos pos) {
            this.pos = pos;
        }

        public boolean isRequireAspectUpdateAndReset() {
            boolean ret = this.requireAspectUpdate;
            this.requireAspectUpdate = false;
            return ret;
        }

        @Nullable
        public PositionedAddonsNetworkIngredientsFilter<T> getTargetFilter() {
            return this.targetFilter;
        }

        public void setTargetFilter(@Nullable PositionedAddonsNetworkIngredientsFilter<T> targetFilter) {
            this.targetFilter = targetFilter;

            // Trigger aspect re-execution if needed
            if (targetFilter == null) {
                this.requireAspectUpdate();
            } else {
                getVariable(network, partNetwork).addInvalidationListener(this::requireAspectUpdate);
            }
        }

        public void requireAspectUpdate() {
            this.requireAspectUpdate = true;
        }

        @Override
        public void setNetworks(@Nullable INetwork network, @Nullable IPartNetwork partNetwork) {
            this.network = network;
            this.partNetwork = partNetwork;
        }

        @Override
        @Nullable
        public INetwork getNetwork() {
            return network;
        }

        @Override
        @Nullable
        public IPartNetwork getPartNetwork() {
            return partNetwork;
        }

        @Override
        public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == getTargetCapability()) {
                return LazyOptional.of(this::getCapabilityInstance).cast();
            }
            return super.getCapability(capability, network, partNetwork, target);
        }
    }

}
