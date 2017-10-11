package org.cyclops.integratedtunnels.capability.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.cyclops.commoncapabilities.api.capability.inventorystate.IInventoryState;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.integrateddynamics.Reference;
import org.cyclops.integrateddynamics.api.network.AttachCapabilitiesEventNetwork;
import org.cyclops.integratedtunnels.Capabilities;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;
import org.cyclops.integratedtunnels.core.network.FluidNetwork;
import org.cyclops.integratedtunnels.core.network.ItemNetwork;

/**
 * Constructor event for network capabilities.
 * @author rubensworks
 */
public class TunnelNetworkCapabilityConstructors {

    @SubscribeEvent
    public void onNetworkLoad(AttachCapabilitiesEventNetwork event) {
        ItemNetwork itemNetwork = new ItemNetwork();
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "itemNetwork"),
                new DefaultCapabilityProvider<IItemNetwork>(() -> ItemNetworkConfig.CAPABILITY, itemNetwork));
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "itemStorageNetwork"),
                new DefaultCapabilityProvider<IItemHandler>(() -> CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemNetwork));
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "inventoryStateItemNetwork"),
                new DefaultCapabilityProvider<IInventoryState>(() -> Capabilities.INVENTORY_STATE, itemNetwork));
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "itemStorageSlotlessNetwork"),
                new DefaultCapabilityProvider<ISlotlessItemHandler>(() -> Capabilities.SLOTLESS_ITEMHANDLER, itemNetwork));

        FluidNetwork fluidNetwork = new FluidNetwork();
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "fluidNetwork"),
                new DefaultCapabilityProvider<IFluidNetwork>(() -> FluidNetworkConfig.CAPABILITY, fluidNetwork));
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "fluidStorageNetwork"),
                new DefaultCapabilityProvider<IFluidHandler>(() -> CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, fluidNetwork));
    }

}
