package org.cyclops.integratedtunnels.core.part;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.integrateddynamics.api.evaluate.variable.ValueDeseralizationContext;
import org.cyclops.integrateddynamics.api.network.*;
import org.cyclops.integrateddynamics.api.part.IPartContainer;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.core.part.PartTypeBase;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;

import javax.annotation.Nullable;
import java.util.Optional;

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
    protected void onVariableContentsUpdated(IPartNetwork network, PartTarget target, S state) {
        super.onVariableContentsUpdated(network, target, state);
        state.requireAspectUpdate();
    }

    @Override
    public Optional<MenuProvider> getContainerProviderSettings(PartPos pos) {
        return Optional.of(new MenuProvider() {

            @Override
            public Component getDisplayName() {
                return Component.translatable(getTranslationKey());
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
                Triple<IPartContainer, PartTypeBase, PartTarget> data = PartHelpers.getContainerPartConstructionData(pos);
                return new ContainerInterfaceSettings(id, playerInventory, new SimpleContainer(0),
                        data.getRight(), Optional.of(data.getLeft()), data.getMiddle());
            }

            @Override
            public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                return false;
            }
        });
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
        N addonsNetwork = state.getPositionedAddonsNetwork();
        if (addonsNetwork != null) {
            ((IPositionedAddonsNetworkIngredients<T, ?>) addonsNetwork).setPositionedStorageFilter(pos, null);
        }
    }

    // Methods below copied from PartTypeInterfacePositionedAddon

    @Override
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.afterNetworkReAlive(network, partNetwork, target, state);
        addTargetToNetwork(network, target, state.getPriority(), state.getChannelInterface(), state);
    }

    @Override
    public void onNetworkRemoval(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkRemoval(network, partNetwork, target, state);
        scheduleNetworkObservation(target, state);
        removeTargetFromNetwork(network, state);
    }

    @Override
    public void onNetworkAddition(INetwork network, IPartNetwork partNetwork, PartTarget target, S state) {
        super.onNetworkAddition(network, partNetwork, target, state);
        addTargetToNetwork(network, target, state.getPriority(), state.getChannelInterface(), state);
        scheduleNetworkObservation(target, state);
    }

    @Override
    public void onBlockNeighborChange(INetwork network, IPartNetwork partNetwork, PartTarget target, S state, BlockGetter world, @Nullable Direction side) {
        super.onBlockNeighborChange(network, partNetwork, target, state, world, side);
        if (network != null && canNeighbourChangeAffectTarget(state, world)) {
            updateTargetInNetwork(network, target, state.getPriority(), state.getChannelInterface(), state);
        }
    }

    @Override
    public void setPriorityAndChannel(INetwork network, IPartNetwork partNetwork, PartTarget target, S state, int priority, int channel) {
        // We need to do this because the energy network is not automagically aware of the priority changes,
        // so we have to re-add it.
        removeTargetFromNetwork(network, state);
        super.setPriorityAndChannel(network, partNetwork, target, state, priority, channel);
        addTargetToNetwork(network, target, priority, state.getChannelInterface(), state);
    }

    @Override
    public boolean setTargetOffset(S state, PartPos center, Vec3i offset) {
        // Remove interface before changing offset, and re-add after,
        // because the target offset might change the interface.
        INetwork network = state.getNetwork();
        if (network != null) {
            removeTargetFromNetwork(network, state);
        }
        boolean ret = super.setTargetOffset(state, center, offset);
        if (network != null) {
            PartTarget target = getTarget(center, state);
            addTargetToNetwork(network, target, state.getPriority(), state.getChannelInterface(), state);
            // Force an observation, so that the network index does not linger on the old target
            scheduleNetworkObservation(target, state);
        }
        return ret;
    }

    @Override
    public void setTargetSideOverride(S state, @Nullable Direction side) {
        // Remove interface before changing the target side, and re-add after,
        // because the target side determines the position of this interface in the network.
        INetwork network = state.getNetwork();
        PartPos center = state.getCenter();
        if (network != null && center != null) {
            removeTargetFromNetwork(network, state);
        }
        super.setTargetSideOverride(state, side);
        if (network != null && center != null) {
            PartTarget target = getTarget(center, state);
            addTargetToNetwork(network, target, state.getPriority(), state.getChannelInterface(), state);
            // Force an observation, so that the network index does not linger on the old target side
            scheduleNetworkObservation(target, state);
        }
    }

    public static abstract class State<N extends IPositionedAddonsNetwork, T, P extends PartTypeInterfacePositionedAddonFiltering<N, T, P, S>, S extends PartTypeInterfacePositionedAddonFiltering.State<N, T, P, S>>
            extends PartStateWriterBase<P>
            implements IPartTypeInterfacePositionedAddon.IState<N, T, P, S> {
        private N positionedAddonsNetwork = null;
        private PartPos pos = null;
        private PartPos center = null;
        private BlockState validatedTargetBlockState = null;
        private boolean targetCapabilityInvalidated = true;
        private BlockPos targetCapabilityListenerPos = null;
        // Strong reference: NeoForge only holds capability invalidation listeners weakly.
        private ICapabilityInvalidationListener targetCapabilityListener = null;
        private boolean validTargetCapability = false;
        private int channelInterface = 0;

        private PositionedAddonsNetworkIngredientsFilter<T> targetFilter = null;
        private INetwork network;
        private IPartNetwork partNetwork;
        private ValueDeseralizationContext valueDeseralizationContext;
        private boolean requireAspectUpdate = true;

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        protected int getDefaultUpdateInterval() {
            return 10;
        }

        @Override
        public void serialize(ValueOutput valueOutput) {
            super.serialize(valueOutput);
            valueOutput.putInt("channelInterface", channelInterface);
        }

        @Override
        public void deserialize(ValueInput valueInput) {
            super.deserialize(valueInput);
            this.channelInterface = valueInput.getInt("channelInterface").orElseThrow();
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

        @Nullable
        @Override
        public PartPos getCenter() {
            return center;
        }

        @Override
        public void setCenter(@Nullable PartPos center) {
            this.center = center;
        }

        @Nullable
        @Override
        public BlockState getValidatedTargetBlockState() {
            return validatedTargetBlockState;
        }

        @Override
        public void setValidatedTargetBlockState(@Nullable BlockState validatedTargetBlockState) {
            this.validatedTargetBlockState = validatedTargetBlockState;
        }

        @Override
        public boolean isTargetCapabilityInvalidated() {
            return targetCapabilityInvalidated;
        }

        @Override
        public void setTargetCapabilityInvalidated(boolean targetCapabilityInvalidated) {
            this.targetCapabilityInvalidated = targetCapabilityInvalidated;
        }

        @Nullable
        @Override
        public BlockPos getTargetCapabilityListenerPos() {
            return targetCapabilityListenerPos;
        }

        @Override
        public void setTargetCapabilityListener(BlockPos pos, ICapabilityInvalidationListener listener) {
            this.targetCapabilityListenerPos = pos;
            this.targetCapabilityListener = listener;
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

            // Trigger aspect re-execution if needed.
            // Our networks are unset while this part is detached from its network, in which case we retry later.
            if (targetFilter == null || network == null || partNetwork == null) {
                this.requireAspectUpdate();
            } else {
                getVariable(network, partNetwork, valueDeseralizationContext).addInvalidationListener(this::requireAspectUpdate);
            }
        }

        public void requireAspectUpdate() {
            this.requireAspectUpdate = true;
        }

        @Override
        public void setNetworks(@Nullable INetwork network, @Nullable IPartNetwork partNetwork, ValueDeseralizationContext valueDeseralizationContext) {
            this.network = network;
            this.partNetwork = partNetwork;
            this.valueDeseralizationContext = valueDeseralizationContext;
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
        public <T> Optional<T> getCapability(P partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == getTargetCapability()) {
                return Optional.of((T) this.getCapabilityInstance());
            }
            return super.getCapability(partType, capability, network, partNetwork, target);
        }
    }

}
