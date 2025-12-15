package org.cyclops.integratedtunnels.part;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.cyclops.integrateddynamics.GeneralConfig;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.core.TunnelHelpers;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

import java.util.Optional;

/**
 * A part state for handling energy import and export.
 * It also acts as an energy capability that can be added to itself.
 * @author rubensworks
 */
public class PartStateEnergy<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IEnergyNetwork, Long> implements EnergyHandler {

    public PartStateEnergy(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public <T> Optional<T> getCapability(P partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
        if (capability == Capabilities.Energy.PART) {
            return Optional.of((T) this);
        }
        return super.getCapability(partType, capability, network, partNetwork, target);
    }

    protected EnergyHandler getEnergyStorage() {
        return getPositionedAddonsNetwork().getChannelExternal(net.neoforged.neoforge.capabilities.Capabilities.Energy.BLOCK, TunnelHelpers.getPassiveInteractionChannel(this));
    }

    @Override
    public long getAmountAsLong() {
        if (getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            long stored = getEnergyStorage().getAmountAsLong();
            if (getStorageFilter().testView(stored)) {
                return stored;
            }
        }
        return 0;
    }

    @Override
    public long getCapacityAsLong() {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null ? getEnergyStorage().getCapacityAsLong() : 0;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        amount = Math.min(amount, GeneralConfig.energyRateLimit);
        return this.canReceive() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testInsertion((long) amount)
                ? getEnergyStorage().insert(amount, transaction) : 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        amount = Math.min(amount, GeneralConfig.energyRateLimit);
        return this.canExtract() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testExtraction((long) amount)
                ? getEnergyStorage().extract(amount, transaction) : 0;
    }
}
