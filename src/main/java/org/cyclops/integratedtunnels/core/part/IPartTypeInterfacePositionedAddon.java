package org.cyclops.integratedtunnels.core.part;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.cyclopscore.helper.IModHelpersNeoForge;
import org.cyclops.integrateddynamics.api.evaluate.variable.ValueDeseralizationContext;
import org.cyclops.integrateddynamics.api.network.*;
import org.cyclops.integrateddynamics.api.part.*;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Interface for positioned network addons.
 * @author rubensworks
 */
public interface IPartTypeInterfacePositionedAddon<N extends IPositionedAddonsNetwork, T, P extends IPartTypeInterfacePositionedAddon<N, T, P, S>, S extends IPartTypeInterfacePositionedAddon.IState<N, T, P, S>> extends IPartType<P, S> {

    public NetworkCapability<N> getNetworkCapability();

    public PartCapability<T> getPartCapability();

    public BlockCapability<T, Direction> getBlockCapability();

    public default boolean isTargetCapabilityValid(T capability) {
        return capability != null;
    }

    public default Optional<T> getTargetCapabilityInstance(PartPos pos) {
        return IModHelpersNeoForge.get().getCapabilityHelpers().getCapability(pos.getPos(), pos.getSide(), getBlockCapability());
    }

    /**
     * Determine the position that this interface should expose to the network for the given target.
     *
     * The target position inside the given target is not always up-to-date,
     * as it does not take the part state's target side override and offset into account,
     * such as when a network element is revalidated.
     * That is why it is recalculated here based on the part state.
     *
     * @param target The part target.
     * @param state The part state.
     * @return The effective target position.
     */
    public default PartPos getEffectiveTargetPos(PartTarget target, S state) {
        return getTarget(target.getCenter(), state).getTarget();
    }

    /**
     * Check if a neighbour change can influence the target capability of this interface.
     *
     * Re-checking the target capability requires a block entity and a block capability lookup,
     * while neighbour changes are very frequent: every comparator-relevant change of a nearby container
     * triggers one on the cable, so an interface exposing a chest would re-check for every item that
     * moves in or out of it. This method filters those out.
     *
     * Neighbour changes don't tell us which neighbour was changed,
     * as {@link IPartType#onBlockNeighborChange(INetwork, IPartNetwork, PartTarget, IPartState, BlockGetter, Direction)}
     * only receives the side at the center block, which is absent for most of these changes.
     * That is why the block state at our target is compared against the last one we validated instead.
     *
     * A capability can appear or disappear without the block state changing at all,
     * for example when the configuration of a block entity changes.
     * NeoForge requires such changes to be signalled through
     * {@link net.minecraft.world.level.Level#invalidateCapabilities(BlockPos)}, which is what
     * {@link #registerTargetCapabilityListener(PartPos, IState)} subscribes to, so that they mark the
     * target as needing a re-check here.
     *
     * @param state The part state.
     * @param world The world the neighbour change happened in.
     * @return If the target capability of this interface may have changed.
     */
    public default boolean canNeighbourChangeAffectTarget(S state, BlockGetter world) {
        PartPos pos = state.getPos();
        PartPos center = state.getCenter();
        if (pos == null || center == null || !state.isValidTargetCapability()) {
            // If we don't know our exposed position yet, or don't expose a target capability yet,
            // we have to assume that a target may have become available.
            return true;
        }

        if (state.isTargetCapabilityInvalidated()) {
            // Our target signalled that its capabilities changed since we last checked them.
            return true;
        }

        BlockPos targetPos = pos.getPos().getBlockPos();
        Direction side = center.getSide();
        BlockPos centerPos = center.getPos().getBlockPos();
        if (targetPos.getX() != centerPos.getX() + side.getStepX()
                || targetPos.getY() != centerPos.getY() + side.getStepY()
                || targetPos.getZ() != centerPos.getZ() + side.getStepZ()) {
            // Interfaces with a target offset or side override are not necessarily a neighbour of their target,
            // so they may never receive a neighbour change for it, and have to keep re-checking.
            return true;
        }

        // By far the most neighbour changes we receive are the comparator updates that our own target
        // emits whenever its contents change, which can not affect its capabilities.
        // A block state change at our target can, and while it is supposed to be accompanied by a
        // capability invalidation, not all blocks do so, so fall back to comparing block states here.
        BlockState validatedBlockState = state.getValidatedTargetBlockState();
        return validatedBlockState == null || world.getBlockState(targetPos) != validatedBlockState;
    }

    /**
     * Remember that the target capability at the given position was just checked,
     * so that {@link #canNeighbourChangeAffectTarget(IState, BlockGetter)} can tell
     * a change of that capability apart from a mere contents change of the target.
     * @param posTarget The position that this interface exposes to the network.
     * @param state The part state.
     */
    public default void rememberValidatedTarget(PartPos posTarget, S state) {
        Level level = posTarget.getPos().getLevel(true);
        state.setValidatedTargetBlockState(level == null ? null : level.getBlockState(posTarget.getPos().getBlockPos()));
        state.setTargetCapabilityInvalidated(false);
        registerTargetCapabilityListener(posTarget, state);
    }

    /**
     * Subscribe to capability invalidations at the given target position, if not subscribed already.
     *
     * This is the only reliable signal for capabilities that appear or disappear without the block state
     * changing, such as when the configuration of a block entity changes.
     * NeoForge holds these listeners weakly, so the part state keeps the only strong reference to it.
     *
     * @param posTarget The position that this interface exposes to the network.
     * @param state The part state.
     */
    public default void registerTargetCapabilityListener(PartPos posTarget, S state) {
        BlockPos blockPos = posTarget.getPos().getBlockPos();
        if (blockPos.equals(state.getTargetCapabilityListenerPos())) {
            // We are already listening at this position.
            return;
        }
        if (posTarget.getPos().getLevel(true) instanceof ServerLevel serverLevel) {
            ICapabilityInvalidationListener listener = () -> {
                state.setTargetCapabilityInvalidated(true);
                return true;
            };
            // The part state must hold on to the listener, as NeoForge only references it weakly.
            state.setTargetCapabilityListener(blockPos, listener);
            serverLevel.registerCapabilityListener(blockPos, listener);
        }
    }

    public default void scheduleNetworkObservation(PartTarget target, S state) {
        IPositionedAddonsNetwork positionedAddonsNetwork = state.getPositionedAddonsNetwork();
        if (positionedAddonsNetwork instanceof IPositionedAddonsNetworkIngredients) {
            ((IPositionedAddonsNetworkIngredients) positionedAddonsNetwork).scheduleObservationForced(
                    state.getChannelInterface(), getEffectiveTargetPos(target, state));
        }
    }

    public default Pair<N, Boolean> addPositionToNetwork(INetwork network, PartPos pos, int priority, int channelInterface, S state) {
        return network.getCapability(getNetworkCapability())
                .map(networkCapability -> {
                    boolean validTargetCapability = getTargetCapabilityInstance(pos)
                            .map(this::isTargetCapabilityValid)
                            .orElse(false);
                    return Pair.of(networkCapability, validTargetCapability);
                })
                .orElse(null);
    }

    public void onAddingPositionToNetwork(N networkCapability, INetwork network, PartPos pos, int priority, int channelInterface, S state);

    public default void removePositionFromNetwork(INetwork network, PartPos posTarget, S state) {
        network.getCapability(getNetworkCapability())
                .ifPresent(networkCapability -> {
                    onRemovingPositionFromNetwork(networkCapability, network, posTarget, state);
                });
    }

    public void onRemovingPositionFromNetwork(N networkCapability, INetwork network, PartPos pos, S state);

    /**
     * @deprecated Use {@link #addTargetToNetwork(INetwork, PartTarget, int, int, IState)} instead,
     *             which also keeps track of the part's center position.
     */
    @Deprecated // TODO: remove in next major
    public default void addTargetToNetwork(INetwork network, PartPos posTarget, int priority, int channelInterface, S state) {
        Pair<N, Boolean> ret = addPositionToNetwork(network, posTarget, priority, channelInterface, state);
        N networkCapability = ret.getLeft();
        boolean validTargetCapability = ret.getRight();
        if (networkCapability != null) {
            state.setPositionedAddonsNetwork(networkCapability);
            state.setNetworks(network, NetworkHelpers.getPartNetworkChecked(network), ValueDeseralizationContext.of(posTarget.getPos().getLevel(true)));
            state.setPos(posTarget);
            state.setValidTargetCapability(validTargetCapability);
            if (validTargetCapability) {
                onAddingPositionToNetwork(networkCapability, network, posTarget, priority, channelInterface, state);
            }
        }
    }

    public default void addTargetToNetwork(INetwork network, PartTarget target, int priority, int channelInterface, S state) {
        PartPos posTarget = getEffectiveTargetPos(target, state);
        Pair<N, Boolean> ret = addPositionToNetwork(network, posTarget, priority, channelInterface, state);
        N networkCapability = ret.getLeft();
        boolean validTargetCapability = ret.getRight();
        if (networkCapability != null) {
            state.setPositionedAddonsNetwork(networkCapability);
            state.setNetworks(network, NetworkHelpers.getPartNetworkChecked(network), ValueDeseralizationContext.of(posTarget.getPos().getLevel(true)));
            state.setPos(posTarget);
            state.setCenter(target.getCenter());
            state.setValidTargetCapability(validTargetCapability);
            rememberValidatedTarget(posTarget, state);
            if (validTargetCapability) {
                onAddingPositionToNetwork(networkCapability, network, posTarget, priority, channelInterface, state);
            }
        }
    }

    /**
     * @deprecated Use {@link #removeTargetFromNetwork(INetwork, IState)} instead,
     *             which always removes the position that was added before.
     */
    @Deprecated // TODO: remove in next major
    public default void removeTargetFromNetwork(INetwork network, PartPos pos, S state) {
        removeTargetFromNetwork(network, state);
    }

    public default void removeTargetFromNetwork(INetwork network, S state) {
        // Remove the position that was added to the network before,
        // as the effective target position may have changed in the meantime,
        // for example when the target side override or offset was modified.
        PartPos posTarget = state.getPos();
        if (posTarget != null) {
            removePositionFromNetwork(network, posTarget, state);
        }
        state.setPositionedAddonsNetwork(null);
        state.setNetworks(null, null, null);
        state.setPos(null);
        state.setValidTargetCapability(false);
        state.setValidatedTargetBlockState(null);
        state.setTargetCapabilityInvalidated(true);
    }

    /**
     * @deprecated Use {@link #updateTargetInNetwork(INetwork, PartTarget, int, int, IState)} instead,
     *             which also keeps track of the part's center position.
     */
    @Deprecated // TODO: remove in next major
    public default void updateTargetInNetwork(INetwork network, PartPos pos, int priority, int channelInterface, S state) {
        if (network.getCapability(getNetworkCapability()).isPresent()) {
            boolean validTargetCapability = getTargetCapabilityInstance(pos)
                    .map(this::isTargetCapabilityValid)
                    .orElse(false);
            boolean wasValidTargetCapability = state.isValidTargetCapability();
            // Only trigger a change if the capability presence has changed.
            if (validTargetCapability != wasValidTargetCapability) {
                removeTargetFromNetwork(network, state);
                addTargetToNetwork(network, pos, priority, channelInterface, state);
            }
        }
    }

    public default void updateTargetInNetwork(INetwork network, PartTarget target, int priority, int channelInterface, S state) {
        if (network.getCapability(getNetworkCapability()).isPresent()) {
            PartPos posTarget = getEffectiveTargetPos(target, state);
            boolean validTargetCapability = getTargetCapabilityInstance(posTarget)
                    .map(this::isTargetCapabilityValid)
                    .orElse(false);
            boolean wasValidTargetCapability = state.isValidTargetCapability();
            // Only trigger a change if the capability presence has changed.
            if (validTargetCapability != wasValidTargetCapability) {
                removeTargetFromNetwork(network, state);
                addTargetToNetwork(network, target, priority, channelInterface, state);
            } else {
                rememberValidatedTarget(posTarget, state);
            }
        }
    }

    public static interface IState<N extends IPositionedAddonsNetwork, T, P extends IPartTypeInterfacePositionedAddon<N, T, P, S>, S extends IPartTypeInterfacePositionedAddon.IState<N, T, P, S>> extends IPartState<P> {
        public void setChannelInterface(int channelInterface);
        public int getChannelInterface();
        public PartCapability<T> getTargetCapability();
        public N getPositionedAddonsNetwork();
        public void setPositionedAddonsNetwork(N positionedAddonsNetwork);
        public boolean isValidTargetCapability();
        public void setValidTargetCapability(boolean validTargetCapability);
        public PartPos getPos();
        public void setPos(PartPos pos);

        /**
         * @return The center position of this part, or null if this part was never added to a network.
         */
        // TODO: make a non-default method in next major
        @Nullable
        public default PartPos getCenter() {
            return null;
        }

        /**
         * Set the center position of this part.
         * @param center The center position.
         */
        // TODO: make a non-default method in next major
        public default void setCenter(@Nullable PartPos center) {

        }

        /**
         * @return The block state that was present at the exposed target position when its capability
         *         was last checked, or null if it is unknown.
         */
        // TODO: make a non-default method in next major
        @Nullable
        public default BlockState getValidatedTargetBlockState() {
            return null;
        }

        /**
         * Set the block state that is present at the exposed target position.
         * @param blockState The block state, or null if it is unknown.
         */
        // TODO: make a non-default method in next major
        public default void setValidatedTargetBlockState(@Nullable BlockState blockState) {

        }

        /**
         * @return If the capabilities at the exposed target position were invalidated
         *         since they were last checked.
         */
        // TODO: make a non-default method in next major
        public default boolean isTargetCapabilityInvalidated() {
            return true;
        }

        /**
         * Set if the capabilities at the exposed target position were invalidated
         * since they were last checked.
         * @param invalidated If they were invalidated.
         */
        // TODO: make a non-default method in next major
        public default void setTargetCapabilityInvalidated(boolean invalidated) {

        }

        /**
         * @return The position that this state is listening for capability invalidations at,
         *         or null if it is not listening anywhere.
         */
        // TODO: make a non-default method in next major
        @Nullable
        public default BlockPos getTargetCapabilityListenerPos() {
            return null;
        }

        /**
         * Store the capability invalidation listener for the given position.
         * The state must keep a strong reference to it, as NeoForge only references it weakly.
         * @param pos The position that is being listened at.
         * @param listener The listener.
         */
        // TODO: make a non-default method in next major
        public default void setTargetCapabilityListener(BlockPos pos, ICapabilityInvalidationListener listener) {

        }

        public default void disablePosition() {
            N positionedNetwork = getPositionedAddonsNetwork();
            PartPos pos = getPos();
            if (positionedNetwork != null) {
                positionedNetwork.disablePosition(pos);
            }
        }

        public default void enablePosition() {
            N positionedNetwork = getPositionedAddonsNetwork();
            PartPos pos = getPos();
            if (positionedNetwork != null) {
                positionedNetwork.enablePosition(pos);
            }
        }

        public default boolean isPositionEnabled() {
            N positionedNetwork = getPositionedAddonsNetwork();
            PartPos pos = getPos();
            if (positionedNetwork != null) {
                return !positionedNetwork.isPositionDisabled(pos);
            }
            return true;
        }

        public default boolean isNetworkAndPositionValid() {
            return getPositionedAddonsNetwork() != null && isPositionEnabled();
        }

        public void setNetworks(@Nullable INetwork network, @Nullable IPartNetwork partNetwork, ValueDeseralizationContext valueDeseralizationContext);
        @Nullable
        public INetwork getNetwork();
        @Nullable
        public IPartNetwork getPartNetwork();

        public abstract T getCapabilityInstance();
    }

}
