package org.cyclops.integratedtunnels.part.aspect;

import org.cyclops.integrateddynamics.api.network.IPartPosIteratorHandler;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.core.network.PartPosIteratorHandlerRoundRobin;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

/**
 * A helper class for movement targets with a certain network type.
 * @author rubensworks
 */
public abstract class ChanneledTarget<N extends IPositionedAddonsNetwork> implements IChanneledTarget<N> {

    private final N channeledNetwork;
    private final PartStateRoundRobin<?> partState;
    private final int channel;
    private final boolean roundRobin;

    public ChanneledTarget(N channeledNetwork, PartStateRoundRobin<?> partState, int channel, boolean roundRobin) {
        this.channeledNetwork = channeledNetwork;
        this.partState = partState;
        this.channel = channel;
        this.roundRobin = roundRobin;
    }

    @Override
    public N getChanneledNetwork() {
        return channeledNetwork;
    }

    @Override
    public PartStateRoundRobin<?> getPartState() {
        return partState;
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public boolean isRoundRobin() {
        return roundRobin;
    }

    @Override
    public void preTransfer() {
        if (isRoundRobin()) {
            IPartPosIteratorHandler handler = getPartState().getPartPosIteratorHandler();

            if (handler == null) {
                handler = new PartPosIteratorHandlerRoundRobin();
            }

            getChanneledNetwork().setPartPosIteratorHandler(handler);
        }
    }

    @Override
    public void postTransfer() {
        if (isRoundRobin()) {
            IPartPosIteratorHandler handler = getChanneledNetwork().getPartPosIteratorHandler();
            if (handler != null) {
                // Save the iterator state (as it may have changed) in the part state
                getPartState().setPartPosIteratorHandler(handler);

                // Reset the network's iterator, to avoid influencing other parts.
                getChanneledNetwork().setPartPosIteratorHandler(null);
            }
        }
    }

}
