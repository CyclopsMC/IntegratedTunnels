package org.cyclops.integratedtunnels.core.part;

import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;

/**
 * A part state for handling addon import and export.
 * @author rubensworks
 */
public class PartStatePositionedAddon<P extends IPartTypeWriter, T extends IPositionedAddonsNetwork> extends PartStateWriterBase<P> {

    private final boolean canReceive;
    private final boolean canExtract;
    private T positionedAddonsNetwork;

    public PartStatePositionedAddon(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize);
        this.canReceive = canReceive;
        this.canExtract = canExtract;
    }

    public T getPositionedAddonsNetwork() {
        return positionedAddonsNetwork;
    }

    public void setPositionedAddonsNetwork(T positionedAddonsNetwork) {
        this.positionedAddonsNetwork = positionedAddonsNetwork;
    }

    public boolean canReceive() {
        return canReceive;
    }

    public boolean canExtract() {
        return canExtract;
    }
}
