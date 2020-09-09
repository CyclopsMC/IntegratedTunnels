package org.cyclops.integratedtunnels;

import net.minecraftforge.fml.config.ModConfig;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.extendedconfig.DummyConfig;
import org.cyclops.cyclopscore.tracking.Analytics;
import org.cyclops.cyclopscore.tracking.Versions;

/**
 * A config with general options for this mod.
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    @ConfigurableProperty(category = "core", comment = "If an anonymous mod startup analytics request may be sent to our analytics service.")
    public static boolean analytics = true;

    @ConfigurableProperty(category = "core", comment = "If the version checker should be enabled.")
    public static boolean versionChecker = true;

    @ConfigurableProperty(category = "core", comment = "How many ticks importers/exporters should sleep until checking targets again when they were previously unchanged.", configLocation = ModConfig.Type.SERVER)
    public static int inventoryUnchangedTickTimeout = 10;

    @ConfigurableProperty(category = "core", comment = "The maximum network fluid transfer rate.", isCommandable = true, minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int fluidRateLimit = Integer.MAX_VALUE;

    @ConfigurableProperty(category = "core", comment = "If particles should be shown and sounds should be played when tunnels are interacting with the world.", isCommandable = true)
    public static boolean worldInteractionEvents = true;

    @ConfigurableProperty(category = "core", comment = "If items should be ejected into the world when item movement failed due to item handlers declaring inconsistent movement in simulation mode. If disabled, items can be voided.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static boolean ejectItemsOnInconsistentSimulationMovement = true;

    @ConfigurableProperty(category = "core", comment = "If items should be ejected into the world when a block is broken and not all items fit into the target. Will be voided otherwise.", isCommandable = true)
    public static boolean ejectItemsOnBlockDropOverflow = true;

    @ConfigurableProperty(category = "general", comment = "The base energy usage for the energy exporter.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int exporterEnergyBaseConsumption = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the fluid exporter.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int exporterFluidBaseConsumption = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the item exporter.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int exporterItemBaseConsumption = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world block exporter when it has a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int exporterWorldBlockBaseConsumptionEnabled = 32;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world block exporter when it does not have a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int exporterWorldBlockBaseConsumptionDisabled = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world energy exporter when it has a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int exporterWorldEnergyBaseConsumptionEnabled = 32;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world energy exporter when it does not have a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int exporterWorldEnergyBaseConsumptionDisabled = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world fluid exporter when it has a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int exporterWorldFluidBaseConsumptionEnabled = 32;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world fluid exporter when it does not have a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int exporterWorldFluidBaseConsumptionDisabled = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world item exporter when it has a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int exporterWorldItemBaseConsumptionEnabled = 32;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world item exporter when it does not have a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int exporterWorldItemBaseConsumptionDisabled = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the energy importer.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int importerEnergyBaseConsumption = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the fluid importer.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int importerFluidBaseConsumption = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the item importer.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int importerItemBaseConsumption = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world block importer when it has a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int importerWorldBlockBaseConsumptionEnabled = 32;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world block importer when it does not have a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int importerWorldBlockBaseConsumptionDisabled = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world energy importer when it has a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int importerWorldEnergyBaseConsumptionEnabled = 32;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world energy importer when it does not have a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int importerWorldEnergyBaseConsumptionDisabled = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world fluid importer when it has a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int importerWorldFluidBaseConsumptionEnabled = 32;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world fluid importer when it does not have a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int importerWorldFluidBaseConsumptionDisabled = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world item importer when it has a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int importerWorldItemBaseConsumptionEnabled = 32;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the world item importer when it does not have a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int importerWorldItemBaseConsumptionDisabled = 1;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the energy interface.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int interfaceEnergyBaseConsumption = 0;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the fluid interface.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int interfaceFluidBaseConsumption = 0;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the item interface.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int interfaceItemBaseConsumption = 0;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the player simulator when it has a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int playerSimulatorBaseConsumptionEnabled = 64;
    @ConfigurableProperty(category = "general", comment = "The base energy usage for the player simulator when it does not have a variable.", minimalValue = 0, configLocation = ModConfig.Type.SERVER)
    public static int playerSimulatorBaseConsumptionDisabled = 1;

    public GeneralConfig() {
        super(IntegratedTunnels._instance, "general");
    }

    @Override
    public void onRegistered() {
        if(analytics) {
            Analytics.registerMod(getMod(), Reference.GA_TRACKING_ID);
        }
        if(versionChecker) {
            Versions.registerMod(getMod(), IntegratedTunnels._instance, Reference.VERSION_URL);
        }
    }
}
