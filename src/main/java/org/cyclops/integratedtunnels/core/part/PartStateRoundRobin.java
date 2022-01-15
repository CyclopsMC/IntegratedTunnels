package org.cyclops.integratedtunnels.core.part;

import org.cyclops.integrateddynamics.api.network.IPartPosIteratorHandler;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;

/**
 * A writer part state that maintains an iterator for round-robin iteration over interfaces.
 *
 * @author rubensworks
 */
public class PartStateRoundRobin<P extends IPartTypeWriter> extends PartStateWriterBase<P> {

    private IPartPosIteratorHandler partPosIteratorHandler = null;

    public PartStateRoundRobin(int inventorySize) {
        super(inventorySize);
    }

    public void setPartPosIteratorHandler(IPartPosIteratorHandler partPosIteratorHandler) {
        this.partPosIteratorHandler = partPosIteratorHandler;
    }

    public IPartPosIteratorHandler getPartPosIteratorHandler() {
        return partPosIteratorHandler;
    }
}
