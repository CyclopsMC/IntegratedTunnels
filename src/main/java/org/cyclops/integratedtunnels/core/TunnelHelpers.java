package org.cyclops.integratedtunnels.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.Level;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.ingredient.storage.IngredientStorageHelpers;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.IntegratedTunnels;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * @author rubensworks
 */
public class TunnelHelpers {

    private static final Cache<Integer, Boolean> CACHE_INV_CHECKS = CacheBuilder.newBuilder()
            .expireAfterWrite(GeneralConfig.inventoryUnchangedTickTimeout * (1000 / MinecraftHelpers.SECOND_IN_TICKS),
                    TimeUnit.MILLISECONDS).build();

    /**
     * Move instances from source to destination.
     * @param source The source instance storage.
     * @param sourceSlot The source slot.
     * @param destination The destination ingredient storage.
     * @param destinationSlot The destination slot.
     * @param ingredientPredicate Only instances matching this predicate will be moved.
     * @param simulate If the transfer should be simulated.
     * @param <T> The instance type.
     * @param <M> The matching condition parameter.
     * @return The moved instance.
     */
    @Nonnull
    public static <T, M> T moveSingle(IIngredientComponentStorage<T, M> source, int sourceSlot,
                                      IIngredientComponentStorage<T, M> destination, int destinationSlot,
                                      IngredientPredicate<T, M> ingredientPredicate, boolean simulate) {
        try {
            if (ingredientPredicate.hasMatchFlags()) {
                return IngredientStorageHelpers.moveIngredientsSlotted(source, sourceSlot, destination, destinationSlot,
                        ingredientPredicate.getInstance(), ingredientPredicate.getMatchFlags(), simulate);
            } else {
                return IngredientStorageHelpers.moveIngredientsSlotted(source, sourceSlot, destination, destinationSlot,
                        ingredientPredicate, Integer.MAX_VALUE, ingredientPredicate.isExactQuantity(), simulate);
            }
        } catch (IllegalStateException e) {
            IntegratedTunnels.clog(Level.WARN, e.getMessage());
            return source.getComponent().getMatcher().getEmptyInstance();
        }
    }

    /**
     * Move ingredients from source to destination.
     * @param connectionHash The connection hash.
     * @param source The source ingredient storage.
     * @param sourceSlot The source slot.
     * @param destination The destination ingredient storage.
     * @param destinationSlot The destination slot.
     * @param ingredientPredicate Only ingredientstack matching this predicate will be moved.
     * @param <T> The instance type.
     * @param <M> The matching condition parameter.
     * @return The moved ingredientstack.
     */
    @Nonnull
    public static <T, M> T moveSingleStateOptimized(int connectionHash,
                                                    IIngredientComponentStorage<T, M> source, int sourceSlot,
                                                    IIngredientComponentStorage<T, M> destination, int destinationSlot,
                                                    IngredientPredicate<T, M> ingredientPredicate) {
        IIngredientMatcher<T, M> matcher = source.getComponent().getMatcher();

        // Don't do any expensive transfers if the to-be-moved stack is empty
        if (ingredientPredicate.isEmpty()) {
            return matcher.getEmptyInstance();
        }

        // Don't do anything if we are sleeping for this connection
        if (CACHE_INV_CHECKS.getIfPresent(connectionHash) != null) {
            return matcher.getEmptyInstance();
        }

        //
        T moved = moveSingle(source, sourceSlot, destination, destinationSlot, ingredientPredicate, false);
        if (matcher.isEmpty(moved)) {
            // Mark this connection as 'sleeping' if nothing was moved
            CACHE_INV_CHECKS.put(connectionHash, true);
        }
        return moved;
    }
}
