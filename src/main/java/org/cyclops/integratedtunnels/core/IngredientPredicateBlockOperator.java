package org.cyclops.integratedtunnels.core;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.MutableComponent;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.cyclopscore.helper.BlockHelpers;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeBlock;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.core.predicate.IngredientPredicate;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class IngredientPredicateBlockOperator extends IngredientPredicate<ItemStack, Integer> {
    private final IOperator predicate;
    private final PartTarget partTarget;

    public IngredientPredicateBlockOperator(int amount, boolean exactAmount, IOperator predicate, PartTarget partTarget) {
        super(IngredientComponent.ITEMSTACK, false, false, amount, exactAmount);
        this.predicate = predicate;
        this.partTarget = partTarget;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        ValueObjectTypeBlock.ValueBlock valueBlock = ValueObjectTypeBlock.ValueBlock.of(
                input.getItem() instanceof BlockItem ? BlockHelpers.getBlockStateFromItemStack(input) : null);
        try {
            IValue result = ValueHelpers.evaluateOperator(predicate, valueBlock);
            ValueHelpers.validatePredicateOutput(predicate, result);
            return ((ValueTypeBoolean.ValueBoolean) result).getRawValue();
        } catch (EvaluationException e) {
            PartHelpers.PartStateHolder<?, ?> partData = PartHelpers.getPart(partTarget.getCenter());
            if (partData != null) {
                IPartStateWriter partState = (IPartStateWriter) partData.getState();
                partState.addError(partState.getActiveAspect(), (MutableComponent) e.getErrorMessage());
                partState.setDeactivated(true);
            }
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IngredientPredicateBlockOperator)) {
            return false;
        }
        IngredientPredicateBlockOperator that = (IngredientPredicateBlockOperator) obj;
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
