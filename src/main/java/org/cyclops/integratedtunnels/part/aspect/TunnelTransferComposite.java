package org.cyclops.integratedtunnels.part.aspect;

import java.util.Arrays;

/**
 * @author rubensworks
 */
public class TunnelTransferComposite implements ITunnelTransfer {

    private final ITunnelTransfer[] values;

    public TunnelTransferComposite(ITunnelTransfer... values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TunnelTransferComposite)) {
            return false;
        }
        TunnelTransferComposite that = (TunnelTransferComposite) obj;
        return Arrays.equals(this.values, that.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
