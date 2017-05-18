package org.cyclops.integratedtunnels.part;

import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.integrateddynamics.GeneralConfig;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

/**
 * A part state for handling energy import and export.
 * It also acts as an energy capability that can be added to itself.
 * @author rubensworks
 */
public class PartStateEnergy<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IEnergyNetwork> implements IEnergyStorage {

    public PartStateEnergy(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        maxReceive = Math.min(maxReceive, GeneralConfig.energyRateLimit);
        return this.canReceive() && getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().receiveEnergy(maxReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        maxExtract = Math.min(maxExtract, GeneralConfig.energyRateLimit);
        return this.canExtract() && getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().extractEnergy(maxExtract, simulate) : 0;
    }

    @Override
    public int getEnergyStored() {
        return getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().getEnergyStored() : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().getMaxEnergyStored() : 0;
    }
}
