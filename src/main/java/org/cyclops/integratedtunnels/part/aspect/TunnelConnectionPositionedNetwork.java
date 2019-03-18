package org.cyclops.integratedtunnels.part.aspect;

import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;

/**
 * A tunnel connection over a network at a certain position.
 * @author rubensworks
 */
public class TunnelConnectionPositionedNetwork implements ITunnelConnection {

    private final INetwork network;
    private final int channel;
    private final PartPos pos;
    private final ITunnelTransfer transfer;

    public TunnelConnectionPositionedNetwork(INetwork network, int channel, PartPos pos, ITunnelTransfer transfer) {
        this.network = network;
        this.channel = channel;
        this.pos = pos;
        this.transfer = transfer;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TunnelConnectionPositionedNetwork)) {
            return false;
        }
        TunnelConnectionPositionedNetwork that = (TunnelConnectionPositionedNetwork) obj;
        return this.network == that.network && this.channel == that.channel
                && this.pos.equals(that.pos) && this.transfer.equals(that.transfer);
    }

    @Override
    public int hashCode() {
        return this.network.hashCode() ^ this.channel ^ this.pos.hashCode() ^ this.transfer.hashCode();
    }
}
