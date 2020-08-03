package org.cyclops.integratedtunnels.part;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A part state for handling fluid import and export.
 * It also acts as an fluid capability that can be added to itself.
 * @author rubensworks
 */
public class PartStateFluid<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IFluidNetwork> implements IFluidHandler {

    public PartStateFluid(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    protected IFluidHandler getFluidHandler() {
        return getPositionedAddonsNetwork().getChannelExternal(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getChannel());
    }

    @Override
    public int getTanks() {
        return getPositionedAddonsNetwork() != null ? getFluidHandler().getTanks() : 0;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return getPositionedAddonsNetwork() != null ? getFluidHandler().getFluidInTank(tank) : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return getPositionedAddonsNetwork() != null ? getFluidHandler().getTankCapacity(tank) : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return getPositionedAddonsNetwork() != null && getFluidHandler().isFluidValid(tank, stack);
    }

    protected FluidStack rateLimitFluid(FluidStack fluidStack) {
        if (fluidStack != null && fluidStack.getAmount() > GeneralConfig.fluidRateLimit) {
            return new FluidStack(fluidStack, GeneralConfig.fluidRateLimit);
        }
        return fluidStack;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return canReceive() && getPositionedAddonsNetwork() != null ? getFluidHandler().fill(rateLimitFluid(resource), action) : 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return canExtract() && getPositionedAddonsNetwork() != null ? getFluidHandler().drain(rateLimitFluid(resource), action) : null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return canExtract() && getPositionedAddonsNetwork() != null ? getFluidHandler().drain(Math.min(maxDrain, GeneralConfig.fluidRateLimit), action) : null;
    }
}
