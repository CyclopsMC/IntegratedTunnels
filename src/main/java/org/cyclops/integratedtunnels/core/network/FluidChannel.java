package org.cyclops.integratedtunnels.core.network;

import java.util.List;

import javax.annotation.Nullable;

import org.cyclops.cyclopscore.helper.FluidHelpers;
import org.cyclops.integrateddynamics.api.network.IChanneledNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork.PrioritizedPartPos;
import org.cyclops.integratedtunnels.GeneralConfig;

import com.google.common.collect.Lists;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidChannel implements IFluidHandler {
	final FluidNetwork network;
	final int channel;

	FluidChannel(FluidNetwork network, int channel) {
		this.network = network;
		this.channel = channel;
	}

    @Override
    public IFluidTankProperties[] getTankProperties() {
        List<IFluidTankProperties> properties = Lists.newArrayList();
        for(PrioritizedPartPos partPos : network.getPositions(this.channel)) {
            IFluidHandler fluidHandler = network.getFluidHandler(partPos);
            if (fluidHandler != null) {
            	network.disablePosition(partPos.getPartPos());
                properties.addAll(Lists.newArrayList(fluidHandler.getTankProperties()));
                network.enablePosition(partPos.getPartPos());
            }
        }
        return properties.toArray(new IFluidTankProperties[properties.size()]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        int amount = FluidHelpers.getAmount(resource);
        amount = Math.min(amount, GeneralConfig.fluidRateLimit);
        int toFill = amount;
        for(PrioritizedPartPos partPos : network.getPositions(this.channel)) {
            IFluidHandler fluidHandler = network.getFluidHandler(partPos);
            if (fluidHandler != null) {
            	network.disablePosition(partPos.getPartPos());
                toFill -= fluidHandler.fill(resource, doFill);
                network.enablePosition(partPos.getPartPos());
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
        maxDrain = Math.min(maxDrain, GeneralConfig.fluidRateLimit);
        FluidStack fluid = null;
        for(PrioritizedPartPos partPos : network.getPositions(this.channel)) {
            IFluidHandler fluidHandler = network.getFluidHandler(partPos);
            if (fluidHandler != null) {
            	network.disablePosition(partPos.getPartPos());
                FluidStack drainedFluid = fluidHandler.drain(resource, doDrain);
                network.enablePosition(partPos.getPartPos());
                resource.amount -= FluidHelpers.getAmount(drainedFluid);
                if (drainedFluid != null) {
                    fluid = drainedFluid;
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
        maxDrain = Math.min(maxDrain, GeneralConfig.fluidRateLimit);
        int toDrain = maxDrain;
        FluidStack fluid = null;
        for(PrioritizedPartPos partPos : network.getPositions(this.channel)) {
            IFluidHandler fluidHandler = network.getFluidHandler(partPos);
            if (fluidHandler != null) {
            	network.disablePosition(partPos.getPartPos());
                FluidStack drainedFluid = fluidHandler.drain(toDrain, doDrain);
                network.enablePosition(partPos.getPartPos());
                toDrain -= FluidHelpers.getAmount(drainedFluid);
                if (drainedFluid != null) {
                    fluid = drainedFluid;
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
