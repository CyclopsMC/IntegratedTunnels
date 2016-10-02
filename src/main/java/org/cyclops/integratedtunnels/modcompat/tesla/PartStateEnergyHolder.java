package org.cyclops.integratedtunnels.modcompat.tesla;

import net.darkhax.tesla.api.ITeslaHolder;
import org.cyclops.integratedtunnels.part.PartStateEnergy;

/**
 * @author rubensworks
 */
public class PartStateEnergyHolder implements ITeslaHolder {

    private final PartStateEnergy partState;

    public PartStateEnergyHolder(PartStateEnergy partState) {
        this.partState = partState;
    }

    @Override
    public long getStoredPower() {
        return partState.getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return partState.getMaxEnergyStored();
    }
}
