package org.cyclops.integratedtunnels;

import com.google.common.collect.Lists;
import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.cyclopscore.config.ModConfigLocation;
import org.cyclops.cyclopscore.config.extendedconfig.DummyConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;

import java.util.List;

/**
 * A config with general options for this mod.
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfigCommon<IModBase> {

    @ConfigurablePropertyCommon(category = "core", comment = "For how many ticks importers/exporters should fail to process before they can start sleeping.", configLocation = ModConfigLocation.SERVER)
    public static int inventoryUnchangedTickCount = 3;
    @ConfigurablePropertyCommon(category = "core", comment = "How many ticks importers/exporters should sleep until checking targets again when they were previously unchanged.", configLocation = ModConfigLocation.SERVER)
    public static int inventoryUnchangedTickTimeout = 10;

    @ConfigurablePropertyCommon(category = "core", comment = "The maximum network fluid transfer rate.", isCommandable = true, minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int fluidRateLimit = Integer.MAX_VALUE;

    @ConfigurablePropertyCommon(category = "core", comment = "If particles should be shown and sounds should be played when tunnels are interacting with the world.", isCommandable = true)
    public static boolean worldInteractionEvents = true;

    @ConfigurablePropertyCommon(category = "core", comment = "If items should be ejected into the world when item movement failed due to item handlers declaring inconsistent movement in simulation mode. If disabled, items can be voided.", isCommandable = true, configLocation = ModConfigLocation.SERVER)
    public static boolean ejectItemsOnInconsistentSimulationMovement = true;

    @ConfigurablePropertyCommon(category = "core", comment = "If items should be ejected into the world when a block is broken and not all items fit into the target. Will be voided otherwise.", isCommandable = true)
    public static boolean ejectItemsOnBlockDropOverflow = true;

    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the energy exporter.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int exporterEnergyBaseConsumption = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the fluid exporter.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int exporterFluidBaseConsumption = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the item exporter.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int exporterItemBaseConsumption = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world block exporter when it has a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int exporterWorldBlockBaseConsumptionEnabled = 32;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world block exporter when it does not have a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int exporterWorldBlockBaseConsumptionDisabled = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world energy exporter when it has a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int exporterWorldEnergyBaseConsumptionEnabled = 32;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world energy exporter when it does not have a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int exporterWorldEnergyBaseConsumptionDisabled = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world fluid exporter when it has a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int exporterWorldFluidBaseConsumptionEnabled = 32;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world fluid exporter when it does not have a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int exporterWorldFluidBaseConsumptionDisabled = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world item exporter when it has a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int exporterWorldItemBaseConsumptionEnabled = 32;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world item exporter when it does not have a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int exporterWorldItemBaseConsumptionDisabled = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the energy importer.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int importerEnergyBaseConsumption = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the fluid importer.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int importerFluidBaseConsumption = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the item importer.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int importerItemBaseConsumption = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world block importer when it has a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int importerWorldBlockBaseConsumptionEnabled = 32;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world block importer when it does not have a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int importerWorldBlockBaseConsumptionDisabled = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world energy importer when it has a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int importerWorldEnergyBaseConsumptionEnabled = 32;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world energy importer when it does not have a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int importerWorldEnergyBaseConsumptionDisabled = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world fluid importer when it has a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int importerWorldFluidBaseConsumptionEnabled = 32;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world fluid importer when it does not have a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int importerWorldFluidBaseConsumptionDisabled = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world item importer when it has a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int importerWorldItemBaseConsumptionEnabled = 32;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the world item importer when it does not have a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int importerWorldItemBaseConsumptionDisabled = 1;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the energy interface.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int interfaceEnergyBaseConsumption = 0;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the fluid interface.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int interfaceFluidBaseConsumption = 0;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the item interface.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int interfaceItemBaseConsumption = 0;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the player simulator when it has a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int playerSimulatorBaseConsumptionEnabled = 64;
    @ConfigurablePropertyCommon(category = "general", comment = "The base energy usage for the player simulator when it does not have a variable.", minimalValue = 0, configLocation = ModConfigLocation.SERVER)
    public static int playerSimulatorBaseConsumptionDisabled = 1;

    @ConfigurablePropertyCommon(category = "core", comment = "A list of regex patterns for block id's that should not be importable by world block importers. Example patterns: 'minecraft:bedrock', 'minecraft:.*_portal.*', '.*:end_.*'", configLocation = ModConfigLocation.SERVER)
    public static List<String> blockImporterBlacklist = Lists.newArrayList();

    public GeneralConfig() {
        super(IntegratedTunnels._instance, "general");
    }
}
