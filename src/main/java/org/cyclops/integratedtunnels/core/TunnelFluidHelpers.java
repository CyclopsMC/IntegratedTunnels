package org.cyclops.integratedtunnels.core;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.operator.IOperator;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueHelpers;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author rubensworks
 */
public class TunnelFluidHelpers {

    public static final Predicate<FluidStack> MATCH_ALL = new Predicate<FluidStack>() {
        @Override
        public boolean apply(@Nullable FluidStack input) {
            return true;
        }
    };

    /**
     * Move all fluids matching the predicate from source to target.
     * @param source The source fluid handler.
     * @param target The target fluid handler.
     * @param maxAmount The maximum fluid amount to transfer.
     * @param doTransfer If transfer should actually happen, will simulate otherwise.
     * @param fluidStackMatcher The fluidstack match predicate.
     * @return The moved fluidstack or null.
     */
    @Nullable
    public static FluidStack moveFluids(IFluidHandler source, final IFluidHandler target, int maxAmount, boolean doTransfer, Predicate<FluidStack> fluidStackMatcher) {
        return moveFluids(source, new Function<FluidStack, IFluidHandler>() {
            @Nullable
            @Override
            public IFluidHandler apply(@Nullable FluidStack input) {
                return target;
            }
        }, maxAmount, doTransfer, fluidStackMatcher);
    }

    /**
     * Move all fluids matching the predicate from source to target.
     * @param source The source fluid handler.
     * @param targetGetter The target fluid handler getter.
     * @param maxAmount The maximum fluid amount to transfer.
     * @param doTransfer If transfer should actually happen, will simulate otherwise.
     * @param fluidStackMatcher The fluidstack match predicate.
     * @return The moved fluidstack or null.
     */
    @Nullable
    public static FluidStack moveFluids(IFluidHandler source, Function<FluidStack, IFluidHandler> targetGetter, int maxAmount, boolean doTransfer, Predicate<FluidStack> fluidStackMatcher) {
        List<FluidStack> checkFluids = Lists.newArrayList();
        for (IFluidTankProperties properties : source.getTankProperties()) {
            FluidStack contents = properties.getContents();
            if (contents != null) {
                FluidStack toMove = contents.copy();
                toMove.amount = maxAmount;
                if (fluidStackMatcher.apply(toMove)) {
                    checkFluids.add(toMove);
                }
            }
        }

        for (FluidStack checkFluid : checkFluids) {
            FluidStack drainable = source.drain(checkFluid, false);
            if (drainable != null && drainable.amount > 0) {
                IFluidHandler target = targetGetter.apply(drainable);
                if (target != null) {
                    int fillableAmount = target.fill(drainable, false);
                    if (fillableAmount > 0) {
                        FluidStack drained = source.drain(new FluidStack(drainable.getFluid(), fillableAmount), doTransfer);
                        if (drained != null) {
                            drained.amount = target.fill(drained, doTransfer);
                            return drained;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Predicate<FluidStack> matchFluidStack(final FluidStack fluidStack, final boolean checkAmount, final boolean checkNbt) {
        return new Predicate<FluidStack>() {
            @Override
            public boolean apply(@Nullable FluidStack input) {
                return areFluidStackEqual(input, fluidStack, true, checkAmount, checkNbt);
            }
        };
    }

    public static Predicate<FluidStack> matchFluidStacks(final IValueTypeListProxy<ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack> fluidStacks,
                                                       final boolean checkAmount, final boolean checkNbt) {
        return new Predicate<FluidStack>() {
            @Override
            public boolean apply(@Nullable FluidStack input) {
                for (ValueObjectTypeFluidStack.ValueFluidStack fluidStack : fluidStacks) {
                    if (fluidStack.getRawValue().isPresent()
                            && areFluidStackEqual(input, fluidStack.getRawValue().get(), true, checkAmount, checkNbt)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static Predicate<FluidStack> matchPredicate(final PartTarget partTarget, final IOperator predicate) {
        return new Predicate<FluidStack>() {
            @Override
            public boolean apply(@Nullable FluidStack input) {
                ValueObjectTypeFluidStack.ValueFluidStack valueFluidStack = ValueObjectTypeFluidStack.ValueFluidStack.of(input);
                try {
                    IValue result = ValueHelpers.evaluateOperator(predicate, valueFluidStack);
                    return ((ValueTypeBoolean.ValueBoolean) result).getRawValue();
                } catch (EvaluationException e) {
                    PartHelpers.PartStateHolder<?, ?> partData = PartHelpers.getPart(partTarget.getCenter());
                    if (partData != null) {
                        IPartStateWriter partState = (IPartStateWriter) partData.getState();
                        partState.addError(partState.getActiveAspect(), new L10NHelpers.UnlocalizedString(e.getMessage()));
                        partState.setDeactivated(true);
                    }
                    return false;
                }
            }
        };
    }

    public static boolean areFluidStackEqual(FluidStack stackA, FluidStack stackB,
                                             boolean checkFluid, boolean checkAmount, boolean checkNbt) {
        if (stackA == null && stackB == null) return true;
        if (stackA != null && stackB != null) {
            if (checkAmount && stackA.amount != stackB.amount) return false;
            if (checkFluid && stackA.getFluid() != stackB.getFluid()) return false;
            if (checkNbt && !FluidStack.areFluidStackTagsEqual(stackA, stackB)) return false;
            return true;
        }
        return false;
    }

}
