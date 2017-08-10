package org.cyclops.integratedtunnels;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
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

    @CapabilityInject(ITeslaConsumer.class)
    public static Capability<ITeslaConsumer> TESLA_CONSUMER = null;
    @CapabilityInject(ITeslaProducer.class)
    public static Capability<ITeslaProducer> TESLA_PRODUCER = null;
    @CapabilityInject(ITeslaHolder.class)
    public static Capability<ITeslaHolder> TESLA_HOLDER = null;
}
