package org.cyclops.integratedtunnels.part;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

/**
 * A part state for handling item import and export.
 * It also acts as an item capability that can be added to itself.
 * @author rubensworks
 */
public class PartStateItem<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IItemNetwork> implements IItemHandler {

    public PartStateItem(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public int getSlots() {
        return getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().getSlots() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().getStackInSlot(slot) : null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return canReceive() && getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return canExtract() && getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().extractItem(slot, amount, simulate) : null;
    }

    @Override
    public int getSlotLimit(int slot) {
        return getPositionedAddonsNetwork().getSlotLimit(slot);
    }
}
