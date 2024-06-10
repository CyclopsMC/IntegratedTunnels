package org.cyclops.integratedtunnels;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.cyclops.commoncapabilities.IngredientComponents;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerEnergyStorage;
import org.cyclops.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerFluidStack;
import org.cyclops.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerItemStack;
import org.cyclops.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerItemStackSlotless;
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
        public static final NetworkCapability<IItemNetwork> NETWORK = NetworkCapability.create(new ResourceLocation(Reference.MOD_ID, "item_network"), IItemNetwork.class);
    }
    public static final class ItemHandler {
        public static final NetworkCapability<IItemHandler> NETWORK = NetworkCapability.create(new ResourceLocation(Reference.MOD_ID, "item_handler"), IItemHandler.class);
        public static final PartCapability<IItemHandler> PART = PartCapability.create(new ResourceLocation(Reference.MOD_ID, "item_handler"), IItemHandler.class);
    }
    public static final class SlotlessItemHandler {
        public static final PartCapability<ISlotlessItemHandler> PART = PartCapability.create(new ResourceLocation(Reference.MOD_ID, "slotless_item_handler"), ISlotlessItemHandler.class);
    }
    public static final class FluidNetwork {
        public static final NetworkCapability<IFluidNetwork> NETWORK = NetworkCapability.create(new ResourceLocation(Reference.MOD_ID, "fluid_network"), IFluidNetwork.class);
    }
    public static final class FluidHandler {
        public static final NetworkCapability<IFluidHandler> NETWORK = NetworkCapability.create(new ResourceLocation(Reference.MOD_ID, "fluid_handler"), IFluidHandler.class);
        public static final PartCapability<IFluidHandler> PART = PartCapability.create(new ResourceLocation(Reference.MOD_ID, "fluid_handler"), IFluidHandler.class);
    }
    public static final class EnergyStorage {
        public static final PartCapability<IEnergyStorage> PART = PartCapability.create(new ResourceLocation(Reference.MOD_ID, "energy_storage"), IEnergyStorage.class);
    }

    public static void registerPartCapabilities(RegisterPartCapabilitiesEvent event) {
        event.register(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, ItemHandler.PART);
        event.register(org.cyclops.commoncapabilities.api.capability.Capabilities.SlotlessItemHandler.BLOCK, SlotlessItemHandler.PART);
        event.register(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, FluidHandler.PART);
        event.register(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK, EnergyStorage.PART);

        IngredientComponents.ENERGY.setStorageWrapperHandler(EnergyStorage.PART, new IngredientComponentStorageWrapperHandlerEnergyStorage<>(IngredientComponents.ENERGY, EnergyStorage.PART));
        IngredientComponents.ITEMSTACK.setStorageWrapperHandler(ItemHandler.PART, new IngredientComponentStorageWrapperHandlerItemStack<>(IngredientComponents.ITEMSTACK, ItemHandler.PART, SlotlessItemHandler.PART));
        IngredientComponents.ITEMSTACK.setStorageWrapperHandler(SlotlessItemHandler.PART, new IngredientComponentStorageWrapperHandlerItemStackSlotless<>(IngredientComponents.ITEMSTACK, SlotlessItemHandler.PART));
        IngredientComponents.FLUIDSTACK.setStorageWrapperHandler(FluidHandler.PART, new IngredientComponentStorageWrapperHandlerFluidStack<>(IngredientComponents.FLUIDSTACK, FluidHandler.PART));
    }
}
