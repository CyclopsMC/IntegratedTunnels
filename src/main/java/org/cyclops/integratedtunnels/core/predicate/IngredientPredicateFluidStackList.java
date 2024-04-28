package org.cyclops.integratedtunnels.core.predicate;

import com.google.common.collect.Iterables;
import net.minecraftforge.fluids.FluidStack;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import org.cyclops.integratedtunnels.core.TunnelFluidHelpers;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class IngredientPredicateFluidStackList extends IngredientPredicate<FluidStack, Integer> {
    private final boolean blacklist;
    private final IValueTypeListProxy<ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack> fluidStacks;
    private final boolean checkFluid;
    private final boolean checkAmount;
    private final boolean checkNbt;

    public IngredientPredicateFluidStackList(boolean blacklist, int amount, boolean exactAmount, IValueTypeListProxy<ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack> fluidStacks, int matchFlags, boolean checkFluid, boolean checkAmount, boolean checkNbt) {
        super(IngredientComponent.FLUIDSTACK, Iterables.transform(fluidStacks, ValueObjectTypeFluidStack.ValueFluidStack::getRawValue), matchFlags, blacklist, false, amount, exactAmount);
        this.blacklist = blacklist;
        this.fluidStacks = fluidStacks;
        this.checkFluid = checkFluid;
        this.checkAmount = checkAmount;
        this.checkNbt = checkNbt;
    }

    @Override
    public boolean test(@Nullable FluidStack input) {
        for (ValueObjectTypeFluidStack.ValueFluidStack fluidStack : fluidStacks) {
            if (!fluidStack.getRawValue().isEmpty()
                    && TunnelFluidHelpers.areFluidStackEqual(input, fluidStack.getRawValue(), checkFluid, false, checkNbt)) { // TODO: hardcoded 'false' may have to be removed when restoring exact amount
                return !blacklist;
            }
        }
        return blacklist;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IngredientPredicateFluidStackList)) {
            return false;
        }
        IngredientPredicateFluidStackList that = (IngredientPredicateFluidStackList) obj;
        return super.equals(obj)
                && this.blacklist == that.blacklist
                && this.checkFluid == that.checkFluid
                && this.checkAmount == that.checkAmount
                && this.checkNbt == that.checkNbt
                && this.fluidStacks.equals(that.fluidStacks);
    }

    @Override
    public int hashCode() {
        return super.hashCode()
                ^ (this.blacklist ? 1 : 0) << 1
                ^ (this.checkFluid ? 1 : 0) << 2
                ^ (this.checkAmount ? 1 : 0) << 3
                ^ (this.checkNbt ? 1 : 0) << 4
                ^ this.fluidStacks.hashCode();
    }
}
