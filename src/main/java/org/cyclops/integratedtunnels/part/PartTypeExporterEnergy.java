package org.cyclops.integratedtunnels.part;

import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integratedtunnels.core.part.PartTypeIOEnergy;

/**
 * @author rubensworks
 */
public class PartTypeExporterEnergy extends PartTypeIOEnergy<PartTypeExporterEnergy, PartStateEnergy<PartTypeExporterEnergy>> {
    public PartTypeExporterEnergy(String name) {
        super(name);
    }

    @Override
    protected void handleEnergyConnection(IEnergyNetwork energyNetwork, IEnergyStorage energyStorage) {
        int ENERGY_RATE = 80; // TODO

        int toSend = Math.min(ENERGY_RATE, energyNetwork.getEnergyStored());
        int sent = energyStorage.receiveEnergy(toSend, false);
        energyNetwork.extractEnergy(sent, false);
    }

    @Override
    protected PartStateEnergy<PartTypeExporterEnergy> constructDefaultState() {
        return new PartStateEnergy<PartTypeExporterEnergy>(false, true);
    }
}
