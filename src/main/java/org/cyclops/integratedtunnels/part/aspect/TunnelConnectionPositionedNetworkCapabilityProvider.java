package org.cyclops.integratedtunnels.part.aspect;

import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A tunnel connection over a network at a certain position.
 * @author rubensworks
 */
public class TunnelConnectionPositionedNetworkCapabilityProvider extends TunnelConnectionPositionedNetwork {

    @Nullable
    private final Object capabilityProvider;

    public TunnelConnectionPositionedNetworkCapabilityProvider(INetwork network, int channel, PartPos pos,
                                                               ITunnelTransfer transfer,
                                                               @Nullable Object capabilityProvider) {
        super(network, channel, pos, transfer);
        this.capabilityProvider = capabilityProvider;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TunnelConnectionPositionedNetworkCapabilityProvider)) {
            return false;
        }
        TunnelConnectionPositionedNetworkCapabilityProvider that = (TunnelConnectionPositionedNetworkCapabilityProvider) obj;
        return Objects.equals(this.capabilityProvider, that.capabilityProvider) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.capabilityProvider == null ? 0 : this.capabilityProvider.hashCode() ^ super.hashCode();
    }
}
