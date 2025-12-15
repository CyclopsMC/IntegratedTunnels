package org.cyclops.integratedtunnels.capability.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.integrateddynamics.api.network.AttachCapabilitiesEventNetwork;
import org.cyclops.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import org.cyclops.integratedtunnels.Capabilities;
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
        ResourceHandler<ItemResource> itemHandler = itemNetwork.getChannelExternal(net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK,
                IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL);
        event.register(Capabilities.ItemNetwork.NETWORK, new DefaultCapabilityProvider<>(itemNetwork));
        event.register(Capabilities.Item.NETWORK, new DefaultCapabilityProvider<>(itemHandler));
        event.addFullNetworkListener(itemNetwork);

        FluidNetwork fluidNetwork = new FluidNetwork(IngredientComponent.FLUIDSTACK);
        ResourceHandler<FluidResource> fluidChannel = fluidNetwork.getChannelExternal(net.neoforged.neoforge.capabilities.Capabilities.Fluid.BLOCK,
                IPositionedAddonsNetworkIngredients.DEFAULT_CHANNEL);
        event.register(Capabilities.FluidNetwork.NETWORK, new DefaultCapabilityProvider<>(fluidNetwork));
        event.register(Capabilities.Fluid.NETWORK, new DefaultCapabilityProvider<>(fluidChannel));
        event.addFullNetworkListener(fluidNetwork);
    }

}
