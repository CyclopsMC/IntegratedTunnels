package org.cyclops.integratedtunnels.api.network;

import net.minecraftforge.fluids.capability.IFluidHandler;

import org.cyclops.integrateddynamics.api.network.IChanneledNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;

/**
 * A network capability that holds fluids.
 * @author rubensworks
 */
public interface IFluidNetwork extends IPositionedAddonsNetwork, IChanneledNetwork<IFluidHandler> {

}
