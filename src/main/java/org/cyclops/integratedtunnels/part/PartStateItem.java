package org.cyclops.integratedtunnels.part;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

import javax.annotation.Nonnull;

/**
 * A part state for handling item import and export.
 * It also acts as an item capability that can be added to itself.
 * @author rubensworks
 */
public class PartStateItem<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IItemNetwork> implements IItemHandler {

    public PartStateItem(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    protected IItemHandler getItemHandler() {
        return getPositionedAddonsNetwork().getChannelExternal(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getChannel());
    }

    @Override
    public int getSlots() {
        return getPositionedAddonsNetwork() != null ? getItemHandler().getSlots() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return getPositionedAddonsNetwork() != null ? getItemHandler().getStackInSlot(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return canReceive() && getPositionedAddonsNetwork() != null ? getItemHandler().insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return canExtract() && getPositionedAddonsNetwork() != null ? getItemHandler().extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return getPositionedAddonsNetwork() != null ? getItemHandler().getSlotLimit(slot) : 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return getPositionedAddonsNetwork() != null && getItemHandler().isItemValid(slot, stack);
    }
}
