package org.cyclops.integratedtunnels.part;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.network.PositionedAddonsNetworkIngredientsFilter;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * A part state for handling fluid import and export.
 * It also acts as an fluid capability that can be added to itself.
 * @author rubensworks
 */
public class PartStateFluid<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IFluidNetwork, FluidStack> implements IFluidHandler {

    public PartStateFluid(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network, IPartNetwork partNetwork, PartTarget target) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(capability, network, partNetwork, target);
    }

    protected IFluidHandler getFluidHandler() {
        return getPositionedAddonsNetwork().getChannelExternal(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getChannel());
    }

    @Override
    public int getTanks() {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null ? getFluidHandler().getTanks() : 0;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if (getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            FluidStack fluidStack = getFluidHandler().getFluidInTank(tank);
            if (getStorageFilter().testView(fluidStack)) {
                return fluidStack;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null ? getFluidHandler().getTankCapacity(tank) : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testInsertion(stack) && getFluidHandler().isFluidValid(tank, stack);
    }

    protected FluidStack rateLimitFluid(FluidStack fluidStack) {
        if (fluidStack != null && fluidStack.getAmount() > GeneralConfig.fluidRateLimit) {
            return new FluidStack(fluidStack, GeneralConfig.fluidRateLimit);
        }
        return fluidStack;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return canReceive() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testInsertion(resource) ? getFluidHandler().fill(rateLimitFluid(resource), action) : 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return canExtract() && getPositionedAddonsNetwork() != null && getStorageFilter() != null && getStorageFilter().testExtraction(resource) ? getFluidHandler().drain(rateLimitFluid(resource), action) : FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (canExtract() && getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            PositionedAddonsNetworkIngredientsFilter<FluidStack> filter = getStorageFilter();

            // If we do an effective extraction, first simulate to check if it matches the filter
            if (action.execute()) {
                FluidStack drainedSimulated = getFluidHandler().drain(Math.min(maxDrain, GeneralConfig.fluidRateLimit), FluidAction.EXECUTE);
                if (!filter.testExtraction(drainedSimulated)) {
                    return FluidStack.EMPTY;
                }
            }

            FluidStack drained = getFluidHandler().drain(Math.min(maxDrain, GeneralConfig.fluidRateLimit), action);

            // If simulating, just check the output
            if (action.simulate() && !filter.testExtraction(drained)) {
                return FluidStack.EMPTY;
            }

            return drained;
        }
        return FluidStack.EMPTY;
    }
}
