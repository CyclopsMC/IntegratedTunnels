package org.cyclops.integratedtunnels.core.network;

import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.integrateddynamics.api.network.IChanneledNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork.PrioritizedPartPos;
import org.cyclops.integratedtunnels.api.network.IItemNetwork.IItemChannel;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemChannel implements IItemChannel {
    final ItemNetwork network;
    final int channel;

    ItemChannel(ItemNetwork network, int channel) {
        this.network = network;
        this.channel = channel;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, boolean simulate) {
        for(PrioritizedPartPos partPos : network.getPositions(this.channel)) {
            ISlotlessItemHandler itemHandler = network.getSlotlessItemHandler(partPos);
            if (itemHandler != null) {
                network.disablePosition(partPos.getPartPos());
                stack = itemHandler.insertItem(stack, simulate);
                network.enablePosition(partPos.getPartPos());
                if (stack.isEmpty()) return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(int amount, boolean simulate) {
        for(PrioritizedPartPos partPos : network.getPositions(this.channel)) {
            ISlotlessItemHandler itemHandler = network.getSlotlessItemHandler(partPos);
            if (itemHandler != null) {
                network.disablePosition(partPos.getPartPos());
                ItemStack extracted = itemHandler.extractItem(amount, simulate);
                network.enablePosition(partPos.getPartPos());
                if (!extracted.isEmpty()) {
                    return extracted;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(ItemStack matchStack, int matchFlags, boolean simulate) {
        for(PrioritizedPartPos partPos : network.getPositions(this.channel)) {
            ISlotlessItemHandler itemHandler = network.getSlotlessItemHandler(partPos);
            if (itemHandler != null) {
                network.disablePosition(partPos.getPartPos());
                ItemStack extracted = itemHandler.extractItem(matchStack, matchFlags, simulate);
                network.enablePosition(partPos.getPartPos());
                if (!extracted.isEmpty()) {
                    return extracted;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlots() {
        int slots = 0;
        for(PrioritizedPartPos partPos : network.getPositions(this.channel)) {
            IItemHandler itemHandler = network.getItemHandler(partPos);
            if (itemHandler != null) {
                network.disablePosition(partPos.getPartPos());
                slots += itemHandler.getSlots();
                network.enablePosition(partPos.getPartPos());
            }
        }
        return slots;
    }

    protected Triple<IItemHandler, Integer, PrioritizedPartPos> getItemHandlerForSlot(int slot) {
        for(PrioritizedPartPos partPos : network.getPositions(this.channel)) {
            IItemHandler itemHandler = network.getItemHandler(partPos);
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
            network.disablePosition(slottedHandler.getRight().getPartPos());
            ItemStack ret = slottedHandler.getLeft().getStackInSlot(slottedHandler.getMiddle());
            network.enablePosition(slottedHandler.getRight().getPartPos());
            return ret;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        Triple<IItemHandler, Integer, PrioritizedPartPos> slottedHandler = getItemHandlerForSlot(slot);
        if (slottedHandler != null) {
            network.disablePosition(slottedHandler.getRight().getPartPos());
            ItemStack ret = slottedHandler.getLeft().insertItem(slottedHandler.getMiddle(), stack, simulate);
            network.enablePosition(slottedHandler.getRight().getPartPos());
            return ret;
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        Triple<IItemHandler, Integer, PrioritizedPartPos> slottedHandler = getItemHandlerForSlot(slot);
        if (slottedHandler != null) {
            network.disablePosition(slottedHandler.getRight().getPartPos());
            ItemStack ret = slottedHandler.getLeft().extractItem(slottedHandler.getMiddle(), amount, simulate);
            network.enablePosition(slottedHandler.getRight().getPartPos());
            return ret;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        Triple<IItemHandler, Integer, PrioritizedPartPos> slottedHandler = getItemHandlerForSlot(slot);
        if (slottedHandler != null) {
            network.disablePosition(slottedHandler.getRight().getPartPos());
            int ret = slottedHandler.getLeft().getSlotLimit(slottedHandler.getMiddle());
            network.enablePosition(slottedHandler.getRight().getPartPos());
            return ret;
        }
        return 0;
    }

}
