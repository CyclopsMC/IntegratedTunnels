package org.cyclops.integratedtunnels.core.predicate;

import net.minecraftforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class IngredientPredicateFluidStackOperator extends IngredientPredicate<FluidStack, Integer> {
    private final IOperator predicate;
    private final PartTarget partTarget;

    public IngredientPredicateFluidStackOperator(int amount, boolean exactAmount, IOperator predicate, PartTarget partTarget) {
        super(IngredientComponent.FLUIDSTACK, false, false, amount, exactAmount);
        this.predicate = predicate;
        this.partTarget = partTarget;
    }

    @Override
    public boolean test(@Nullable FluidStack input) {
        ValueObjectTypeFluidStack.ValueFluidStack valueFluidStack = ValueObjectTypeFluidStack.ValueFluidStack.of(input);
        try {
            IValue result = ValueHelpers.evaluateOperator(predicate, valueFluidStack);
            ValueHelpers.validatePredicateOutput(predicate, result);
            return ((ValueTypeBoolean.ValueBoolean) result).getRawValue();
        } catch (EvaluationException e) {
            PartHelpers.PartStateHolder<?, ?> partData = PartHelpers.getPart(partTarget.getCenter());
            if (partData != null) {
                IPartStateWriter partState = (IPartStateWriter) partData.getState();
                partState.addError(partState.getActiveAspect(), e.getErrorMessage());
                partState.setDeactivated(true);
            }
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IngredientPredicateFluidStackOperator)) {
            return false;
        }
        IngredientPredicateFluidStackOperator that = (IngredientPredicateFluidStackOperator) obj;
        return super.equals(obj)
                && this.predicate.equals(that.predicate)
                && this.partTarget.equals(that.partTarget);
    }

    @Override
    public int hashCode() {
        return super.hashCode()
                ^ this.predicate.hashCode()
                ^ this.partTarget.hashCode();
    }
}
