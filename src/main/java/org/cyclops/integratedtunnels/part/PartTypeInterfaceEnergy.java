package org.cyclops.integratedtunnels.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.helper.EnergyHelpers;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.GeneralConfig;
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
    protected LazyOptional<IEnergyStorage> getTargetCapabilityInstance(PartPos pos) {
        return EnergyHelpers.getEnergyStorage(pos);
    }

    @Override
    protected PartTypeInterfaceEnergy.State constructDefaultState() {
        return new PartTypeInterfaceEnergy.State();
    }
    
    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceEnergyBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<PartTypeInterfaceEnergy, IEnergyNetwork, IEnergyStorage> implements IEnergyStorage {

        @Override
        protected Capability<IEnergyStorage> getTargetCapability() {
            return CapabilityEnergy.ENERGY;
        }

        protected IEnergyStorage getEnergyStorage() {
            return getPositionedAddonsNetwork().getChannelExternal(CapabilityEnergy.ENERGY, getChannel());
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getEnergyStorage().receiveEnergy(maxReceive, simulate);
            enablePosition();
            return ret;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getEnergyStorage().extractEnergy(maxExtract, simulate);
            enablePosition();
            return ret;
        }

        @Override
        public int getEnergyStored() {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getEnergyStorage().getEnergyStored();
            enablePosition();
            return ret;
        }

        @Override
        public int getMaxEnergyStored() {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getEnergyStorage().getMaxEnergyStored();
            enablePosition();
            return ret;
        }

        @Override
        public boolean canExtract() {
            if (!isNetworkAndPositionValid()) {
                return false;
            }
            disablePosition();
            boolean ret = getEnergyStorage().canExtract();
            enablePosition();
            return ret;
        }

        @Override
        public boolean canReceive() {
            if (!isNetworkAndPositionValid()) {
                return false;
            }
            disablePosition();
            boolean ret = getEnergyStorage().canReceive();
            enablePosition();
            return ret;
        }

    }
}
