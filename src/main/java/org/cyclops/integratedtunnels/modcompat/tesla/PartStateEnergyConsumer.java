package org.cyclops.integratedtunnels.modcompat.tesla;

import net.darkhax.tesla.api.ITeslaConsumer;
import org.cyclops.integratedtunnels.part.PartStateEnergy;

/**
 * @author rubensworks
 */
public class PartStateEnergyConsumer implements ITeslaConsumer {

    private final PartStateEnergy partState;

    public PartStateEnergyConsumer(PartStateEnergy partState) {
        this.partState = partState;
    }

    @Override
    public long givePower(long power, boolean simulated) {
        return partState.canReceive() ? partState.receiveEnergy((int) Math.min(power, Integer.MAX_VALUE), simulated) : 0;
    }
}
