package org.cyclops.integratedtunnels.core;

import net.minecraftforge.energy.IEnergyStorage;

/**
 * @author Ruben Taelman (ruben.taelman@ugent.be)
 */
public class TunnelEnergyHelpers {

    /**
     * Move energy from source to target.
     * @param source The source energy storage.
     * @param target The target energy storage.
     * @param amount The maximum amount to transfer.
     * @param simulate If the transfer should be simulated.
     * @return The moved energy amount.
     */
    public static int moveEnergySingle(IEnergyStorage source, IEnergyStorage target, int amount, boolean simulate) {
        return target.receiveEnergy(source.extractEnergy(amount, simulate), simulate);
    }

    /**
     * Move energy from source to target.
     * @param source The source energy storage.
     * @param target The target energy storage.
     * @param amount The maximum amount to transfer.
     * @return The moved energy amount.
     */
    public static int moveEnergy(IEnergyStorage source, IEnergyStorage target, int amount) {
        int canSend = moveEnergySingle(source, target, amount, true);
        return canSend > 0 ? moveEnergySingle(source, target, canSend, false) : 0;
    }

}
