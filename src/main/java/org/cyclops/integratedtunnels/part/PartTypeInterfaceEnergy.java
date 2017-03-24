package org.cyclops.integratedtunnels.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.core.part.PartStateEmpty;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

/**
 * Interface for energy storages.
 * @author rubensworks
 */
public class PartTypeInterfaceEnergy extends PartTypeInterfacePositionedAddon<IEnergyNetwork, IEnergyStorage, PartTypeInterfaceEnergy, PartStateEmpty<PartTypeInterfaceEnergy>> {
    public PartTypeInterfaceEnergy(String name) {
        super(name);
    }

    @Override
    protected Capability<IEnergyNetwork> getNetworkCapability() {
        return Capabilities.NETWORK_ENERGY;
    }

    @Override
    protected Capability<IEnergyStorage> getTargetCapability() {
        return CapabilityEnergy.ENERGY;
    }

    @Override
    protected PartStateEmpty<PartTypeInterfaceEnergy> constructDefaultState() {
        return new PartStateEmpty<PartTypeInterfaceEnergy>();
    }
}
