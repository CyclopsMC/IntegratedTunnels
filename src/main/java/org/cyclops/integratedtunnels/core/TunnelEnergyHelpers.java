package org.cyclops.integratedtunnels.core;

import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.ingredient.storage.IngredientStorageHelpers;

/**
 * @author Ruben Taelman (ruben.taelman@ugent.be)
 */
public class TunnelEnergyHelpers {

    /**
     * Move energy from source to target.
     * @param source The source energy storage.
     * @param target The target energy storage.
     * @param amount The maximum amount to transfer.
     * @param exact If only the exact amount is allowed to be transferred.
     * @return The moved energy amount.
     */
    public static int moveEnergy(IIngredientComponentStorage<Integer, Boolean> source, IIngredientComponentStorage<Integer, Boolean> target, int amount, boolean exact) {
        int canSend = IngredientStorageHelpers.moveIngredients(source, target, amount, true, true);
        return canSend > 0 && (!exact || canSend == amount) ? IngredientStorageHelpers.moveIngredients(source, target, amount, true, false) : 0;
    }

}
