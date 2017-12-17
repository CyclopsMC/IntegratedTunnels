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
import org.cyclops.integratedtunnels.GeneralConfig;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A network that can hold fluids.
 * @author rubensworks
 */
public class FluidNetwork extends PositionedAddonsNetwork implements IFluidNetwork {

    protected IFluidHandler getFluidHandler(PrioritizedPartPos pos) {
        if (isPositionDisabled(pos.getPartPos())) {
            return null;
        }
        return TileHelpers.getCapability(pos.getPartPos().getPos(), pos.getPartPos().getSide(), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
    }

    @Override
    public boolean addPosition(PartPos pos, int priority, int channel) {
        IFluidHandler fluidHandler = TileHelpers.getCapability(pos.getPos(), pos.getSide(), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        return fluidHandler != null && super.addPosition(pos, priority, channel);
    }

	@Override
	public IFluidHandler getChannel(int channel) {
		return new FluidChannel(this, channel);
	}
}
