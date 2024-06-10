package org.cyclops.integratedtunnels.part.aspect.listproxy;

import com.google.common.collect.Iterators;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.ingredient.collection.IIngredientCollectionLike;
import org.cyclops.cyclopscore.persist.nbt.INBTProvider;
import org.cyclops.cyclopscore.persist.nbt.NBTClassType;
import org.cyclops.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeListProxyPositioned;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectReadBuilders;

import java.util.Iterator;
import java.util.Optional;

/**
 * A list proxy for the fluids in a network at a certain position.
 */
public class ValueTypeListProxyPositionedFluidNetwork extends ValueTypeListProxyPositioned<ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack> implements INBTProvider {

    private int channel;

    public ValueTypeListProxyPositionedFluidNetwork(DimPos pos, Direction side, int channel) {
        super(TunnelValueTypeListProxyFactories.POSITIONED_FLUID_NETWORK.getName(), ValueTypes.OBJECT_FLUIDSTACK, pos, side);
        this.channel = channel;
    }

    public ValueTypeListProxyPositionedFluidNetwork() {
        this(null, null, 0);
    }

    public void writeGeneratedFieldsToNBT(CompoundTag tag) {
        super.writeGeneratedFieldsToNBT(tag);
        NBTClassType.writeNbt(Integer.class, "channel", this.channel, tag);
    }

    public void readGeneratedFieldsFromNBT(CompoundTag tag) {
        super.readGeneratedFieldsFromNBT(tag);
        this.channel = NBTClassType.readNbt(Integer.class, "channel", tag);
    }

    protected Optional<IIngredientPositionsIndex<FluidStack, Integer>> getChannelIndex() {
        return TunnelAspectReadBuilders.Network.getChannelIndex(Capabilities.FluidNetwork.NETWORK, getPos(), getSide(), channel);
    }

    @Override
    public int getLength() {
        return getChannelIndex()
                .map(IIngredientCollectionLike::size)
                .orElse(0);
    }

    @Override
    public ValueObjectTypeFluidStack.ValueFluidStack get(int index) {
        return ValueObjectTypeFluidStack.ValueFluidStack.of(getChannelIndex()
                .map(store -> Iterators.get(store.iterator(), index, FluidStack.EMPTY))
                .orElse(FluidStack.EMPTY));
    }

    @Override
    public Iterator<ValueObjectTypeFluidStack.ValueFluidStack> iterator() {
        // We use a custom iterator that retrieves the network only once.
        // Because for large networks, the network would have to be retrieved for every single ingredient,
        // which could result in a major performance problem.
        return getChannelIndex()
                .map(store -> store.stream().map(ValueObjectTypeFluidStack.ValueFluidStack::of).iterator())
                .orElse(Iterators.forArray());
    }
}
