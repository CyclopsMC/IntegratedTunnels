package org.cyclops.integratedtunnels;

import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.ConfigurableType;
import org.cyclops.cyclopscore.config.ConfigurableTypeCategory;
import org.cyclops.cyclopscore.config.extendedconfig.DummyConfig;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.tracking.Analytics;
import org.cyclops.cyclopscore.tracking.Versions;

/**
 * A config with general options for this mod.
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    /**
     * The current mod version, will be used to check if the player's config isn't out of date and
     * warn the player accordingly.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "Config version for " + Reference.MOD_NAME +".\nDO NOT EDIT MANUALLY!", showInGui = false)
    public static String version = Reference.MOD_VERSION;

    /**
     * If the debug mode should be enabled. @see Debug
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "Set 'true' to enable development debug mode. This will result in a lower performance!", requiresMcRestart = true)
    public static boolean debug = false;

    /**
     * If the recipe loader should crash when finding invalid recipes.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "If the recipe loader should crash when finding invalid recipes.", requiresMcRestart = true)
    public static boolean crashOnInvalidRecipe = false;

    /**
     * If mod compatibility loader should crash hard if errors occur in that process.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "If mod compatibility loader should crash hard if errors occur in that process.", requiresMcRestart = true)
    public static boolean crashOnModCompatCrash = false;

    /**
     * If an anonymous mod startup analytics request may be sent to our analytics service.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "If an anonymous mod startup analytics request may be sent to our analytics service.")
    public static boolean analytics = true;

    /**
     * If the version checker should be enabled.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "If the version checker should be enabled.")
    public static boolean versionChecker = true;

    /**
     * How many ticks importers/exporters should sleep until checking targets again when they were previously unchanged.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "How many ticks importers/exporters should sleep until checking targets again when they were previously unchanged.")
    public static int inventoryUnchangedTickTimeout = 10;

    /**
     * The maximum network fluid transfer rate.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "The maximum network fluid transfer rate.", isCommandable = true, minimalValue = 0)
    public static int fluidRateLimit = Integer.MAX_VALUE;

    /**
     * If particles should be shown and sounds should be played when tunnels are interacting with the world.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "If particles should be shown and sounds should be played when tunnels are interacting with the world.", isCommandable = true)
    public static boolean worldInteractionEvents = true;

    /**
     * If items should be ejected into the world when item movement failed due to item handlers declaring inconsistent movement in simulation mode. If disabled, items can be voided.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "If items should be ejected into the world when item movement failed due to item handlers declaring inconsistent movement in simulation mode. If disabled, items can be voided.", isCommandable = true)
    public static boolean ejectItemsOnInconsistentSimulationMovement = true;
    
    /**
     * The base energy usage for the energy exporter.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the energy exporter.", minimalValue = 0)
    public static int exporterEnergyBaseConsumption = 1;
    
    /**
     * The base energy usage for the fluid exporter.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the fluid exporter.", minimalValue = 0)
    public static int exporterFluidBaseConsumption = 1;
    
    /**
     * The base energy usage for the item exporter.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the item exporter.", minimalValue = 0)
    public static int exporterItemBaseConsumption = 1;
    
    /**
     * The base energy usage for the world block exporter when it has a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world block exporter when it has a variable.", minimalValue = 0)
    public static int exporterWorldBlockBaseConsumptionEnabled = 32;
    
    /**
     * The base energy usage for the world block exporter when it does not have a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world block exporter when it does not have a variable.", minimalValue = 0)
    public static int exporterWorldBlockBaseConsumptionDisabled = 1;
    
    /**
     * The base energy usage for the world energy exporter when it has a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world energy exporter when it has a variable.", minimalValue = 0)
    public static int exporterWorldEnergyBaseConsumptionEnabled = 32;
    
    /**
     * The base energy usage for the world energy exporter when it does not have a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world energy exporter when it does not have a variable.", minimalValue = 0)
    public static int exporterWorldEnergyBaseConsumptionDisabled = 1;
    
    /**
     * The base energy usage for the world fluid exporter when it has a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world fluid exporter when it has a variable.", minimalValue = 0)
    public static int exporterWorldFluidBaseConsumptionEnabled = 32;
    
    /**
     * The base energy usage for the world fluid exporter when it does not have a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world fluid exporter when it does not have a variable.", minimalValue = 0)
    public static int exporterWorldFluidBaseConsumptionDisabled = 1;
    
    /**
     * The base energy usage for the world item exporter when it has a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world item exporter when it has a variable.", minimalValue = 0)
    public static int exporterWorldItemBaseConsumptionEnabled = 32;
    
    /**
     * The base energy usage for the world item exporter when it does not have a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world item exporter when it does not have a variable.", minimalValue = 0)
    public static int exporterWorldItemBaseConsumptionDisabled = 1;
    
    /**
     * The base energy usage for the energy importer.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the energy importer.", minimalValue = 0)
    public static int importerEnergyBaseConsumption = 1;
    
    /**
     * The base energy usage for the fluid importer.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the fluid importer.", minimalValue = 0)
    public static int importerFluidBaseConsumption = 1;
    
    /**
     * The base energy usage for the item importer.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the item importer.", minimalValue = 0)
    public static int importerItemBaseConsumption = 1;
    
    /**
     * The base energy usage for the world block importer when it has a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world block importer when it has a variable.", minimalValue = 0)
    public static int importerWorldBlockBaseConsumptionEnabled = 32;
    
    /**
     * The base energy usage for the world block importer when it does not have a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world block importer when it does not have a variable.", minimalValue = 0)
    public static int importerWorldBlockBaseConsumptionDisabled = 1;
    
    /**
     * The base energy usage for the world energy importer when it has a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world energy importer when it has a variable.", minimalValue = 0)
    public static int importerWorldEnergyBaseConsumptionEnabled = 32;
    
    /**
     * The base energy usage for the world energy importer when it does not have a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world energy importer when it does not have a variable.", minimalValue = 0)
    public static int importerWorldEnergyBaseConsumptionDisabled = 1;
    
    /**
     * The base energy usage for the world fluid importer when it has a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world fluid importer when it has a variable.", minimalValue = 0)
    public static int importerWorldFluidBaseConsumptionEnabled = 32;
    
    /**
     * The base energy usage for the world fluid importer when it does not have a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world fluid importer when it does not have a variable.", minimalValue = 0)
    public static int importerWorldFluidBaseConsumptionDisabled = 1;
    
    /**
     * The base energy usage for the world item importer when it has a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world item importer when it has a variable.", minimalValue = 0)
    public static int importerWorldItemBaseConsumptionEnabled = 32;
    
    /**
     * The base energy usage for the world item importer when it does not have a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the world item importer when it does not have a variable.", minimalValue = 0)
    public static int importerWorldItemBaseConsumptionDisabled = 1;
    
    /**
     * The base energy usage for the energy interface.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the energy interface.", minimalValue = 0)
    public static int interfaceEnergyBaseConsumption = 0;
    
    /**
     * The base energy usage for the fluid interface.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the fluid interface.", minimalValue = 0)
    public static int interfaceFluidBaseConsumption = 0;
    
    /**
     * The base energy usage for the item interface.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the item interface.", minimalValue = 0)
    public static int interfaceItemBaseConsumption = 0;
    
    /**
     * The base energy usage for the player simulator when it has a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the player simulator when it has a variable.", minimalValue = 0)
    public static int playerSimulatorBaseConsumptionEnabled = 64;
    
    /**
     * The base energy usage for the player simulator when it does not have a variable.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "The base energy usage for the player simulator when it does not have a variable.", minimalValue = 0)
    public static int playerSimulatorBaseConsumptionDisabled = 1;
    
    

    /**
     * The type of this config.
     */
    public static ConfigurableType TYPE = ConfigurableType.DUMMY;

    /**
     * Create a new instance.
     */
    public GeneralConfig() {
        super(IntegratedTunnels._instance, true, "general", null, GeneralConfig.class);
    }

    @Override
    public void onRegistered() {
        getMod().putGenericReference(ModBase.REFKEY_CRASH_ON_INVALID_RECIPE, GeneralConfig.crashOnInvalidRecipe);
        getMod().putGenericReference(ModBase.REFKEY_DEBUGCONFIG, GeneralConfig.debug);
        getMod().putGenericReference(ModBase.REFKEY_CRASH_ON_MODCOMPAT_CRASH, GeneralConfig.crashOnModCompatCrash);

        if(analytics) {
            Analytics.registerMod(getMod(), Reference.GA_TRACKING_ID);
        }
        if(versionChecker) {
            Versions.registerMod(getMod(), IntegratedTunnels._instance, Reference.VERSION_URL);
        }
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}
