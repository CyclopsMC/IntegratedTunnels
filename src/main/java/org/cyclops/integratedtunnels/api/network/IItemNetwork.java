package org.cyclops.integratedtunnels.api.network;

import net.minecraftforge.items.IItemHandler;

import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.integrateddynamics.api.network.IChanneledNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetwork;

/**
 * A network capability that holds items.
 * @author rubensworks
 */
public interface IItemNetwork extends IPositionedAddonsNetwork, IChanneledNetwork<IItemNetwork.IItemChannel> {
    public static interface IItemChannel extends IItemHandler, ISlotlessItemHandler {}
}
