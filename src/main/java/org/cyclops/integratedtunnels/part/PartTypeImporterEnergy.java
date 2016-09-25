package org.cyclops.integratedtunnels.part;

import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integratedtunnels.core.EnergyHelpers;
import org.cyclops.integratedtunnels.core.part.PartTypeIOEnergy;

/**
 * @author rubensworks
 */
public class PartTypeImporterEnergy extends PartTypeIOEnergy<PartTypeImporterEnergy, PartStateEnergy<PartTypeImporterEnergy>> {
    public PartTypeImporterEnergy(String name) {
        super(name);
    }

    @Override
    protected void handleEnergyConnection(IEnergyNetwork energyNetwork, IEnergyStorage energyStorage) {
        int energyRate = Integer.MAX_VALUE;
        EnergyHelpers.moveEnergy(energyStorage, energyNetwork, energyRate);
    }

    @Override
    protected PartStateEnergy<PartTypeImporterEnergy> constructDefaultState() {
        return new PartStateEnergy<PartTypeImporterEnergy>(true, false);
    }
}
