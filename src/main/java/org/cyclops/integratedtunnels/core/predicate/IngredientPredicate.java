package org.cyclops.integratedtunnels.core.predicate;

import com.google.common.collect.Lists;
import org.cyclops.commoncapabilities.api.ingredient.IIngredientMatcher;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integratedtunnels.part.aspect.ITunnelTransfer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

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

        ArrayList<T> instances1 = Lists.newArrayList(this.instances);
        ArrayList<T> instances2 = Lists.newArrayList(that.instances);
        if (instances1.size() != instances2.size()) {
            return false;
        }
        IIngredientMatcher<T, M> matcher = this.ingredientComponent.getMatcher();
        for (int i = 0; i < instances1.size(); i++) {
            if (!matcher.matchesExactly(instances1.get(i), instances2.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return ingredientComponent.hashCode()
                ^ StreamSupport.stream(instances.spliterator(), false)
                    .map(instance -> ingredientComponent.getMatcher().hash(instance))
                    .reduce(0, (a, b) -> a ^ b)
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
