package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.item.ItemStack;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.capability.network.ItemNetworkConfig;
import org.cyclops.integratedtunnels.core.IngredientPredicate;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

/**
 * @author rubensworks
 */
public class ItemTargetStorage extends ChanneledTarget<IItemNetwork> implements IItemTarget {

    private final int connectionHash;
    private final IIngredientComponentStorage<ItemStack, Integer> storage;
    private final int slot;
    private final IngredientPredicate<ItemStack, Integer> itemStackMatcher;
    private final PartTarget partTarget;
    private final IAspectProperties properties;

    public ItemTargetStorage(int transferHash, INetwork network,
                             IIngredientComponentStorage<ItemStack, Integer> storage, int slot,
                             IngredientPredicate<ItemStack, Integer> itemStackMatcher, PartTarget partTarget,
                             IAspectProperties properties, PartStateRoundRobin<?> partState) {
        super(network, network.getCapability(ItemNetworkConfig.CAPABILITY), partState,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT).getRawValue());
        int storagePosHash = partTarget.getTarget().hashCode();
        this.connectionHash = transferHash << 4 + storagePosHash ^ System.identityHashCode(getChanneledNetwork());
        this.storage = storage;
        this.slot = slot;
        this.itemStackMatcher = itemStackMatcher;
        this.partTarget = partTarget;
        this.properties = properties;
    }

    @Override
    public IIngredientComponentStorage<ItemStack, Integer> getItemChannel() {
        return getChanneledNetwork().getChannel(getChannel());
    }

    @Override
    public boolean hasValidTarget() {
        return storage != null;
    }

    @Override
    public IIngredientComponentStorage<ItemStack, Integer> getStorage() {
        return storage;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public IngredientPredicate<ItemStack, Integer> getItemStackMatcher() {
        return itemStackMatcher;
    }

    @Override
    public PartTarget getPartTarget() {
        return partTarget;
    }

    @Override
    public IAspectProperties getProperties() {
        return properties;
    }

    @Override
    public int getConnectionHash() {
        return connectionHash;
    }
}
