package org.cyclops.integratedtunnels.part;

import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.part.write.PartStateWriterBase;

/**
 * A part state for handling energy import and export.
 * It also acts as an energy capability that can be added to itself.
 * @author rubensworks
 */
public class PartStateEnergy<P extends IPartTypeWriter> extends PartStateWriterBase<P> implements IEnergyStorage {

    private final boolean canReceive;
    private final boolean canExtract;
    private IEnergyNetwork energyNetwork;

    public PartStateEnergy(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize);
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
