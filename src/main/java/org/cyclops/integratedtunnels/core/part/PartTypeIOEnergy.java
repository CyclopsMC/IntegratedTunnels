package org.cyclops.integratedtunnels.core.part;

import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.core.helper.EnergyHelpers;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.part.PartStateEnergy;

/**
 * Base part for interfaces.
 * @author rubensworks
 */
public abstract class PartTypeIOEnergy<P extends PartTypeIOEnergy<P, S>, S extends PartStateEnergy<P>> extends PartTypeTunnel<P, PartStateEnergy<P>> {
    public PartTypeIOEnergy(String name) {
        super(name);
    }

    @Override
    public boolean isUpdate(PartStateEnergy<P> state) {
        return true;
    }

    @Override
    public void update(INetwork network, IPartNetwork partNetwork, PartTarget target, PartStateEnergy<P> state) {
        super.update(network, partNetwork, target, state);
        PartPos pos = target.getTarget();
        IEnergyStorage energyStorage = EnergyHelpers.getEnergyStorage(pos.getPos().getWorld(), pos.getPos().getBlockPos(), pos.getSide());
        if (energyStorage != null && network.hasCapability(Capabilities.NETWORK_ENERGY)) {
            IEnergyNetwork energyNetwork = network.getCapability(Capabilities.NETWORK_ENERGY);
            handleEnergyConnection(energyNetwork, energyStorage);
        }
    }

    protected abstract void handleEnergyConnection(IEnergyNetwork energyNetwork, IEnergyStorage energyStorage);

    @Override
    public void afterNetworkReAlive(INetwork network, IPartNetwork partNetwork, PartTarget target, PartStateEnergy state) {
        super.afterNetworkReAlive(network, partNetwork, target, state);
        if (network.hasCapability(Capabilities.NETWORK_ENERGY)) {
            state.setEnergyNetwork(network.getCapability(Capabilities.NETWORK_ENERGY));
        }
    }
}
