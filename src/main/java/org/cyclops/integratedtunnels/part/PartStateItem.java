package org.cyclops.integratedtunnels.part;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.TunnelHelpers;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

import java.util.Optional;

/**
 * A part state for handling item import and export.
 * It also acts as an item capability that can be added to itself.
 * @author rubensworks
 */
public class PartStateItem<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IItemNetwork, ItemStack> implements ResourceHandler<ItemResource> {

    public PartStateItem(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public <T> Optional<T> getCapability(P partType, PartCapability<T> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
        if (capability == Capabilities.Item.PART) {
            return Optional.of((T) this);
        }
        return super.getCapability(partType, capability, network, partNetwork, target);
    }

    protected ResourceHandler<ItemResource> getItemHandler() {
        return getPositionedAddonsNetwork().getChannelExternal(net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, TunnelHelpers.getPassiveInteractionChannel(this));
    }

    @Override
    public int size() {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null ? getItemHandler().size() : 0;
    }

    @Override
    public ItemResource getResource(int slot) {
        if (getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            ResourceHandler<ItemResource> ih = getItemHandler();
            ItemResource resource = ih.getResource(slot);
            ItemStack itemStack = resource.toStack(ih.getAmountAsInt(slot));
            if (getStorageFilter().testView(itemStack)) {
                return resource;
            }
        }
        return ItemResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int slot) {
        if (getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            ResourceHandler<ItemResource> ih = getItemHandler();
            ItemResource resource = ih.getResource(slot);
            long amount = ih.getAmountAsLong(slot);
            ItemStack itemStack = resource.toStack((int) amount);
            if (getStorageFilter().testView(itemStack)) {
                return amount;
            }
        }
        return 0;
    }

    @Override
    public long getCapacityAsLong(int tank, ItemResource itemResource) {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null ? getItemHandler().getCapacityAsLong(tank, itemResource) : 0;
    }

    @Override
    public boolean isValid(int tank, ItemResource itemResource) {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testInsertion(itemResource.toStack(1)) && getItemHandler().isValid(tank, itemResource);
    }

    @Override
    public int insert(int tank, ItemResource resource, int amount, TransactionContext transaction) {
        return canReceive() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testInsertion(resource.toStack(amount)) ? getItemHandler().insert(tank, resource, amount, transaction) : 0;
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        return canReceive() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testInsertion(resource.toStack(amount)) ? getItemHandler().insert(resource, amount, transaction) : 0;
    }

    @Override
    public int extract(int tank, ItemResource resource, int amount, TransactionContext transaction) {
        return canExtract() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testExtraction(resource.toStack(amount)) ? getItemHandler().extract(tank, resource, amount, transaction) : 0;
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        return canExtract() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testExtraction(resource.toStack(amount)) ? getItemHandler().extract(resource, amount, transaction) : 0;
    }
}
