package org.cyclops.integratedtunnels.core.network;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.commoncapabilities.api.capability.itemhandler.DefaultSlotlessItemHandlerWrapper;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.network.PositionedAddonsNetwork;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.TunnelItemHelpers;

import java.util.concurrent.TimeUnit;

/**
 * A network that can hold items.
 * @author rubensworks
 */
public class ItemNetwork extends PositionedAddonsNetwork implements IItemNetwork, IInventoryState {

    // IItemHandler's are only cached for a tick, because we can't assure that they will still be there next tick.
    // Caching is important because IItemHandler's can possibly be looked up many times per tick, when actively exporting for example.
    private static final Cache<PartPos, IItemHandler> CACHE_ITEMHANDLER = CacheBuilder.newBuilder()
            .weakValues().expireAfterAccess(1000 / MinecraftHelpers.SECOND_IN_TICKS, TimeUnit.MILLISECONDS).build();
    private static final Cache<PartPos, ISlotlessItemHandler> CACHE_SLOTLESSITEMHANDLER = CacheBuilder.newBuilder()
            .weakValues().expireAfterAccess(1000 / MinecraftHelpers.SECOND_IN_TICKS, TimeUnit.MILLISECONDS).build();

    protected static IItemHandler getItemHandler(PrioritizedPartPos pos) {
        IItemHandler itemHandler = CACHE_ITEMHANDLER.getIfPresent(pos.getPartPos());
        if (itemHandler == null) {
            itemHandler = TileHelpers.getCapability(pos.getPartPos().getPos(), pos.getPartPos().getSide(), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            CACHE_ITEMHANDLER.put(pos.getPartPos(), itemHandler);
        }
        return itemHandler;
    }

    protected static ISlotlessItemHandler getSlotlessItemHandler(PrioritizedPartPos pos) {
        ISlotlessItemHandler slotlessItemHandler = CACHE_SLOTLESSITEMHANDLER.getIfPresent(pos.getPartPos());
        if (slotlessItemHandler == null) {
            slotlessItemHandler = TileHelpers.getCapability(pos.getPartPos().getPos(), pos.getPartPos().getSide(), Capabilities.SLOTLESS_ITEMHANDLER);
            if (slotlessItemHandler == null) {
                IItemHandler itemHandler = getItemHandler(pos);
                if (itemHandler != null) {
                    slotlessItemHandler = new DefaultSlotlessItemHandlerWrapper(itemHandler);
                }
            }
            CACHE_SLOTLESSITEMHANDLER.put(pos.getPartPos(), slotlessItemHandler);
        }
        return slotlessItemHandler;
    }

    protected static IInventoryState getInventoryState(PrioritizedPartPos pos) {
        return TileHelpers.getCapability(pos.getPartPos().getPos(), pos.getPartPos().getSide(), Capabilities.INVENTORY_STATE);
    }

    @Override
    public boolean addPosition(PartPos pos, int priority) {
        IItemHandler itemHandler = TileHelpers.getCapability(pos.getPos(), pos.getSide(), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        return itemHandler != null && super.addPosition(pos, priority);
    }

    @Override
    public int getSlots() {
        int slots = 0;
        for(PrioritizedPartPos partPos : getPositions()) {
            IItemHandler itemHandler = getItemHandler(partPos);
            if (itemHandler != null) {
                slots += itemHandler.getSlots();
            }
        }
        return slots;
    }

    protected Pair<IItemHandler, Integer> getItemHandlerForSlot(int slot) {
        for(PrioritizedPartPos partPos : getPositions()) {
            IItemHandler itemHandler = getItemHandler(partPos);
            if (itemHandler != null) {
                int slots = itemHandler.getSlots();
                if (slot < slots) {
                    return Pair.of(itemHandler, slot);
                }
                slot -= slots;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        Pair<IItemHandler, Integer> slottedHandler = getItemHandlerForSlot(slot);
        if (slottedHandler != null) {
            return slottedHandler.getLeft().getStackInSlot(slottedHandler.getRight());
        }
        return null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        Pair<IItemHandler, Integer> slottedHandler = getItemHandlerForSlot(slot);
        if (slottedHandler != null) {
            return slottedHandler.getLeft().insertItem(slottedHandler.getRight(), stack, simulate);
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        Pair<IItemHandler, Integer> slottedHandler = getItemHandlerForSlot(slot);
        if (slottedHandler != null) {
            return slottedHandler.getLeft().extractItem(slottedHandler.getRight(), amount, simulate);
        }
        return null;
    }

    @Override
    public int getHash() {
        int hash = 0;
        int i = 0;
        for(PrioritizedPartPos partPos : getPositions()) {
            IItemHandler itemHandler = getItemHandler(partPos);
            hash += TunnelItemHelpers.calculateInventoryState(itemHandler, getInventoryState(partPos)) + i++;
        }
        return hash;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, boolean simulate) {
        for(PrioritizedPartPos partPos : getPositions()) {
            ISlotlessItemHandler itemHandler = getSlotlessItemHandler(partPos);
            if (itemHandler != null) {
                stack = itemHandler.insertItem(stack, simulate);
                if (stack == null) return null;
            }
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(int amount, boolean simulate) {
        for(PrioritizedPartPos partPos : getPositions()) {
            ISlotlessItemHandler itemHandler = getSlotlessItemHandler(partPos);
            if (itemHandler != null) {
                ItemStack extracted = itemHandler.extractItem(amount, simulate);
                if (extracted != null) {
                    return extracted;
                }
            }
        }
        return null;
    }

    @Override
    public ItemStack extractItem(ItemStack matchStack, int matchFlags, boolean simulate) {
        for(PrioritizedPartPos partPos : getPositions()) {
            ISlotlessItemHandler itemHandler = getSlotlessItemHandler(partPos);
            if (itemHandler != null) {
                ItemStack extracted = itemHandler.extractItem(matchStack, matchFlags, simulate);
                if (extracted != null) {
                    return extracted;
                }
            }
        }
        return null;
    }
}
