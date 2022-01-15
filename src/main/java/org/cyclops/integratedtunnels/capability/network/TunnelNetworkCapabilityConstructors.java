package org.cyclops.integratedtunnels.capability.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.integrateddynamics.Reference;
import org.cyclops.integrateddynamics.api.network.AttachCapabilitiesEventNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integratedtunnels.core.network.FluidNetwork;
import org.cyclops.integratedtunnels.core.network.ItemNetwork;

/**
 * Constructor event for network capabilities.
 * @author rubensworks
 */
public class TunnelNetworkCapabilityConstructors {

    @SubscribeEvent
    public void onNetworkLoad(AttachCapabilitiesEventNetwork event) {
        ItemNetwork itemNetwork = new ItemNetwork(IngredientComponent.ITEMSTACK);
        IItemHandler itemHandler = itemNetwork.getChannelExternal(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL);
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "item_network"),
                new DefaultCapabilityProvider<>(() -> ItemNetworkConfig.CAPABILITY, itemNetwork));
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "item_storage_network"),
                new DefaultCapabilityProvider<>(() -> CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, itemHandler));
        event.addFullNetworkListener(itemNetwork);

        FluidNetwork fluidNetwork = new FluidNetwork(IngredientComponent.FLUIDSTACK);
        IFluidHandler fluidChannel = fluidNetwork.getChannelExternal(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
                IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL);
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "fluid_network"),
                new DefaultCapabilityProvider<>(() -> FluidNetworkConfig.CAPABILITY, fluidNetwork));
        event.addCapability(new ResourceLocation(Reference.MOD_ID, "fluid_storage_network"),
                new DefaultCapabilityProvider<>(() -> CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, fluidChannel));
        event.addFullNetworkListener(fluidNetwork);
    }

}
