package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Iterators;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandler;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.cyclopscore.ingredient.collection.FilteredIngredientCollectionIterator;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon;
import org.cyclops.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Optional;

/**
 * Interface for item handlers.
 * @author rubensworks
 */
public class PartTypeInterfaceItem extends PartTypeInterfacePositionedAddon<IItemNetwork, IItemHandler, PartTypeInterfaceItem, PartTypeInterfaceItem.State> {
    public PartTypeInterfaceItem(String name) {
        super(name);
    }

    @Override
    public NetworkCapability<IItemNetwork> getNetworkCapability() {
        return Capabilities.ItemNetwork.NETWORK;
    }

    @Override
    public PartCapability<IItemHandler> getPartCapability() {
        return Capabilities.ItemHandler.PART;
    }

    @Override
    public BlockCapability<IItemHandler, Direction> getBlockCapability() {
        return net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK;
    }

    @Override
    protected PartTypeInterfaceItem.State constructDefaultState() {
        return new PartTypeInterfaceItem.State();
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceItemBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<IItemNetwork, IItemHandler, PartTypeInterfaceItem, PartTypeInterfaceItem.State> {

        @Override
        public PartCapability<IItemHandler> getTargetCapability() {
            return Capabilities.ItemHandler.PART;
        }

        @Override
        public IItemHandler getCapabilityInstance() {
            return new PartTypeInterfaceItem.ItemHandler(this);
        }

        @Override
        public <T> Optional<T> getCapability(PartTypeInterfaceItem partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == Capabilities.ItemHandler.PART) {
                return Optional.of((T) this.getCapabilityInstance());
            }
            return super.getCapability(partType, capability, network, partNetwork, target);
        }
    }

    public static class ItemHandler implements IItemHandler, ISlotlessItemHandler {
        private final IPartTypeInterfacePositionedAddon.IState<IItemNetwork, IItemHandler, ?, ?> state;

        public ItemHandler(IPartTypeInterfacePositionedAddon.IState<IItemNetwork, IItemHandler, ?, ?> state) {
            this.state = state;
        }

        protected IItemHandler getItemHandler() {
            return state.getPositionedAddonsNetwork().getChannelExternal(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, state.getChannel());
        }

        @Override
        public int getSlots() {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getItemHandler().getSlots();
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (!state.isNetworkAndPositionValid()) {
                return ItemStack.EMPTY;
            }
            state.disablePosition();
            ItemStack ret = getItemHandler().getStackInSlot(slot);
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return stack;
            }
            state.disablePosition();
            ItemStack ret = getItemHandler().insertItem(slot, stack, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return ItemStack.EMPTY;
            }
            state.disablePosition();
            ItemStack ret = getItemHandler().extractItem(slot, amount, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getItemHandler().getSlotLimit(slot);
            state.enablePosition();
            return ret;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (!state.isNetworkAndPositionValid()) {
                return false;
            }
            state.disablePosition();
            boolean ret = getItemHandler().isItemValid(slot, stack);
            state.enablePosition();
            return ret;
        }

        @Override
        public Iterator<ItemStack> getItems() {
            if (!state.isNetworkAndPositionValid()) {
                return Iterators.forArray();
            }
            state.disablePosition();
            Iterator<ItemStack> ret;
            if (!state.getPositionedAddonsNetwork().getChannel(state.getChannelInterface()).iterator().hasNext()) {
                // If the target is empty, we can safely forward this call to our index.
                ret = state.getPositionedAddonsNetwork().getChannel(state.getChannelInterface()).iterator();
            } else {
                // If the target is not empty, iterate over all positions except for the target to determine items.
                // If we would not do this, this would result in duplication of index contents, see CyclopsMC/IntegratedTerminals#109
                IItemNetwork network = state.getPositionedAddonsNetwork();
                ret = Iterators.concat(network.getPositions(state.getChannel()).stream()
                        .filter(pos -> !network.isPositionDisabled(pos))
                        .map(network::getRawInstances)
                        .toArray(Iterator[]::new));
            }
            state.enablePosition();
            return ret;
        }

        @Override
        public Iterator<ItemStack> findItems(@Nonnull ItemStack stack, int matchFlags) {
            if (!state.isNetworkAndPositionValid()) {
                return Iterators.forArray();
            }
            state.disablePosition();
            Iterator<ItemStack> ret;
            if (!state.getPositionedAddonsNetwork().getChannel(state.getChannelInterface()).iterator(stack, matchFlags).hasNext()) {
                // If the target is empty, we can safely forward this call to our index.
                ret = state.getPositionedAddonsNetwork().getChannel(state.getChannelInterface()).iterator(stack, matchFlags);
            } else {
                // If the target is not empty, iterate over all positions except for the target to determine items.
                // If we would not do this, this would result in duplication of index contents, see CyclopsMC/IntegratedTerminals#109
                ret = new FilteredIngredientCollectionIterator<>(getItems(), IngredientComponents.ITEMSTACK.getMatcher(), stack, matchFlags);
            }
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack insertItem(ItemStack stack, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return stack;
            }
            state.disablePosition();
            ItemStack ret = state.getPositionedAddonsNetwork().getChannel(state.getChannelInterface()).insert(stack, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(int amount, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return ItemStack.EMPTY;
            }
            state.disablePosition();
            ItemStack ret = state.getPositionedAddonsNetwork().getChannel(state.getChannelInterface()).extract(amount, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(ItemStack matchStack, int matchFlags, boolean simulate) {
            if (!state.isNetworkAndPositionValid()) {
                return ItemStack.EMPTY;
            }
            state.disablePosition();
            ItemStack ret = state.getPositionedAddonsNetwork().getChannel(state.getChannelInterface()).extract(matchStack, matchFlags, simulate);
            state.enablePosition();
            return ret;
        }

        @Override
        public int getLimit() {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int limit = 0;
            IItemHandler itemHandler = getItemHandler();
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                limit += itemHandler.getSlotLimit(i);
            }
            state.enablePosition();
            return limit;
        }
    }
}
