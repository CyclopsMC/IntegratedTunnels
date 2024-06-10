package org.cyclops.integratedtunnels.part.aspect.operator;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.cyclopscore.persist.nbt.NBTClassType;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueType;
import org.cyclops.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import org.cyclops.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.core.evaluate.operator.PositionedOperator;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectReadBuilders;

import java.util.Optional;

/**
 * @author rubensworks
 */
public abstract class PositionedOperatorIngredientIndex<T, M> extends PositionedOperator {

    private int channel;

    public PositionedOperatorIngredientIndex(String name, PositionedOperatorIngredientIndex.Function<T, M> function,
                                             IValueType input, IValueType output,
                                             DimPos pos, Direction side, int channel) {
        super(name, name, name, new IValueType[]{input},
                output, function, IConfigRenderPattern.PREFIX_1, pos, side);
        this.channel = channel;
        ((PositionedOperatorIngredientIndex.Function) this.getFunction()).setOperator(this);
    }

    public void writeGeneratedFieldsToNBT(CompoundTag tag) {
        super.writeGeneratedFieldsToNBT(tag);
        NBTClassType.writeNbt(Integer.class, "channel", this.channel, tag);
    }

    public void readGeneratedFieldsFromNBT(CompoundTag tag) {
        super.readGeneratedFieldsFromNBT(tag);
        this.channel = NBTClassType.readNbt(Integer.class, "channel", tag);
    }

    @Override
    protected String getUnlocalizedType() {
        return "virtual";
    }

    @Override
    public IOperator materialize() {
        return this;
    }

    protected Optional<IIngredientPositionsIndex<T, M>> getChannelIndex() {
        return TunnelAspectReadBuilders.Network.getChannelIndex(getNetworkCapability(), getPos(), getSide(), channel);
    }

    protected abstract NetworkCapability<? extends IPositionedAddonsNetworkIngredients<T, M>> getNetworkCapability();

    public static abstract class Function<T, M> implements IFunction {

        private PositionedOperatorIngredientIndex<T, M> operator;

        public void setOperator(PositionedOperatorIngredientIndex<T, M> operator) {
            this.operator = operator;
        }

        public PositionedOperatorIngredientIndex<T, M> getOperator() {
            return operator;
        }
    }
}
