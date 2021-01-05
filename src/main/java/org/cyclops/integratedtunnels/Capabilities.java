package org.cyclops.integratedtunnels;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;

/**
 * Used capabilities for this mod.
 * @author rubensworks
 */
public class Capabilities {
    @CapabilityInject(IEnergyNetwork.class)
    public static Capability<IEnergyNetwork> NETWORK_ENERGY = null;
    @CapabilityInject(IFluidNetwork.class)
    public static Capability<IFluidNetwork> NETWORK_FLUID = null;
    @CapabilityInject(IInventoryState.class)
    public static Capability<IInventoryState> INVENTORY_STATE = null;
    @CapabilityInject(ISlotlessItemHandler.class)
    public static Capability<ISlotlessItemHandler> SLOTLESS_ITEMHANDLER = null;
}
