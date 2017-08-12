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

    public ItemStackPredicate(@Nonnull ItemStack itemStack, int matchFlags) {
        this.itemStack = itemStack;
        this.matchFlags = matchFlags;
    }

    public ItemStackPredicate() {
        this(ItemStack.EMPTY, -1);
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
}
