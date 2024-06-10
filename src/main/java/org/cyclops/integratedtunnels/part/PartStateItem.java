package org.cyclops.integratedtunnels.part;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.PositionedAddonsNetworkIngredientsFilter;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A part state for handling item import and export.
 * It also acts as an item capability that can be added to itself.
 * @author rubensworks
 */
public class PartStateItem<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IItemNetwork, ItemStack> implements IItemHandler {

    public PartStateItem(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public <T> Optional<T> getCapability(P partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
        if (capability == Capabilities.ItemHandler.PART) {
            return Optional.of((T) this);
        }
        return super.getCapability(partType, capability, network, partNetwork, target);
    }

    protected IItemHandler getItemHandler() {
        return getPositionedAddonsNetwork().getChannelExternal(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, getChannel());
    }

    @Override
    public int getSlots() {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null ? getItemHandler().getSlots() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        ItemStack ret = getPositionedAddonsNetwork() != null && getStorageFilter() != null ? getItemHandler().getStackInSlot(slot) : ItemStack.EMPTY;
        if (!ret.isEmpty() && !getStorageFilter().testView(ret)) {
            return ItemStack.EMPTY;
        }
        return ret;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (canReceive() && getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            if (!getStorageFilter().testInsertion(stack)) {
                return stack;
            }
            return getItemHandler().insertItem(slot, stack, simulate);
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (canExtract() && getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            PositionedAddonsNetworkIngredientsFilter<ItemStack> filter = getStorageFilter();

            // If we do an effective extraction, first simulate to check if it matches the filter
            if (!simulate) {
                ItemStack extractedSimulated = getItemHandler().extractItem(slot, amount, true);
                if (!filter.testExtraction(extractedSimulated)) {
                    return ItemStack.EMPTY;
                }
            }

            ItemStack extracted = getItemHandler().extractItem(slot, amount, simulate);

            // If simulating, just check the output
            if (simulate && !filter.testExtraction(extracted)) {
                return ItemStack.EMPTY;
            }

            return extracted;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return getPositionedAddonsNetwork() != null ? getItemHandler().getSlotLimit(slot) : 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null
                && getItemHandler().isItemValid(slot, stack) && !getStorageFilter().testInsertion(stack);
    }
}
