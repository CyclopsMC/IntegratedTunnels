package org.cyclops.integratedtunnels.core.part;

import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;
import org.cyclops.integrateddynamics.api.network.PositionedAddonsNetworkIngredientsFilter;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;

import javax.annotation.Nullable;

/**
 * A part state for handling addon import and export.
 * @author rubensworks
 */
public class PartStatePositionedAddon<P extends IPartTypeWriter, N extends IPositionedAddonsNetwork, T> extends PartStateRoundRobin<P> {

    private final boolean canReceive;
    private final boolean canExtract;
    @Nullable
    private N positionedAddonsNetwork;
    @Nullable
    private PositionedAddonsNetworkIngredientsFilter<T> storageFilter;

    public PartStatePositionedAddon(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize);
        this.canReceive = canReceive;
        this.canExtract = canExtract;
    }

    @Nullable
    public N getPositionedAddonsNetwork() {
        return positionedAddonsNetwork;
    }

    public void setPositionedAddonsNetwork(@Nullable N positionedAddonsNetwork) {
        this.positionedAddonsNetwork = positionedAddonsNetwork;
    }

    public boolean canReceive() {
        return canReceive;
    }

    public boolean canExtract() {
        return canExtract;
    }

    @Nullable
    public PositionedAddonsNetworkIngredientsFilter<T> getStorageFilter() {
        return storageFilter;
    }

    public void setStorageFilter(@Nullable PositionedAddonsNetworkIngredientsFilter<T> storageFilter) {
        this.storageFilter = storageFilter;
    }
}
