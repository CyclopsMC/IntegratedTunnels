package org.cyclops.integratedtunnels.core;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * A predicate for matching ItemStacks.
 * @author rubensworks
 */
public abstract class ItemStackPredicate implements Predicate<ItemStack> {

    private final ItemStack itemStack;
    private final int matchFlags;
    private final boolean blacklist;
    private final EmptyBehaviour emptyBehaviour;
    private final boolean empty;
    private final int maxQuantity;
    private final boolean exactQuantity;

    public ItemStackPredicate(@Nonnull ItemStack itemStack, int matchFlags, boolean blacklist, boolean empty,
                              int maxQuantity, boolean exactQuantity, EmptyBehaviour emptyBehaviour) {
        this.itemStack = itemStack;
        this.matchFlags = matchFlags;
        this.blacklist = blacklist;
        this.empty = empty;
        this.maxQuantity = maxQuantity;
        this.exactQuantity = exactQuantity;
        this.emptyBehaviour = emptyBehaviour;
    }

    public ItemStackPredicate(boolean blacklist, boolean empty, int maxQuantity, boolean exactQuantity) {
        this(ItemStack.EMPTY, -1, blacklist, empty, maxQuantity, exactQuantity, EmptyBehaviour.NONE);
    }

    @Nonnull
    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getMatchFlags() {
        return matchFlags;
    }

    public boolean hasMatchFlags() {
        return getMatchFlags() >= 0 && (emptyBehaviour == EmptyBehaviour.ANY || !getItemStack().isEmpty());
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
