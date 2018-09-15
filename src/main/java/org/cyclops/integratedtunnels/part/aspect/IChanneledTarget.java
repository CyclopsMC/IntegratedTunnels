package org.cyclops.integratedtunnels.part.aspect;

import org.apache.logging.log4j.Level;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.PartStateException;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

/**
 * A helper class for movement targets with a certain network type.
 * @author rubensworks
 */
public interface IChanneledTarget<N extends IPositionedAddonsNetwork> {

    public N getChanneledNetwork();

    public boolean hasValidTarget();

    public PartStateRoundRobin<?> getPartState();

    public int getChannel();

    public boolean isRoundRobin();

    public void preTransfer();

    public void postTransfer();

    public static INetwork getNetworkChecked(PartPos pos) throws PartStateException {
        INetwork network = NetworkHelpers.getNetwork(pos.getPos().getWorld(), pos.getPos().getBlockPos(), pos.getSide());
        if (network == null) {
            IntegratedDynamics.clog(Level.ERROR, "Could not get the network for transfer as no network was found.");
            throw new PartStateException(pos.getPos(), pos.getSide());
        }
        return network;
    }

}
