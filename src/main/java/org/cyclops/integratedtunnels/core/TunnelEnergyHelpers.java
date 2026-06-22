package org.cyclops.integratedtunnels.core;

import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.ingredient.storage.IngredientStorageHelpers;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;

/**
 * @author Ruben Taelman (ruben.taelman@ugent.be)
 */
public class TunnelEnergyHelpers {

    /**
     * Move energy from source to target.
     * @param network The network in which the movement is happening.
     * @param ingredientsNetwork The ingredients network in which the movement is happening.
     * @param channel The channel.
     * @param source The source energy storage.
     * @param target The target energy storage.
     * @param amount The maximum amount to transfer.
     * @param exact If only the exact amount is allowed to be transferred.
     * @param craftIfFailed If energy should be crafted if transfer failed.
     * @return The moved energy amount.
     * @throws EvaluationException If illegal movement occured and further movement should stop.
     */
    public static long moveEnergy(INetwork network, IPositionedAddonsNetworkIngredients<Long, Boolean> ingredientsNetwork,
                                 int channel, IIngredientComponentStorage<Long, Boolean> source,
                                 IIngredientComponentStorage<Long, Boolean> target, long amount, boolean exact,
                                 boolean craftIfFailed) throws EvaluationException {
        long moved;
        try (var tx = Transaction.openRoot()) {
            moved = IngredientStorageHelpers.moveIngredients(source, target, amount, exact, tx);
            tx.commit();
        }

        // Schedule a new observation for the network, as its contents may have changed
        ingredientsNetwork.scheduleObservation();

        if (craftIfFailed && moved == 0 && target.insert(amount, true) == amount) {
            TunnelHelpers.requestCrafting(network, ingredientsNetwork, channel, amount, exact);
        }

        return moved;
    }

}
