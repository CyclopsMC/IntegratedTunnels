package org.cyclops.integratedtunnels.part;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.core.part.PartStatePositionedAddon;

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

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().getTankProperties() : new IFluidTankProperties[0];
    }

    protected FluidStack rateLimitFluid(FluidStack fluidStack) {
        if (fluidStack != null && fluidStack.amount > GeneralConfig.fluidRateLimit) {
            return new FluidStack(fluidStack, GeneralConfig.fluidRateLimit);
        }
        return fluidStack;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return canReceive() && getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().fill(rateLimitFluid(resource), doFill) : 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return canExtract() && getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().drain(rateLimitFluid(resource), doDrain) : null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return canExtract() && getPositionedAddonsNetwork() != null ? getPositionedAddonsNetwork().drain(Math.min(maxDrain, GeneralConfig.fluidRateLimit), doDrain) : null;
    }
}
