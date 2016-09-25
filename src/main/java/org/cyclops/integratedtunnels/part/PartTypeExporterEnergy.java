package org.cyclops.integratedtunnels.part;

import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integratedtunnels.core.EnergyHelpers;
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
        int energyRate = Integer.MAX_VALUE;
        EnergyHelpers.moveEnergy(energyNetwork, energyStorage, energyRate);
    }

    @Override
    protected PartStateEnergy<PartTypeExporterEnergy> constructDefaultState() {
        return new PartStateEnergy<PartTypeExporterEnergy>(false, true);
    }
}
