package org.cyclops.integratedtunnels.core;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

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
    public static FluidStack moveFluids(IFluidHandler source, IFluidHandler target, int maxAmount, boolean doTransfer, Predicate<FluidStack> fluidStackMatcher) {
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
                int fillableAmount = target.fill(drainable, false);
                if (fillableAmount > 0) {
                    FluidStack drained = source.drain(fillableAmount, doTransfer);
                    if (drained != null) {
                        drained.amount = target.fill(drained, doTransfer);
                        return drained;
                    }
                }
            }
        }
        return null;
    }

}
