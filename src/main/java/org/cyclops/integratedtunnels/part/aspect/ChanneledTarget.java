package org.cyclops.integratedtunnels.part.aspect;

import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartPosIteratorHandler;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.core.network.PartPosIteratorHandlerRoundRobin;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

import javax.annotation.Nullable;

/**
 * A helper class for movement targets with a certain network type.
 * @author rubensworks
 */
public abstract class ChanneledTarget<N extends IPositionedAddonsNetwork, T> implements IChanneledTarget<N, T> {

    private final INetwork network;
    private final N channeledNetwork;
    @Nullable
    private final PartStatePositionedAddon<?, ?, T> partState;
    private final int channel;
    private final boolean roundRobin;
    private final boolean craftIfFailed;
    private final boolean passiveIO;

    public ChanneledTarget(INetwork network, N channeledNetwork, @Nullable PartStatePositionedAddon<?, ?, T> partState, int channel,
                           boolean roundRobin, boolean craftIfFailed, boolean passiveIO) {
        this.network = network;
        this.channeledNetwork = channeledNetwork;
        this.partState = partState;
        this.channel = channel;
        this.roundRobin = roundRobin;
        this.craftIfFailed = craftIfFailed;
        this.passiveIO = passiveIO;
    }

    @Override
    public INetwork getNetwork() {
        return network;
    }

    @Override
    public N getChanneledNetwork() {
        return channeledNetwork;
    }

    @Nullable
    @Override
    public PartStatePositionedAddon<?, ?, T> getPartState() {
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
    public boolean isCraftIfFailed() {
        return craftIfFailed;
    }

    @Override
    public boolean isPassiveIO() {
        return passiveIO;
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
