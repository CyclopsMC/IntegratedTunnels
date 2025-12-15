package org.cyclops.integratedtunnels.part;

import com.google.common.collect.Iterators;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
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
public class PartTypeInterfaceItem extends PartTypeInterfacePositionedAddon<IItemNetwork, ResourceHandler<ItemResource>, PartTypeInterfaceItem, PartTypeInterfaceItem.State> {
    public PartTypeInterfaceItem(String name) {
        super(name);
    }

    @Override
    public NetworkCapability<IItemNetwork> getNetworkCapability() {
        return Capabilities.ItemNetwork.NETWORK;
    }

    @Override
    public PartCapability<ResourceHandler<ItemResource>> getPartCapability() {
        return Capabilities.Item.PART;
    }

    @Override
    public BlockCapability<ResourceHandler<ItemResource>, Direction> getBlockCapability() {
        return net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK;
    }

    @Override
    protected PartTypeInterfaceItem.State constructDefaultState() {
        return new PartTypeInterfaceItem.State();
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceItemBaseConsumption;
    }

    public static class State extends PartTypeInterfacePositionedAddon.State<IItemNetwork, ResourceHandler<ItemResource>, PartTypeInterfaceItem, PartTypeInterfaceItem.State> {

        @Override
        public PartCapability<ResourceHandler<ItemResource>> getTargetCapability() {
            return Capabilities.Item.PART;
        }

        @Override
        public ResourceHandler<ItemResource> getCapabilityInstance() {
            return new PartTypeInterfaceItem.ItemHandler(this);
        }

        @Override
        public <T> Optional<T> getCapability(PartTypeInterfaceItem partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
            if (isNetworkAndPositionValid() && capability == Capabilities.Item.PART) {
                return Optional.of((T) this.getCapabilityInstance());
            }
            return super.getCapability(partType, capability, network, partNetwork, target);
        }
    }

    public static class ItemHandler implements ResourceHandler<ItemResource>, ISlotlessItemHandler {
        private final IPartTypeInterfacePositionedAddon.IState<IItemNetwork, ResourceHandler<ItemResource>, ?, ?> state;

        public ItemHandler(IPartTypeInterfacePositionedAddon.IState<IItemNetwork, ResourceHandler<ItemResource>, ?, ?> state) {
            this.state = state;
        }

        protected ResourceHandler<ItemResource> getItemHandler() {
            return state.getPositionedAddonsNetwork().getChannelExternal(net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, state.getChannel());
        }

        @Override
        public int size() {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getItemHandler().size();
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemResource getResource(int slot) {
            if (!state.isNetworkAndPositionValid()) {
                return ItemResource.EMPTY;
            }
            state.disablePosition();
            ItemResource ret = getItemHandler().getResource(slot);
            state.enablePosition();
            return ret;
        }

        @Override
        public long getAmountAsLong(int slot) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            long ret = getItemHandler().getAmountAsLong(slot);
            state.enablePosition();
            return ret;
        }

        @Override
        public long getCapacityAsLong(int slot, ItemResource resource) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            long ret = getItemHandler().getCapacityAsLong(slot, resource);
            state.enablePosition();
            return ret;
        }

        @Override
        public boolean isValid(int slot, ItemResource resource) {
            if (!state.isNetworkAndPositionValid()) {
                return false;
            }
            state.disablePosition();
            boolean ret = getItemHandler().isValid(slot, resource);
            state.enablePosition();
            return ret;
        }

        @Override
        public int insert(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getItemHandler().insert(slot, resource, amount, transaction);
            state.enablePosition();
            return ret;
        }

        @Override
        public int insert(ItemResource resource, int amount, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getItemHandler().insert(resource, amount, transaction);
            state.enablePosition();
            return ret;
        }

        @Override
        public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getItemHandler().extract(slot, resource, amount, transaction);
            state.enablePosition();
            return ret;
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getItemHandler().extract(resource, amount, transaction);
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
        public ItemStack insertItem(ItemStack stack, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return stack;
            }
            state.disablePosition();
            ItemStack ret = state.getPositionedAddonsNetwork().getChannel(state.getChannelInterface()).insert(stack, transaction);
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(int amount, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return ItemStack.EMPTY;
            }
            state.disablePosition();
            ItemStack ret = state.getPositionedAddonsNetwork().getChannel(state.getChannelInterface()).extract(amount, transaction);
            state.enablePosition();
            return ret;
        }

        @Override
        public ItemStack extractItem(ItemStack matchStack, int matchFlags, TransactionContext transaction) {
            if (!state.isNetworkAndPositionValid()) {
                return ItemStack.EMPTY;
            }
            state.disablePosition();
            ItemStack ret = state.getPositionedAddonsNetwork().getChannel(state.getChannelInterface()).extract(matchStack, matchFlags, transaction);
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
            ResourceHandler<ItemResource> itemHandler = getItemHandler();
            for (int i = 0; i < itemHandler.size(); i++) {
                limit += itemHandler.getCapacityAsInt(i, ItemResource.EMPTY);
            }
            state.enablePosition();
            return limit;
        }
    }
}
