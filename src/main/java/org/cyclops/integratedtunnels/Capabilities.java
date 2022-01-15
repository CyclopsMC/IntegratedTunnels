package org.cyclops.integratedtunnels;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.integrateddynamics.api.network.IEnergyNetwork;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;

/**
 * Used capabilities for this mod.
 * @author rubensworks
 */
public class Capabilities {
    public static Capability<IEnergyNetwork> NETWORK_ENERGY = CapabilityManager.get(new CapabilityToken<>(){});
    public static Capability<IFluidNetwork> NETWORK_FLUID = CapabilityManager.get(new CapabilityToken<>(){});
    public static Capability<IInventoryState> INVENTORY_STATE = CapabilityManager.get(new CapabilityToken<>(){});
    public static Capability<ISlotlessItemHandler> SLOTLESS_ITEMHANDLER = CapabilityManager.get(new CapabilityToken<>(){});
}
