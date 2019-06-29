package org.cyclops.integratedtunnels.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.Level;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageSlotted;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.ingredient.storage.InconsistentIngredientInsertionException;
import org.cyclops.cyclopscore.ingredient.storage.IngredientStorageHelpers;
import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.INetworkCraftingHandlerRegistry;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;
import org.cyclops.integratedtunnels.part.aspect.ITunnelConnection;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * @author rubensworks
 */
public class TunnelHelpers {

    private static final Cache<ITunnelConnection, Boolean> CACHE_INV_CHECKS = CacheBuilder.newBuilder()
            .expireAfterWrite(GeneralConfig.inventoryUnchangedTickTimeout * (1000 / MinecraftHelpers.SECOND_IN_TICKS),
                    TimeUnit.MILLISECONDS).build();

    /**
     * Move instances from source to destination.
     * @param source The source instance storage.
     * @param sourceSlot The source slot.
     * @param destination The destination ingredient storage.
     * @param destinationSlot The destination slot.
     * @param ingredientPredicate Only instances matching this predicate will be moved.
     * @param movementPosition The position at which the movement is happening.
     *                         This is used for handling {@link InconsistentIngredientInsertionException}.
     * @param simulate If the transfer should be simulated.
     * @param <T> The instance type.
     * @param <M> The matching condition parameter.
     * @return The moved instance.
     * @throws EvaluationException If illegal movement occured and further movement should stop.
     */
    @Nonnull
    public static <T, M> T moveSingle(IIngredientComponentStorage<T, M> source, int sourceSlot,
                                      IIngredientComponentStorage<T, M> destination, int destinationSlot,
                                      IngredientPredicate<T, M> ingredientPredicate, PartPos movementPosition,
                                      boolean simulate) throws EvaluationException {
        try {
            try {
                if (ingredientPredicate.hasMatchFlags()) {
                    return IngredientStorageHelpers.moveIngredientsSlotted(source, sourceSlot, destination, destinationSlot,
                            ingredientPredicate.getInstance(), ingredientPredicate.getMatchFlags(), simulate);
                } else {
                    return IngredientStorageHelpers.moveIngredientsSlotted(source, sourceSlot, destination, destinationSlot,
                            ingredientPredicate, ingredientPredicate.getMaxQuantity(), ingredientPredicate.isExactQuantity(), simulate);
                }
            } catch (InconsistentIngredientInsertionException e) {
                // Handle movement errors due to inconsistent simulation.
                // If we are moving items, emit them in the world, otherwise they go lost.
                if (GeneralConfig.ejectItemsOnInconsistentSimulationMovement && e.getIngredientComponent().equals(IngredientComponent.ITEMSTACK)) {
                    ItemStackHelpers.spawnItemStack(movementPosition.getPos().getWorld(), movementPosition.getPos().getBlockPos(), e.getRemainder());
                    throw new EvaluationException("Ingredient movement failed " +
                            "due to inconsistent insertion behaviour by destination in simulation " +
                            "and non-simulation mode. This can be caused by invalid network setups. " +
                            "Ejected failed item in world.");
                }
                throw new EvaluationException("Ingredient movement failed " +
                        "due to inconsistent insertion behaviour by destination in simulation " +
                        "and non-simulation mode. This can be caused by invalid network setups. Lost " +
                        e.getIngredientComponent().getMatcher().localize(e.getRemainder()) + ".");
            }
        } catch (IllegalStateException e) {
            IntegratedTunnels.clog(Level.WARN, e.getMessage());
            return source.getComponent().getMatcher().getEmptyInstance();
        }
    }

    /**
     * Move ingredients from source to destination.
     * @param network The network in which the movement is happening.
     * @param ingredientsNetwork The ingredients network in which the movement is happening.
     * @param channel The channel.
     * @param connection The connection object.
     * @param source The source ingredient storage.
     * @param sourceSlot The source slot.
     * @param destination The destination ingredient storage.
     * @param destinationSlot The destination slot.
     * @param ingredientPredicate Only ingredientstack matching this predicate will be moved.
     * @param movementPosition The position at which the movement is happening.
     *                         This is used for handling {@link InconsistentIngredientInsertionException}.
     * @param craftIfFailed If the exact ingredient from ingredientPredicate should be crafted if transfer failed.
     * @param <T> The instance type.
     * @param <M> The matching condition parameter.
     * @return The moved ingredientstack.
     * @throws EvaluationException If illegal movement occured and further movement should stop.
     */
    @Nonnull
    public static <T, M> T moveSingleStateOptimized(INetwork network, IPositionedAddonsNetworkIngredients<T, M> ingredientsNetwork,
                                                    int channel, ITunnelConnection connection,
                                                    IIngredientComponentStorage<T, M> source, int sourceSlot,
                                                    IIngredientComponentStorage<T, M> destination, int destinationSlot,
                                                    IngredientPredicate<T, M> ingredientPredicate, PartPos movementPosition,
                                                    boolean craftIfFailed) throws EvaluationException {
        IIngredientMatcher<T, M> matcher = source.getComponent().getMatcher();

        // Don't craft if we still have a running crafting job for the instance.
        if (craftIfFailed && isCrafting(network, ingredientsNetwork, channel,
                ingredientPredicate.getInstance(), ingredientPredicate.getMatchFlags())) {
            return matcher.getEmptyInstance();
        }

        // Don't do any expensive transfers if the to-be-moved stack is empty
        if (ingredientPredicate.isEmpty()) {
            return matcher.getEmptyInstance();
        }

        // Don't do anything if we are sleeping for this connection
        if (CACHE_INV_CHECKS.getIfPresent(connection) != null) {
            return matcher.getEmptyInstance();
        }

        // Do the actual movement
        T moved = moveSingle(source, sourceSlot, destination, destinationSlot, ingredientPredicate, movementPosition, false);
        if (matcher.isEmpty(moved)) {
            // Mark this connection as 'sleeping' if nothing was moved
            CACHE_INV_CHECKS.put(connection, true);
        }

        // Schedule a new observation for the network, as its contents may have changed
        ingredientsNetwork.scheduleObservation();

        // Craft if we moved nothing, and the flag is enabled.
        if (craftIfFailed && matcher.isEmpty(moved)) {
            // If we don't have to move exact instances,
            // only request the crafting of 1.
            T craftInstance = ingredientPredicate.getInstance();
            if (!ingredientPredicate.isExactQuantity()) {
                craftInstance = matcher.withQuantity(craftInstance, 1);
            }

            // Don't allow crafting jobs to be started if we detect a case where movement failed,
            // but the required ingredient is in fact present in the network.
            // This is to avoid cases where crafting jobs would be started before a previous movement was observed,
            // and the crafting job output thereby not being detected upon the next observement.
            IIngredientPositionsIndex<T, M> index = ingredientsNetwork.getChannelIndex(channel);
            if (index.getQuantity(ingredientPredicate.getInstance()) >= matcher.getQuantity(craftInstance)) {
                return moved;
            }

            // Only craft if the target accepts the crafting output completely
            boolean targetAcceptsCraftingResult;
            if (destinationSlot >= 0) {
                targetAcceptsCraftingResult = destination instanceof IIngredientComponentStorageSlotted
                        && matcher.isEmpty(((IIngredientComponentStorageSlotted<T, M>) destination)
                        .insert(destinationSlot, craftInstance, true));
            } else {
                targetAcceptsCraftingResult = matcher.isEmpty(destination.insert(craftInstance, true));
            }

            if (targetAcceptsCraftingResult) {
                requestCrafting(network, ingredientsNetwork, channel,
                        craftInstance, ingredientPredicate.getMatchFlags());
            }
        }

        return moved;
    }

    /**
     * Start a crafting job for the given instance.
     * @param network The network to craft in.
     * @param ingredientsNetwork The ingredients network.
     * @param channel The channel.
     * @param instance The instance to craft.
     * @param matchCondition The match condition.
     * @param <T> The instance type.
     * @param <M> The matching condition parameter.
     * @return If a crafting job could be started.
     */
    public static <T, M> boolean requestCrafting(INetwork network, IPositionedAddonsNetworkIngredients<T, M> ingredientsNetwork,
                                                 int channel, T instance, M matchCondition) {
        return IntegratedDynamics._instance.getRegistryManager().getRegistry(INetworkCraftingHandlerRegistry.class)
                .craft(network, ingredientsNetwork, channel, ingredientsNetwork.getComponent(), instance, matchCondition, false);
    }

    /**
     * Check if the given instance is being crafted.
     * @param network The network to craft in.
     * @param ingredientsNetwork The ingredients network.
     * @param channel The channel.
     * @param instance The instance to craft.
     * @param matchCondition The match condition.
     * @param <T> The instance type.
     * @param <M> The matching condition parameter.
     * @return If a crafting job exists for the given instance.
     */
    public static <T, M> boolean isCrafting(INetwork network, IPositionedAddonsNetworkIngredients<T, M> ingredientsNetwork,
                                            int channel, T instance, M matchCondition) {
        return IntegratedDynamics._instance.getRegistryManager().getRegistry(INetworkCraftingHandlerRegistry.class)
                .isCrafting(network, ingredientsNetwork, channel, ingredientsNetwork.getComponent(), instance, matchCondition);
    }
}
