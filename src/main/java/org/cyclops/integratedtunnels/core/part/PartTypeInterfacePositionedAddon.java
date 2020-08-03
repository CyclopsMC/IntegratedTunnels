package org.cyclops.integratedtunnels.core.part;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.api.part.IPartContainer;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.core.part.PartStateBase;
import org.cyclops.integrateddynamics.core.part.PartTypeBase;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Interface for positioned network addons.
 * @author rubensworks
 */
public abstract class PartTypeInterfacePositionedAddon<N extends IPositionedAddonsNetwork, T, P extends IPartType<P, S>, S extends PartTypeInterfacePositionedAddon.State<P, N, T>> extends PartTypeTunnel<P, S> {
    public PartTypeInterfacePositionedAddon(String name) {
        super(name);
    }

    @Override
    public Optional<INamedContainerProvider> getContainerProvider(PartPos pos) {
        return Optional.of(new INamedContainerProvider() {

            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent(getTranslationKey());
            }

            @Nullable
            @Override
            public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers.getContainerPartConstructionData(pos);
                return new ContainerInterfaceSettings(id, playerInventory, new Inventory(0),
                        data.getRight(), Optional.of(data.getLeft()), data.getMiddle());
            }
        });
    }

    @Override
    public void writeExtraGuiData(PacketBuffer packetBuffer, PartPos pos, ServerPlayerEntity player) {
        PacketCodec.write(packetBuffer, pos);
        packetBuffer.writeString(this.getUniqueName().toString());
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

    protected LazyOptional<T> getTargetCapabilityInstance(PartPos pos) {
        return TileHelpers.getCapability(pos.getPos(), pos.getSide(), getTargetCapability());
    }

    protected void addTargetToNetwork(INetwork network, PartPos pos, int priority, int channelInterface, S state) {
        network.getCapability(getNetworkCapability())
                .ifPresent(networkCapability -> {
                    boolean validTargetCapability = getTargetCapabilityInstance(pos)
                            .map(this::isTargetCapabilityValid)
                            .orElse(false);
                    if (validTargetCapability) {
                        networkCapability.addPosition(pos, priority, channelInterface);
                    }
                    state.setPositionedAddonsNetwork(networkCapability);
                    state.setPos(pos);
                    state.setValidTargetCapability(validTargetCapability);
                });
    }

    protected void removeTargetFromNetwork(INetwork network, PartPos pos, S state) {
        network.getCapability(getNetworkCapability())
                .ifPresent(networkCapability -> networkCapability.removePosition(pos));
        state.setPositionedAddonsNetwork(null);
        state.setPos(null);
        state.setValidTargetCapability(false);
    }

    protected void updateTargetInNetwork(INetwork network, PartPos pos, int priority, int channelInterface, S state) {
        if (network.getCapability(getNetworkCapability()).isPresent()) {
            boolean validTargetCapability = getTargetCapabilityInstance(pos)
                    .map(this::isTargetCapabilityValid)
                    .orElse(false);
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

        protected boolean isNetworkAndPositionValid() {
            return getPositionedAddonsNetwork() != null && isPositionEnabled();
        }

        @Override
        public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == getTargetCapability()) {
                return LazyOptional.of(() -> this).cast();
            }
            return super.getCapability(capability, network, partNetwork, target);
        }
    }

}
