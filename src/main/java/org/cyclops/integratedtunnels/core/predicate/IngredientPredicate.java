package org.cyclops.integratedtunnels.core.predicate;

import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integratedtunnels.part.aspect.ITunnelTransfer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A predicate for matching ingredient components.
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public abstract class IngredientPredicate<T, M> implements Predicate<T>, ITunnelTransfer {

    private final IngredientComponent<T, M> ingredientComponent;
    private final Iterable<T> instances;
    private final M matchFlags;
    private final boolean blacklist;
    private final boolean empty;
    private final int maxQuantity;
    private final boolean exactQuantity;

    /**
     * Lazily computed value of {@link #hashCode()}.
     * Predicates are immutable, but are constructed anew on every aspect evaluation,
     * while their hash is needed on every transfer for the transfer cache lookup,
     * so it pays off to only compute it once.
     * A value of 0 means 'not computed yet'.
     */
    private int hashCodeCached = 0;

    public IngredientPredicate(IngredientComponent<T, M> ingredientComponent,
                               Iterable<T> instances, M matchFlags, boolean blacklist, boolean empty,
                               int maxQuantity, boolean exactQuantity) {
        this.ingredientComponent = ingredientComponent;
        this.instances = instances;
        this.matchFlags = matchFlags;
        this.blacklist = blacklist;
        this.empty = empty;
        this.maxQuantity = maxQuantity;
        this.exactQuantity = exactQuantity;
    }

    public IngredientPredicate(IngredientComponent<T, M> ingredientComponent,
                               T instance, M matchFlags, boolean blacklist, boolean empty,
                               int maxQuantity, boolean exactQuantity) {
        this(ingredientComponent, Collections.singletonList(instance), matchFlags, blacklist, empty, maxQuantity, exactQuantity);
    }

    // Note: implementors of this method *should* override equals and hashcode.
    public IngredientPredicate(IngredientComponent<T, M> ingredientComponent,
                               boolean blacklist, boolean empty, int maxQuantity, boolean exactQuantity) {
        this(ingredientComponent, ingredientComponent.getMatcher().getEmptyInstance(), null,
                blacklist, empty, maxQuantity, exactQuantity);
    }

    public IngredientComponent<T, M> getIngredientComponent() {
        return ingredientComponent;
    }

    @Nonnull
    public Iterable<T> getInstances() {
        return instances;
    }

    public M getMatchFlags() {
        return matchFlags;
    }

    public boolean hasMatchFlags() {
        return matchFlags != null && !blacklist;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IngredientPredicate)) {
            return false;
        }

        IngredientPredicate that = (IngredientPredicate) obj;
        if (!(this.ingredientComponent == that.ingredientComponent
                && Objects.equals(this.matchFlags, that.matchFlags)
                && this.blacklist == that.blacklist
                && this.empty == that.empty
                && this.maxQuantity == that.maxQuantity
                && this.exactQuantity == that.exactQuantity)) {
            return false;
        }

        // Compare both instance iterables in lock-step, so that no intermediate collections are needed.
        IIngredientMatcher<T, M> matcher = this.ingredientComponent.getMatcher();
        Iterator<T> it1 = this.instances.iterator();
        Iterator<T> it2 = ((IngredientPredicate<T, M>) that).instances.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            if (!matcher.matchesExactly(it1.next(), it2.next())) {
                return false;
            }
        }

        return !it1.hasNext() && !it2.hasNext();
    }

    @Override
    public int hashCode() {
        int hash = this.hashCodeCached;
        if (hash == 0) {
            hash = computeHashCode();
            if (hash == 0) {
                // Reserve 0 for 'not computed yet'
                hash = 1;
            }
            this.hashCodeCached = hash;
        }
        return hash;
    }

    /**
     * Calculate the hash code of this predicate.
     *
     * The result of this method is cached by {@link #hashCode()},
     * so subclasses must override this method instead of {@link #hashCode()}.
     *
     * @return The hash code.
     */
    protected int computeHashCode() {
        IIngredientMatcher<T, M> matcher = ingredientComponent.getMatcher();
        int instancesHash = 0;
        for (T instance : instances) {
            instancesHash = instancesHash ^ matcher.hash(instance);
        }
        return ingredientComponent.hashCode()
                ^ instancesHash
                ^ Objects.hashCode(matchFlags)
                ^ (blacklist ? 1 : 0)
                ^ (empty ? 2 : 4)
                ^ maxQuantity
                ^ (exactQuantity ? 8 : 16);
    }

    public static enum EmptyBehaviour {
        ANY,
        NONE;

        public static EmptyBehaviour fromBoolean(boolean emptyIsAny) {
            return emptyIsAny ? ANY : NONE;
        }
    }
}
