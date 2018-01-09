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

    public ItemStackPredicate(@Nonnull ItemStack itemStack, int matchFlags, boolean blacklist) {
        this.itemStack = itemStack;
        this.matchFlags = matchFlags;
        this.blacklist = blacklist;
    }

    public ItemStackPredicate(boolean blacklist) {
        this(ItemStack.EMPTY, -1, blacklist);
    }

    @Nonnull
    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getMatchFlags() {
        return matchFlags;
    }

    public boolean hasMatchFlags() {
        return getMatchFlags() >= 0;
    }

    public boolean isBlacklist() {
        return blacklist;
    }
}
