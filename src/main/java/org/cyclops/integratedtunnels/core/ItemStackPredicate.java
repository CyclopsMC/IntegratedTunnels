package org.cyclops.integratedtunnels.core;

import com.google.common.base.Predicate;
import net.minecraft.item.ItemStack;

/**
 * A predicate for matching ItemStacks.
 * @author rubensworks
 */
public abstract class ItemStackPredicate implements Predicate<ItemStack> {

    private final ItemStack itemStack;
    private final int matchFlags;

    public ItemStackPredicate(ItemStack itemStack, int matchFlags) {
        this.itemStack = itemStack;
        this.matchFlags = matchFlags;
    }

    public ItemStackPredicate() {
        this(null, -1);
    }

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
