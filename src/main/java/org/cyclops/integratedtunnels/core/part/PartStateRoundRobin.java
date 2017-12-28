package org.cyclops.integratedtunnels.core.part;

import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;

/**
 * A writer part state that maintains an iterator for round-robin iteration over interfaces.
 *
 * @author rubensworks
 */
public class PartStateRoundRobin<P extends IPartTypeWriter> extends PartStateWriterBase<P> {

    private IPositionedAddonsNetwork.PositionsIterator positionsIterator = null;

    public PartStateRoundRobin(int inventorySize) {
        super(inventorySize);
    }

    public void setPositionsIterator(IPositionedAddonsNetwork.PositionsIterator positionsIterator) {
        this.positionsIterator = positionsIterator;
    }

    public IPositionedAddonsNetwork.PositionsIterator getPositionsIterator() {
        return positionsIterator;
    }
}
