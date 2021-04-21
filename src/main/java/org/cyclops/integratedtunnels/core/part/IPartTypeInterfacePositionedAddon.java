package org.cyclops.integratedtunnels.core.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.api.part.IPartState;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;

import javax.annotation.Nullable;

/**
 * Interface for positioned network addons.
 * @author rubensworks
 */
public interface IPartTypeInterfacePositionedAddon<N extends IPositionedAddonsNetwork, T, P extends IPartTypeInterfacePositionedAddon<N, T, P, S>, S extends IPartTypeInterfacePositionedAddon.IState<N, T, P, S>> extends IPartType<P, S> {

    public Capability<N> getNetworkCapability();

    public Capability<T> getTargetCapability();

    public default boolean isTargetCapabilityValid(T capability) {
        return capability != null;
    }

    public default LazyOptional<T> getTargetCapabilityInstance(PartPos pos) {
        return TileHelpers.getCapability(pos.getPos(), pos.getSide(), getTargetCapability());
    }

    public default void scheduleNetworkObservation(PartTarget target, S state) {
        IPositionedAddonsNetwork positionedAddonsNetwork = state.getPositionedAddonsNetwork();
        if (positionedAddonsNetwork instanceof IPositionedAddonsNetworkIngredients) {
            ((IPositionedAddonsNetworkIngredients) positionedAddonsNetwork).scheduleObservationForced(
                    state.getChannelInterface(), target.getTarget());
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

    public default void addTargetToNetwork(INetwork network, PartPos posTarget, int priority, int channelInterface, S state) {
        Pair<N, Boolean> ret = addPositionToNetwork(network, posTarget, priority, channelInterface, state);
        N networkCapability = ret.getLeft();
        boolean validTargetCapability = ret.getRight();
        if (networkCapability != null) {
            state.setPositionedAddonsNetwork(networkCapability);
            state.setNetworks(network, NetworkHelpers.getPartNetworkChecked(network));
            state.setPos(posTarget);
            state.setValidTargetCapability(validTargetCapability);
            if (validTargetCapability) {
                onAddingPositionToNetwork(networkCapability, network, posTarget, priority, channelInterface, state);
            }
        }
    }

    public default void removeTargetFromNetwork(INetwork network, PartPos pos, S state) {
        removePositionFromNetwork(network, pos, state);
        state.setPositionedAddonsNetwork(null);
        state.setNetworks(null, null);
        state.setPos(null);
        state.setValidTargetCapability(false);
    }

    public default void updateTargetInNetwork(INetwork network, PartPos pos, int priority, int channelInterface, S state) {
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

    public static interface IState<N extends IPositionedAddonsNetwork, T, P extends IPartTypeInterfacePositionedAddon<N, T, P, S>, S extends IPartTypeInterfacePositionedAddon.IState<N, T, P, S>> extends IPartState<P> {
        public void setChannelInterface(int channelInterface);
        public int getChannelInterface();
        public Capability<T> getTargetCapability();
        public N getPositionedAddonsNetwork();
        public void setPositionedAddonsNetwork(N positionedAddonsNetwork);
        public boolean isValidTargetCapability();
        public void setValidTargetCapability(boolean validTargetCapability);
        public PartPos getPos();
        public void setPos(PartPos pos);

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

        public void setNetworks(@Nullable INetwork network, @Nullable IPartNetwork partNetwork);
        @Nullable
        public INetwork getNetwork();
        @Nullable
        public IPartNetwork getPartNetwork();

        public abstract T getCapabilityInstance();
    }

}
