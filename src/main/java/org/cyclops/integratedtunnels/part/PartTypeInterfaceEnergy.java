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
import org.cyclops.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon;
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
    public Capability<IEnergyNetwork> getNetworkCapability() {
        return Capabilities.NETWORK_ENERGY;
    }

    @Override
    public Capability<IEnergyStorage> getTargetCapability() {
        return CapabilityEnergy.ENERGY;
    }

    @Override
    public LazyOptional<IEnergyStorage> getTargetCapabilityInstance(PartPos pos) {
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

    public static class State extends PartTypeInterfacePositionedAddon.State<IEnergyNetwork, IEnergyStorage, PartTypeInterfaceEnergy, PartTypeInterfaceEnergy.State> {

        @Override
        public Capability<IEnergyStorage> getTargetCapability() {
            return CapabilityEnergy.ENERGY;
        }

        @Override
        public IEnergyStorage getCapabilityInstance() {
            return new PartTypeInterfaceEnergy.EnergyStorage(this);
        }
    }

    public static class EnergyStorage implements IEnergyStorage {
        private final IPartTypeInterfacePositionedAddon.IState<IEnergyNetwork, IEnergyStorage, ?, ?> state;

        public EnergyStorage(IState<IEnergyNetwork, IEnergyStorage, ?, ?> state) {
            this.state = state;
        }

        protected IEnergyStorage getEnergyStorage() {
            return state.getPositionedAddonsNetwork().getChannelExternal(CapabilityEnergy.ENERGY, state.getChannel());
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getEnergyStorage().receiveEnergy(maxReceive, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getEnergyStorage().extractEnergy(maxExtract, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public int getEnergyStored() {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getEnergyStorage().getEnergyStored();
            state.enablePosition();
            return ret;
        }

        @Override
        public int getMaxEnergyStored() {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getEnergyStorage().getMaxEnergyStored();
            state.enablePosition();
            return ret;
        }

        @Override
        public boolean canExtract() {
            if (!state.isNetworkAndPositionValid()) {
                return false;
            }
            state.disablePosition();
            boolean ret = getEnergyStorage().canExtract();
            state.enablePosition();
            return ret;
        }

        @Override
        public boolean canReceive() {
            if (!state.isNetworkAndPositionValid()) {
                return false;
            }
            state.disablePosition();
            boolean ret = getEnergyStorage().canReceive();
            state.enablePosition();
            return ret;
        }
    }
}
