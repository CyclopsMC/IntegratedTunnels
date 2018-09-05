package org.cyclops.integratedtunnels.core;

import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * A predicate for matching ItemStacks.
 * @author rubensworks
 */
public abstract class IngredientPredicate<T, M> implements Predicate<T> {

    private final IngredientComponent<T, M> ingredientComponent;
    private final T itemStack;
    private final M matchFlags;
    private final boolean blacklist;
    private final EmptyBehaviour emptyBehaviour;
    private final boolean empty;
    private final int maxQuantity;
    private final boolean exactQuantity;

    public IngredientPredicate(IngredientComponent<T, M> ingredientComponent,
                               @Nonnull T itemStack, M matchFlags, boolean blacklist, boolean empty,
                               int maxQuantity, boolean exactQuantity, EmptyBehaviour emptyBehaviour) {
        this.ingredientComponent = ingredientComponent;
        this.itemStack = itemStack;
        this.matchFlags = matchFlags;
        this.blacklist = blacklist;
        this.emptyBehaviour = emptyBehaviour;
        this.empty = empty;
        this.maxQuantity = maxQuantity;
        this.exactQuantity = exactQuantity;
    }

    public IngredientPredicate(IngredientComponent<T, M> ingredientComponent,
                               boolean blacklist, boolean empty, int maxQuantity, boolean exactQuantity) {
        this(ingredientComponent, ingredientComponent.getMatcher().getEmptyInstance(), null,
                blacklist, empty, maxQuantity, exactQuantity, IngredientPredicate.EmptyBehaviour.NONE);
    }

    public IngredientComponent<T, M> getIngredientComponent() {
        return ingredientComponent;
    }

    @Nonnull
    public T getItemStack() {
        return itemStack;
    }

    public M getMatchFlags() {
        return matchFlags;
    }

    public boolean hasMatchFlags() {
        return matchFlags != null && (emptyBehaviour == EmptyBehaviour.ANY || !getIngredientComponent().getMatcher().isEmpty(getItemStack()));
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public boolean isEmpty() {
        return empty;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public boolean isExactQuantity() {
        return exactQuantity;
    }

    public static enum EmptyBehaviour {
        ANY,
        NONE;

        public static EmptyBehaviour fromBoolean(boolean emptyIsAny) {
            return emptyIsAny ? ANY : NONE;
        }
    }
}
