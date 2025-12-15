package org.cyclops.integratedtunnels.part;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.helper.EnergyHelpers;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

import java.util.Optional;

/**
 * Interface for energy storages.
 * @author rubensworks
 */
public class PartTypeInterfaceEnergy extends PartTypeInterfacePositionedAddon<IEnergyNetwork, EnergyHandler, PartTypeInterfaceEnergy, PartTypeInterfaceEnergy.State> {
    public PartTypeInterfaceEnergy(String name) {
        super(name);
    }

    @Override
    public NetworkCapability<IEnergyNetwork> getNetworkCapability() {
        return org.cyclops.integrateddynamics.Capabilities.EnergyNetwork.NETWORK;
    }

    @Override
    public PartCapability<EnergyHandler> getPartCapability() {
        return Capabilities.Energy.PART;
    }

    @Override
    public BlockCapability<EnergyHandler, Direction> getBlockCapability() {
        return net.neoforged.neoforge.capabilities.Capabilities.Energy.BLOCK;
    }

    @Override
    public Optional<EnergyHandler> getTargetCapabilityInstance(PartPos pos) {
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

    public static class State extends PartTypeInterfacePositionedAddon.State<IEnergyNetwork, EnergyHandler, PartTypeInterfaceEnergy, PartTypeInterfaceEnergy.State> {

        @Override
        public PartCapability<EnergyHandler> getTargetCapability() {
            return Capabilities.Energy.PART;
        }

        @Override
        public EnergyHandler getCapabilityInstance() {
            return new PartTypeInterfaceEnergy.EnergyStorage(this);
        }
    }

    public static class EnergyStorage implements EnergyHandler {
        private final IPartTypeInterfacePositionedAddon.IState<IEnergyNetwork, EnergyHandler, ?, ?> state;

        public EnergyStorage(IState<IEnergyNetwork, EnergyHandler, ?, ?> state) {
            this.state = state;
        }

        protected EnergyHandler getEnergyStorage() {
            return state.getPositionedAddonsNetwork().getChannelExternal(net.neoforged.neoforge.capabilities.Capabilities.Energy.BLOCK, state.getChannel());
        }

        @Override
        public int insert(int amount, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getEnergyStorage().insert(amount, transaction);
            state.enablePosition();
            return ret;
        }

        @Override
        public int extract(int amount, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getEnergyStorage().extract(amount, transaction);
            state.enablePosition();
            return ret;
        }

        @Override
        public long getAmountAsLong() {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            long ret = getEnergyStorage().getAmountAsLong();
            state.enablePosition();
            return ret;
        }

        @Override
        public long getCapacityAsLong() {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            long ret = getEnergyStorage().getCapacityAsLong();
            state.enablePosition();
            return ret;
        }
    }
}
