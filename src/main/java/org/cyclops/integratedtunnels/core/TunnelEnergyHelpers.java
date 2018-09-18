package org.cyclops.integratedtunnels.core;

import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.ingredient.storage.IngredientStorageHelpers;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;

/**
 * @author Ruben Taelman (ruben.taelman@ugent.be)
 */
public class TunnelEnergyHelpers {

    /**
     * Move energy from source to target.
     * @param network The network in which the movement is happening.
     * @param source The source energy storage.
     * @param target The target energy storage.
     * @param amount The maximum amount to transfer.
     * @param exact If only the exact amount is allowed to be transferred.
     * @return The moved energy amount.
     */
    public static int moveEnergy(IPositionedAddonsNetworkIngredients<Integer, Boolean> network,
                                 IIngredientComponentStorage<Integer, Boolean> source,
                                 IIngredientComponentStorage<Integer, Boolean> target, int amount, boolean exact) {
        int moved = IngredientStorageHelpers.moveIngredients(source, target, amount, exact, false);

        // Schedule a new observation for the network, as its contents may have changed
        network.scheduleObservation();

        return moved;
    }

}
