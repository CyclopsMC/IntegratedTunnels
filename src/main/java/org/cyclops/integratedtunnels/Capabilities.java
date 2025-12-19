package org.cyclops.integratedtunnels;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.commoncapabilities.api.ingredient.IngredientComponent;
import org.cyclops.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerEnergyHandler;
import org.cyclops.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerItemStack;
import org.cyclops.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerItemStackSlotless;
import org.cyclops.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerResourceHandler;
import org.cyclops.integrateddynamics.api.network.NetworkCapability;
import org.cyclops.integrateddynamics.api.part.PartCapability;
import org.cyclops.integrateddynamics.core.part.event.RegisterPartCapabilitiesEvent;
import org.cyclops.integratedtunnels.api.network.IFluidNetwork;
import org.cyclops.integratedtunnels.api.network.IItemNetwork;

/**
 * Used capabilities for this mod.
 * @author rubensworks
 */
public class Capabilities {
    public static final class ItemNetwork {
        public static final NetworkCapability<IItemNetwork> NETWORK = NetworkCapability.create(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "item_network"), IItemNetwork.class);
    }
    public static final class Item {
        public static final NetworkCapability<ResourceHandler<ItemResource>> NETWORK = NetworkCapability.create(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "item_handler"), ResourceHandler.asClass());
        public static final PartCapability<ResourceHandler<ItemResource>> PART = PartCapability.create(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "item_handler"), ResourceHandler.asClass());
    }
    public static final class SlotlessItemHandler {
        public static final PartCapability<ISlotlessItemHandler> PART = PartCapability.create(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "slotless_item_handler"), ISlotlessItemHandler.class);
    }
    public static final class FluidNetwork {
        public static final NetworkCapability<IFluidNetwork> NETWORK = NetworkCapability.create(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "fluid_network"), IFluidNetwork.class);
    }
    public static final class Fluid {
        public static final NetworkCapability<ResourceHandler<FluidResource>> NETWORK = NetworkCapability.create(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "fluid_handler"), ResourceHandler.asClass());
        public static final PartCapability<ResourceHandler<FluidResource>> PART = PartCapability.create(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "fluid_handler"), ResourceHandler.asClass());
    }
    public static final class Energy {
        public static final PartCapability<EnergyHandler> PART = PartCapability.create(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "energy_storage"), EnergyHandler.class);
    }

    public static void registerPartCapabilities(RegisterPartCapabilitiesEvent event) {
        event.register(net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK, Item.PART);
        event.register(org.cyclops.commoncapabilities.api.capability.Capabilities.SlotlessItemHandler.BLOCK, SlotlessItemHandler.PART);
        event.register(net.neoforged.neoforge.capabilities.Capabilities.Fluid.BLOCK, Fluid.PART);
        event.register(net.neoforged.neoforge.capabilities.Capabilities.Energy.BLOCK, Energy.PART);

        IngredientComponents.ENERGY.setStorageWrapperHandler(Energy.PART, new IngredientComponentStorageWrapperHandlerEnergyHandler<>(IngredientComponents.ENERGY, Energy.PART));
        IngredientComponents.ITEMSTACK.setStorageWrapperHandler(Item.PART, new IngredientComponentStorageWrapperHandlerItemStack<>(IngredientComponents.ITEMSTACK, Item.PART, SlotlessItemHandler.PART));
        IngredientComponents.ITEMSTACK.setStorageWrapperHandler(SlotlessItemHandler.PART, new IngredientComponentStorageWrapperHandlerItemStackSlotless<>(IngredientComponents.ITEMSTACK, SlotlessItemHandler.PART));
        IngredientComponents.FLUIDSTACK.setStorageWrapperHandler(Fluid.PART, new IngredientComponentStorageWrapperHandlerResourceHandler<>(IngredientComponents.FLUIDSTACK, Fluid.PART, IngredientComponent.FLUIDSTACK_CONVERTER));
    }
}
