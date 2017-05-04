package org.cyclops.integratedtunnels.core.network;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
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

    protected IItemHandler getItemHandler(PrioritizedPartPos pos) {
        if (isPositionDisabled(pos.getPartPos())) {
            return null;
        }
        IItemHandler itemHandler = CACHE_ITEMHANDLER.getIfPresent(pos.getPartPos());
        if (itemHandler == null) {
            itemHandler = TileHelpers.getCapability(pos.getPartPos().getPos(), pos.getPartPos().getSide(), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            CACHE_ITEMHANDLER.put(pos.getPartPos(), itemHandler);
        }
        return itemHandler;
    }

    protected ISlotlessItemHandler getSlotlessItemHandler(PrioritizedPartPos pos) {
        if (isPositionDisabled(pos.getPartPos())) {
            return null;
        }
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

    protected IInventoryState getInventoryState(PrioritizedPartPos pos) {
        if (isPositionDisabled(pos.getPartPos())) {
            return null;
        }
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
                disablePosition(partPos.getPartPos());
                slots += itemHandler.getSlots();
                enablePosition(partPos.getPartPos());
            }
        }
        return slots;
    }

    protected Triple<IItemHandler, Integer, PrioritizedPartPos> getItemHandlerForSlot(int slot) {
        for(PrioritizedPartPos partPos : getPositions()) {
            IItemHandler itemHandler = getItemHandler(partPos);
            if (itemHandler != null) {
                int slots = itemHandler.getSlots();
                if (slot < slots) {
                    return Triple.of(itemHandler, slot, partPos);
                }
                slot -= slots;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        Triple<IItemHandler, Integer, PrioritizedPartPos> slottedHandler = getItemHandlerForSlot(slot);
        if (slottedHandler != null) {
            disablePosition(slottedHandler.getRight().getPartPos());
            ItemStack ret = slottedHandler.getLeft().getStackInSlot(slottedHandler.getMiddle());
            enablePosition(slottedHandler.getRight().getPartPos());
            return ret;
        }
        return null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        Triple<IItemHandler, Integer, PrioritizedPartPos> slottedHandler = getItemHandlerForSlot(slot);
        if (slottedHandler != null) {
            disablePosition(slottedHandler.getRight().getPartPos());
            ItemStack ret = slottedHandler.getLeft().insertItem(slottedHandler.getMiddle(), stack, simulate);
            enablePosition(slottedHandler.getRight().getPartPos());
            return ret;
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        Triple<IItemHandler, Integer, PrioritizedPartPos> slottedHandler = getItemHandlerForSlot(slot);
        if (slottedHandler != null) {
            disablePosition(slottedHandler.getRight().getPartPos());
            ItemStack ret = slottedHandler.getLeft().extractItem(slottedHandler.getMiddle(), amount, simulate);
            enablePosition(slottedHandler.getRight().getPartPos());
            return ret;
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
                disablePosition(partPos.getPartPos());
                stack = itemHandler.insertItem(stack, simulate);
                enablePosition(partPos.getPartPos());
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
                disablePosition(partPos.getPartPos());
                ItemStack extracted = itemHandler.extractItem(amount, simulate);
                enablePosition(partPos.getPartPos());
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
                disablePosition(partPos.getPartPos());
                ItemStack extracted = itemHandler.extractItem(matchStack, matchFlags, simulate);
                enablePosition(partPos.getPartPos());
                if (extracted != null) {
                    return extracted;
                }
            }
        }
        return null;
    }
}
