package org.cyclops.integratedtunnels.core.network;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.network.PositionedAddonsNetwork;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;

import java.util.Set;

/**
 * A network that can hold items.
 * @author rubensworks
 */
public class ItemNetwork extends PositionedAddonsNetwork implements IItemNetwork {

    protected static IItemHandler getItemHandler(PrioritizedPartPos pos) {
        return TileHelpers.getCapability(pos.getPartPos().getPos(), pos.getPartPos().getSide(), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
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
}
