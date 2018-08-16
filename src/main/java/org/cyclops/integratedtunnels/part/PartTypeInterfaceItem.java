package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Iterators;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.capability.network.ItemNetworkConfig;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * Interface for item handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceItem extends PartTypeInterfacePositionedAddon<IItemNetwork, IItemHandler, PartTypeInterfaceItem, PartTypeInterfaceItem.State> {
    public PartTypeInterfaceItem(String name) {
        super(name);
    }

    @Override
    protected Capability<IItemNetwork> getNetworkCapability() {
        return ItemNetworkConfig.CAPABILITY;
    }

    @Override
    protected Capability<IItemHandler> getTargetCapability() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    protected PartTypeInterfaceItem.State constructDefaultState() {
        return new PartTypeInterfaceItem.State();
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<PartTypeInterfaceItem, IItemNetwork, IItemHandler> implements IItemHandler, ISlotlessItemHandler {

        @Override
        protected Capability<IItemHandler> getTargetCapability() {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        }

        protected IItemHandler getItemHandler() {
            return getPositionedAddonsNetwork().getChannelExternal(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getChannel());
        }

        @Override
        public int getSlots() {
            disablePosition();
            int ret = getPositionedAddonsNetwork() != null ? getItemHandler().getSlots() : 0;
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            disablePosition();
            ItemStack ret = getPositionedAddonsNetwork() != null ? getItemHandler().getStackInSlot(slot) : ItemStack.EMPTY;
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            disablePosition();
            ItemStack ret = getPositionedAddonsNetwork() != null ? getItemHandler().insertItem(slot, stack, simulate) : stack;
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            disablePosition();
            ItemStack ret = getPositionedAddonsNetwork() != null ? getItemHandler().extractItem(slot, amount, simulate) : ItemStack.EMPTY;
            enablePosition();
            return ret;
        }

        @Override
        public int getSlotLimit(int slot) {
            disablePosition();
            int ret = getPositionedAddonsNetwork() != null ? getItemHandler().getSlotLimit(slot) : 0;
            enablePosition();
            return ret;
        }

        @Override
        public Iterator<ItemStack> getItems() {
            disablePosition();
            Iterator<ItemStack> ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().getChannel(getChannelInterface()).iterator() : Iterators.forArray();
            enablePosition();
            return ret;
        }

        @Override
        public Iterator<ItemStack> findItems(@Nonnull ItemStack stack, int matchFlags) {
            disablePosition();
            Iterator<ItemStack> ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().getChannel(getChannelInterface()).iterator(stack, matchFlags) : Iterators.forArray();
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack insertItem(ItemStack stack, boolean simulate) {
            disablePosition();
            ItemStack ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().getChannel(getChannelInterface()).insert(stack, simulate) : stack;
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(int amount, boolean simulate) {
            disablePosition();
            ItemStack ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().getChannel(getChannelInterface()).extract(amount, simulate) : ItemStack.EMPTY;
            enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(ItemStack matchStack, int matchFlags, boolean simulate) {
            disablePosition();
            ItemStack ret = getPositionedAddonsNetwork() != null
                    ? getPositionedAddonsNetwork().getChannel(getChannelInterface()).extract(matchStack, matchFlags, simulate) : ItemStack.EMPTY;
            enablePosition();
            return ret;
        }

        @Override
        public boolean hasCapability(Capability<?> capability) {
            return (getPositionedAddonsNetwork() != null && capability == Capabilities.SLOTLESS_ITEMHANDLER)
                    || super.hasCapability(capability);
        }

        @Override
        public <T> T getCapability(Capability<T> capability) {
            if (getPositionedAddonsNetwork() != null && capability == Capabilities.SLOTLESS_ITEMHANDLER) {
                return (T) this;
            }
            return super.getCapability(capability);
        }
    }
}
