package org.cyclops.integratedtunnels.part.aspect.operator;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integratedtunnels.capability.network.ItemNetworkConfig;

import org.cyclops.integrateddynamics.core.evaluate.operator.OperatorBase.SafeVariablesGetter;

/**
 * @author rubensworks
 */
public class PositionedOperatorIngredientIndexItem extends PositionedOperatorIngredientIndex<ItemStack, Integer> {

    public PositionedOperatorIngredientIndexItem() {
        this(null, Direction.NORTH, -1);
    }

    public PositionedOperatorIngredientIndexItem(DimPos pos, Direction side, int channel) {
        super("countbyitem", new Function(), ValueTypes.OBJECT_ITEMSTACK, ValueTypes.LONG, pos, side, channel);
    }

    @Override
    protected Capability<? extends IPositionedAddonsNetworkIngredients<ItemStack, Integer>> getNetworkCapability() {
        return ItemNetworkConfig.CAPABILITY;
    }

    public static class Function extends PositionedOperatorIngredientIndex.Function<ItemStack, Integer> {
        @Override
        public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException {
            ValueObjectTypeItemStack.ValueItemStack itemStack = variables.getValue(0, ValueTypes.OBJECT_ITEMSTACK);
            return ValueTypeLong.ValueLong.of(getOperator().getChannelIndex()
                    .map(index -> index.getQuantity(itemStack.getRawValue()))
                    .orElse(0L));
        }
    }
}
