package org.cyclops.integratedtunnels.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.part.IPartState;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.core.part.PartStateBase;

/**
 * @author rubensworks
 */
public class PartStateEnergy<P extends IPartType> extends PartStateBase<P> implements IEnergyStorage {

    private final boolean canReceive;
    private final boolean canExtract;
    private IEnergyNetwork energyNetwork;

    public PartStateEnergy(boolean canReceive, boolean canExtract) {
        this.canReceive = canReceive;
        this.canExtract = canExtract;
    }

    public IEnergyNetwork getEnergyNetwork() {
        return energyNetwork;
    }

    public void setEnergyNetwork(IEnergyNetwork energyNetwork) {
        this.energyNetwork = energyNetwork;
    }

    @Override
    public boolean hasCapability(Capability<?> capability) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability) {
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) this;
        }
        return super.getCapability(capability);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return this.canReceive && getEnergyNetwork() != null ? getEnergyNetwork().receiveEnergy(maxReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return this.canExtract && getEnergyNetwork() != null ? getEnergyNetwork().extractEnergy(maxExtract, simulate) : 0;
    }

    @Override
    public int getEnergyStored() {
        return getEnergyNetwork() != null ? getEnergyNetwork().getEnergyStored() : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return getEnergyNetwork() != null ? getEnergyNetwork().getMaxEnergyStored() : 0;
    }

    @Override
    public boolean canExtract() {
        return this.canExtract;
    }

    @Override
    public boolean canReceive() {
        return this.canReceive;
    }
}
