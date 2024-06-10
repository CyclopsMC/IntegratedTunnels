package org.cyclops.integratedtunnels.part.aspect.operator;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integratedtunnels.Capabilities;

/**
 * @author rubensworks
 */
public class PositionedOperatorIngredientIndexFluid extends PositionedOperatorIngredientIndex<FluidStack, Integer> {

    public PositionedOperatorIngredientIndexFluid() {
        this(null, Direction.NORTH, -1);
    }

    public PositionedOperatorIngredientIndexFluid(DimPos pos, Direction side, int channel) {
        super("countbyfluid", new Function(), ValueTypes.OBJECT_FLUIDSTACK, ValueTypes.LONG, pos, side, channel);
    }

    @Override
    protected NetworkCapability<? extends IPositionedAddonsNetworkIngredients<FluidStack, Integer>> getNetworkCapability() {
        return Capabilities.FluidNetwork.NETWORK;
    }

    public static class Function extends PositionedOperatorIngredientIndex.Function<FluidStack, Integer> {
        @Override
        public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException {
            ValueObjectTypeFluidStack.ValueFluidStack fluidStack = variables.getValue(0, ValueTypes.OBJECT_FLUIDSTACK);
            return ValueTypeLong.ValueLong.of(getOperator().getChannelIndex()
                    .map(index -> index.getQuantity(fluidStack.getRawValue()))
                    .orElse(0L));
        }
    }
}
