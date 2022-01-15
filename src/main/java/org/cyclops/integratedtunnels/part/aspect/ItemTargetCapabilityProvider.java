package org.cyclops.integratedtunnels.part.aspect;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.capability.network.ItemNetworkConfig;
import org.cyclops.integratedtunnels.core.part.PartStateRoundRobin;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class ItemTargetCapabilityProvider extends ChanneledTargetCapabilityProvider<IItemNetwork, ItemStack, Integer>
        implements IItemTarget {

    private final ITunnelConnection connection;
    private final int slot;
    private final IngredientPredicate<ItemStack, Integer> itemStackMatcher;
    private final PartTarget partTarget;
    private final IAspectProperties properties;

    public ItemTargetCapabilityProvider(ITunnelTransfer transfer, INetwork network, @Nullable ICapabilityProvider capabilityProvider,
                                        Direction side, int slot,
                                        IngredientPredicate<ItemStack, Integer> itemStackMatcher, PartTarget partTarget,
                                        IAspectProperties properties, @Nullable PartStateRoundRobin<?> partState) {
        super(network, capabilityProvider, side, network.getCapability(ItemNetworkConfig.CAPABILITY).orElse(null), partState,
                properties.getValue(TunnelAspectWriteBuilders.PROP_CHANNEL).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_CRAFT).getRawValue(),
                properties.getValue(TunnelAspectWriteBuilders.PROP_PASSIVE_IO).getRawValue());
        this.connection = new TunnelConnectionPositionedNetworkCapabilityProvider(network, getChannel(), partTarget.getTarget(), transfer, capabilityProvider);
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
    public ITunnelConnection getConnection() {
        return connection;
    }

    @Override
    protected IngredientComponent<ItemStack, Integer> getComponent() {
        return IngredientComponent.ITEMSTACK;
    }
}
