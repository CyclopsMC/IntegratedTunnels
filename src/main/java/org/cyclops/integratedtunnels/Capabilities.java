package org.cyclops.integratedtunnels;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
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
        event.register(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, FluidHandler.PART);
        event.register(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK, EnergyStorage.PART);
    }
}
