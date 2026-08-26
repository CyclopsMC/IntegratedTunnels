package org.cyclops.integratedtunnels.core.part;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.cyclopscore.helper.BlockEntityHelpers;
import org.cyclops.integrateddynamics.api.evaluate.variable.ValueDeseralizationContext;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.api.part.IPartState;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
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
        return BlockEntityHelpers.getCapability(pos.getPos(), pos.getSide(), getBlockCapability());
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
            boolean validTargetCapability = getTargetCapabilityInstance(getEffectiveTargetPos(target, state))
                    .map(this::isTargetCapabilityValid)
                    .orElse(false);
            boolean wasValidTargetCapability = state.isValidTargetCapability();
            // Only trigger a change if the capability presence has changed.
            if (validTargetCapability != wasValidTargetCapability) {
                removeTargetFromNetwork(network, state);
                addTargetToNetwork(network, target, priority, channelInterface, state);
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
