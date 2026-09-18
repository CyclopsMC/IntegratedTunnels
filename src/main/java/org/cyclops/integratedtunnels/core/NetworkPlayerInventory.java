package org.cyclops.integratedtunnels.core;

import com.google.common.collect.Lists;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ItemMatch;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * A player inventory that can expose the items of an item network as additional slots.
 *
 * The network's items are never copied into this inventory up-front:
 * the network is only iterated when something actually reads these additional slots,
 * and items are only taken out of the network at the moment they are effectively taken out of this inventory.
 * This way, the network's contents may safely change while the player is being simulated,
 * as no items are ever handed out that were not actually removed from the network first.
 *
 * The items that are read from the network's slots are only views on the network.
 * Modifying such a view without taking it out of this inventory has no effect on the network.
 *
 * @author rubensworks
 */
public class NetworkPlayerInventory extends Inventory {

    private static final int EXTRACT_MATCH_FLAGS = ItemMatch.ITEM | ItemMatch.DATA;

    @Nullable
    private IIngredientComponentStorage<ItemStack, Integer> networkStorage = null;
    @Nullable
    private Iterator<ItemStack> networkIterator = null;
    private boolean networkIteratorDone = false;
    private final List<ItemStack> networkStacks = Lists.newArrayList();
    private final List<Integer> borrowedSlots = Lists.newArrayList();

    public NetworkPlayerInventory(Player player) {
        super(player);
    }

    /**
     * Expose the items of the given network storage as additional slots.
     * @param networkStorage An item network storage, or null to only expose the player's own slots.
     */
    public void setNetworkStorage(@Nullable IIngredientComponentStorage<ItemStack, Integer> networkStorage) {
        this.networkStorage = networkStorage;
        this.networkIterator = null;
        this.networkIteratorDone = false;
        this.networkStacks.clear();
        this.borrowedSlots.clear();
    }

    /**
     * Stop exposing the network, and collect the items that were taken out of the network,
     * but were not consumed during the simulation.
     * @return The items that must be inserted back into the network.
     */
    public List<ItemStack> returnNetworkStorage() {
        List<ItemStack> remaining = Lists.newArrayList();
        for (int slot : this.borrowedSlots) {
            ItemStack itemStack = this.items.get(slot);
            if (!itemStack.isEmpty()) {
                remaining.add(itemStack);
                this.items.set(slot, ItemStack.EMPTY);
            }
        }
        setNetworkStorage(null);
        return remaining;
    }

    /**
     * @return The number of slots of the player itself, without the network's slots.
     */
    public int getRealContainerSize() {
        return super.getContainerSize();
    }

    /**
     * Take the first network item matching the given predicate out of the network,
     * and place it in a free slot of the player.
     *
     * The item is really removed from the network, so it can safely be consumed by the caller.
     * Anything that is not consumed is inserted back into the network afterwards.
     *
     * @param predicate An item predicate.
     * @return The slot the item was placed in, or -1 if no matching item could be taken out of the network.
     */
    public int borrowFromNetwork(Predicate<ItemStack> predicate) {
        for (int i = 0; materializeNetworkStack(i); i++) {
            ItemStack itemStack = this.networkStacks.get(i);
            if (predicate.test(itemStack)) {
                int slot = getFreeBorrowSlot();
                if (slot < 0) {
                    return -1;
                }
                ItemStack extracted = extractFromNetwork(itemStack, itemStack.getMaxStackSize());
                if (extracted.isEmpty()) {
                    return -1;
                }
                this.items.set(slot, extracted);
                this.borrowedSlots.add(slot);
                return slot;
            }
        }
        return -1;
    }

    /**
     * @return The first empty slot that is not being used to hold an item to click with.
     */
    protected int getFreeBorrowSlot() {
        for (int slot = 0; slot < this.items.size(); slot++) {
            if (slot != this.selected && this.items.get(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    /**
     * Make sure that the network item at the given network index is known.
     * @param index A network index.
     * @return If an item is available at the given index.
     */
    protected boolean materializeNetworkStack(int index) {
        while (this.networkStacks.size() <= index) {
            ItemStack itemStack = nextNetworkStack();
            if (itemStack.isEmpty()) {
                return false;
            }
            this.networkStacks.add(itemStack);
        }
        return true;
    }

    protected ItemStack nextNetworkStack() {
        if (this.networkStorage == null || this.networkIteratorDone) {
            return ItemStack.EMPTY;
        }
        if (this.networkIterator == null) {
            this.networkIterator = this.networkStorage.iterator();
        }
        while (this.networkIterator.hasNext()) {
            ItemStack itemStack = this.networkIterator.next();
            if (!itemStack.isEmpty()) {
                return itemStack.copy();
            }
        }
        closeNetworkIterator();
        return ItemStack.EMPTY;
    }

    protected void closeNetworkIterator() {
        this.networkIterator = null;
        this.networkIteratorDone = true;
    }

    /**
     * Really take the given item out of the network.
     * @param prototype The known network item to take out, of which the known amount is decreased accordingly.
     * @param maxQuantity The maximum amount to take out.
     * @return The item that was taken out of the network, which may be less than requested
     *         if the network's contents changed in the meantime.
     */
    protected ItemStack extractFromNetwork(ItemStack prototype, int maxQuantity) {
        if (this.networkStorage == null || prototype.isEmpty() || maxQuantity <= 0) {
            return ItemStack.EMPTY;
        }

        // The network is being modified, so it is not safe to keep iterating over it.
        closeNetworkIterator();

        ItemStack extracted = this.networkStorage.extract(
                prototype.copyWithCount(Math.min(prototype.getCount(), maxQuantity)), EXTRACT_MATCH_FLAGS, false);
        prototype.shrink(extracted.getCount());
        return extracted;
    }

    protected boolean containsNetwork(Predicate<ItemStack> predicate) {
        for (int i = 0; materializeNetworkStack(i); i++) {
            if (predicate.test(this.networkStacks.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getContainerSize() {
        // One more slot is exposed as long as the network may still have more items,
        // so that iterating code keeps asking for the next item until the network is exhausted.
        boolean networkHasMore = this.networkStorage != null && !this.networkIteratorDone;
        return super.getContainerSize() + this.networkStacks.size() + (networkHasMore ? 1 : 0);
    }

    @Override
    public ItemStack getItem(int index) {
        int networkIndex = index - super.getContainerSize();
        if (networkIndex < 0) {
            return super.getItem(index);
        }
        return materializeNetworkStack(networkIndex) ? this.networkStacks.get(networkIndex) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        int networkIndex = index - super.getContainerSize();
        if (networkIndex < 0) {
            return super.removeItem(index, count);
        }
        if (!materializeNetworkStack(networkIndex)) {
            return ItemStack.EMPTY;
        }
        return extractFromNetwork(this.networkStacks.get(networkIndex), count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        int networkIndex = index - super.getContainerSize();
        if (networkIndex < 0) {
            return super.removeItemNoUpdate(index);
        }
        if (!materializeNetworkStack(networkIndex)) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = this.networkStacks.get(networkIndex);
        return extractFromNetwork(itemStack, itemStack.getCount());
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        int networkIndex = index - super.getContainerSize();
        if (networkIndex < 0) {
            super.setItem(index, stack);
            return;
        }
        if (materializeNetworkStack(networkIndex)) {
            ItemStack itemStack = this.networkStacks.get(networkIndex);
            // The items that were in this slot are consumed, so they are taken out of the network and dropped
            if (ItemStack.isSameItemSameComponents(itemStack, stack) && stack.getCount() < itemStack.getCount()) {
                // Only the difference is consumed
                extractFromNetwork(itemStack, itemStack.getCount() - stack.getCount());
                return;
            }
            extractFromNetwork(itemStack, itemStack.getCount());
        }
        // The new item is given to the player, so that it is inserted into the network afterwards
        if (!stack.isEmpty()) {
            int slot = getFreeBorrowSlot();
            if (slot >= 0) {
                this.items.set(slot, stack);
                this.borrowedSlots.add(slot);
            } else {
                this.player.drop(stack, false);
            }
        }
    }

    @Override
    public boolean contains(ItemStack stack) {
        return super.contains(stack) || containsNetwork(itemStack -> ItemStack.isSameItemSameComponents(itemStack, stack));
    }

    @Override
    public boolean contains(TagKey<Item> tag) {
        return super.contains(tag) || containsNetwork(itemStack -> itemStack.is(tag));
    }

    @Override
    public boolean contains(Predicate<ItemStack> predicate) {
        return super.contains(predicate) || containsNetwork(predicate);
    }

    @Override
    public int findSlotMatchingItem(ItemStack stack) {
        int slot = super.findSlotMatchingItem(stack);
        if (slot >= 0) {
            return slot;
        }
        return borrowFromNetwork(itemStack -> ItemStack.isSameItemSameComponents(stack, itemStack));
    }

}
