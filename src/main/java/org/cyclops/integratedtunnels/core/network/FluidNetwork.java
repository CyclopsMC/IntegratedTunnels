package org.cyclops.integratedtunnels.core.network;

import com.google.common.collect.Lists;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.cyclops.cyclopscore.helper.FluidHelpers;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.network.PositionedAddonsNetwork;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A network that can hold fluids.
 * @author rubensworks
 */
public class FluidNetwork extends PositionedAddonsNetwork implements IFluidNetwork {

    protected static IFluidHandler getFluidHandler(PrioritizedPartPos pos) {
        return TileHelpers.getCapability(pos.getPartPos().getPos(), pos.getPartPos().getSide(), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
    }

    @Override
    public boolean addPosition(PartPos pos, int priority) {
        IFluidHandler fluidHandler = TileHelpers.getCapability(pos.getPos(), pos.getSide(), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        return fluidHandler != null && super.addPosition(pos, priority);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        List<IFluidTankProperties> properties = Lists.newArrayList();
        for(PrioritizedPartPos partPos : getPositions()) {
            IFluidHandler fluidHandler = getFluidHandler(partPos);
            if (fluidHandler != null) {
                properties.addAll(Lists.newArrayList(fluidHandler.getTankProperties()));
            }
        }
        return properties.toArray(new IFluidTankProperties[properties.size()]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        int amount = FluidHelpers.getAmount(resource);
        int toFill = amount;
        for(PrioritizedPartPos partPos : getPositions()) {
            IFluidHandler fluidHandler = getFluidHandler(partPos);
            if (fluidHandler != null) {
                toFill -= fluidHandler.fill(resource, doFill);
                if (toFill <= 0) {
                    break;
                }
            }
        }
        return amount - toFill;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        resource = resource.copy();
        int maxDrain = FluidHelpers.getAmount(resource);
        Fluid fluid = null;
        for(PrioritizedPartPos partPos : getPositions()) {
            IFluidHandler fluidHandler = getFluidHandler(partPos);
            if (fluidHandler != null) {
                FluidStack drainedFluid = fluidHandler.drain(resource, doDrain);
                resource.amount -= FluidHelpers.getAmount(drainedFluid);
                if (drainedFluid != null) {
                    fluid = drainedFluid.getFluid();
                }
                if (resource.amount <= 0) {
                    break;
                }
            }
        }
        int drained = maxDrain - resource.amount;
        return drained <= 0 ? null : new FluidStack(fluid, drained);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        int toDrain = maxDrain;
        Fluid fluid = null;
        for(PrioritizedPartPos partPos : getPositions()) {
            IFluidHandler fluidHandler = getFluidHandler(partPos);
            if (fluidHandler != null) {
                FluidStack drainedFluid = fluidHandler.drain(toDrain, doDrain);
                toDrain -= FluidHelpers.getAmount(drainedFluid);
                if (drainedFluid != null) {
                    fluid = drainedFluid.getFluid();
                }
                if (toDrain <= 0) {
                    break;
                }
            }
        }
        int drained = maxDrain - toDrain;
        return drained <= 0 ? null : new FluidStack(fluid, drained);
    }
}
