package org.cyclops.integratedtunnels.part;

import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;

/**
 * A base world part state.
 * @author rubensworks
 */
public class PartStateWorld<P extends IPartTypeWriter> extends PartStateWriterBase<P> {

    public PartStateWorld(int inventorySize) {
        super(inventorySize);
    }

    @Override
    protected int getDefaultUpdateInterval() {
        return 10;
    }
}
