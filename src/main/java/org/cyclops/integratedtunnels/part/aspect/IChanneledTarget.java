package org.cyclops.integratedtunnels.part.aspect;

import org.apache.logging.log4j.Level;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.PartStateException;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * A helper class for movement targets with a certain network type.
 * @author rubensworks
 */
public interface IChanneledTarget<N extends IPositionedAddonsNetwork, T> {

    public INetwork getNetwork();

    public N getChanneledNetwork();

    public boolean hasValidTarget();

    public PartStateRoundRobin<?> getPartState();
    public default PartStatePositionedAddon<?, ?, T> getPartStatePositionedAddon() {
        return (PartStatePositionedAddon<?, ?, T>) getPartState();
    }

    public int getChannel();

    public boolean isRoundRobin();

    public boolean isCraftIfFailed();

    public boolean isPassiveIO();

    public void preTransfer();

    public void postTransfer();

    public static INetwork getNetworkChecked(PartPos pos) throws PartStateException {
        INetwork network = NetworkHelpers.getNetwork(pos.getPos().getWorld(true), pos.getPos().getBlockPos(), pos.getSide()).orElse(null);
        if (network == null) {
            IntegratedDynamics.clog(Level.ERROR, "Could not get the network for transfer as no network was found.");
            throw new PartStateException(pos.getPos(), pos.getSide());
        }
        return network;
    }

    @Nullable
    public static PartStateRoundRobin<?> getPartState(PartPos center) {
        PartHelpers.PartStateHolder<?, ?> partStateHolder = PartHelpers.getPart(center);
        if (partStateHolder == null) {
            return null;
        }
        return (PartStateRoundRobin<?>) partStateHolder.getState();
    }

}
