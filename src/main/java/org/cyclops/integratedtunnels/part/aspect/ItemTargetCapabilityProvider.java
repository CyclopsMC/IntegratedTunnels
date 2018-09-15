package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.capability.network.ItemNetworkConfig;
import org.cyclops.integratedtunnels.core.IngredientPredicate;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class ItemTargetCapabilityProvider extends ChanneledTarget<IItemNetwork> implements IItemTarget {

    private final int connectionHash;
    private final ICapabilityProvider capabilityProvider;
    private final EnumFacing side;
    private final int slot;
    private final IngredientPredicate<ItemStack, Integer> itemStackMatcher;
    private final PartTarget partTarget;
    private final IAspectProperties properties;

    public ItemTargetCapabilityProvider(int transferHash, INetwork network, @Nullable ICapabilityProvider capabilityProvider,
                                        EnumFacing side, int slot,
                                        IngredientPredicate<ItemStack, Integer> itemStackMatcher, PartTarget partTarget,
                                        IAspectProperties properties, PartStateRoundRobin<?> partState) {
        super(network.getCapability(ItemNetworkConfig.CAPABILITY), partState,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue());
        int storagePosHash = partTarget.getTarget().hashCode();
        this.connectionHash = transferHash << 4 + storagePosHash ^ System.identityHashCode(getChanneledNetwork());
        this.capabilityProvider = capabilityProvider;
        this.side = side;
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
        return capabilityProvider != null;
    }

    @Override
    public IIngredientComponentStorage<ItemStack, Integer> getItemStorage() {
        return IngredientComponent.ITEMSTACK.getStorage(capabilityProvider, side);
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
