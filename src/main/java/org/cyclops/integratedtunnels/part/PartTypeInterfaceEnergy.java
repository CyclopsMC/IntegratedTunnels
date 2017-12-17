package org.cyclops.integratedtunnels.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.helper.EnergyHelpers;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

/**
 * Interface for energy storages.
 * @author rubensworks
 */
public class PartTypeInterfaceEnergy extends PartTypeInterfacePositionedAddon<IEnergyNetwork, IEnergyStorage, PartTypeInterfaceEnergy, PartTypeInterfaceEnergy.State> {
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
    protected IEnergyStorage getTargetCapabilityInstance(PartPos pos) {
        return EnergyHelpers.getEnergyStorage(pos);
    }

    @Override
    protected PartTypeInterfaceEnergy.State constructDefaultState() {
        return new PartTypeInterfaceEnergy.State();
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<PartTypeInterfaceEnergy, IEnergyNetwork, IEnergyStorage> implements IEnergyStorage {

        @Override
        protected Capability<IEnergyStorage> getTargetCapability() {
            return CapabilityEnergy.ENERGY;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            disablePosition();
            int ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().getChannel(getChannel()).receiveEnergy(maxReceive, simulate) : 0;
            enablePosition();
            return ret;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            disablePosition();
            int ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().getChannel(getChannel()).extractEnergy(maxExtract, simulate) : 0;
            enablePosition();
            return ret;
        }

        @Override
        public int getEnergyStored() {
            disablePosition();
            int ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().getChannel(getChannel()).getEnergyStored() : 0;
            enablePosition();
            return ret;
        }

        @Override
        public int getMaxEnergyStored() {
            disablePosition();
            int ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().getChannel(getChannel()).getMaxEnergyStored() : 0;
            enablePosition();
            return ret;
        }

        @Override
        public boolean canExtract() {
            disablePosition();
            boolean ret = getPositionedAddonsNetwork() != null
                    && getPositionedAddonsNetwork().getChannel(getChannel()).canExtract();
            enablePosition();
            return ret;
        }

        @Override
        public boolean canReceive() {
            disablePosition();
            boolean ret = getPositionedAddonsNetwork() != null
                    && getPositionedAddonsNetwork().getChannel(getChannel()).canReceive();
            enablePosition();
            return ret;
        }

    }
}
