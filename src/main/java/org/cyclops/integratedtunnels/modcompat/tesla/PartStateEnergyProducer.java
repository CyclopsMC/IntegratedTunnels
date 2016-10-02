package org.cyclops.integratedtunnels.modcompat.tesla;

import net.darkhax.tesla.api.ITeslaProducer;
import org.cyclops.integratedtunnels.part.PartStateEnergy;

/**
 * @author rubensworks
 */
public class PartStateEnergyProducer implements ITeslaProducer {

    private final PartStateEnergy partState;

    public PartStateEnergyProducer(PartStateEnergy partState) {
        this.partState = partState;
    }

    @Override
    public long takePower(long power, boolean simulated) {
        return partState.canExtract() ? partState.extractEnergy((int) Math.min(power, Integer.MAX_VALUE), simulated) : 0;
    }
}
