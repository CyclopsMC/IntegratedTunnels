package org.cyclops.integratedtunnels.part.aspect.listproxy;

import com.google.common.collect.Iterators;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.ingredient.collection.IIngredientCollectionLike;
import org.cyclops.cyclopscore.persist.nbt.INBTProvider;
import org.cyclops.cyclopscore.persist.nbt.NBTClassType;
import org.cyclops.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositioned;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectReadBuilders;

import java.util.Iterator;
import java.util.Optional;

/**
 * A list proxy for the items in a network at a certain position.
 */
public class ValueTypeListProxyPositionedItemNetwork extends ValueTypeListProxyPositioned<ValueObjectTypeItemStack, ValueObjectTypeItemStack.ValueItemStack> implements INBTProvider {

    private int channel;

    public ValueTypeListProxyPositionedItemNetwork(DimPos pos, Direction side, int channel) {
        super(TunnelValueTypeListProxyFactories.POSITIONED_ITEM_NETWORK.getName(), ValueTypes.OBJECT_ITEMSTACK, pos, side);
        this.channel = channel;
    }

    public ValueTypeListProxyPositionedItemNetwork() {
        this(null, null, 0);
    }

    @Override
    public void writeGeneratedFieldsToNBT(CompoundTag tag, HolderLookup.Provider holderLookupProvider) {
        super.writeGeneratedFieldsToNBT(tag, holderLookupProvider);
        NBTClassType.writeNbt(Integer.class, "channel", this.channel, tag, holderLookupProvider);
    }

    @Override
    public void readGeneratedFieldsFromNBT(CompoundTag tag, HolderLookup.Provider holderLookupProvider) {
        super.readGeneratedFieldsFromNBT(tag, holderLookupProvider);
        this.channel = NBTClassType.readNbt(Integer.class, "channel", tag, holderLookupProvider);
    }

    protected Optional<IIngredientPositionsIndex<ItemStack, Integer>> getChannelIndex() {
        return TunnelAspectReadBuilders.Network.getChannelIndex(Capabilities.ItemNetwork.NETWORK, getPos(), getSide(), channel);
    }

    @Override
    public int getLength() {
        return getChannelIndex()
                .map(IIngredientCollectionLike::size)
                .orElse(0);
    }

    @Override
    public ValueObjectTypeItemStack.ValueItemStack get(int index) {
        return ValueObjectTypeItemStack.ValueItemStack.of(getChannelIndex()
                .map(store -> Iterators.get(store.iterator(), index, ItemStack.EMPTY))
                .orElse(ItemStack.EMPTY));
    }

    @Override
    public Iterator<ValueObjectTypeItemStack.ValueItemStack> iterator() {
        // We use a custom iterator that retrieves the network only once.
        // Because for large networks, the network would have to be retrieved for every single ingredient,
        // which could result in a major performance problem.
        return getChannelIndex()
                .map(store -> store.stream().map(ValueObjectTypeItemStack.ValueItemStack::of).iterator())
                .orElse(Iterators.forArray());
    }
}
