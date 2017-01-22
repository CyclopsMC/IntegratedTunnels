package org.cyclops.integratedtunnels;

/**
 * Class that can hold basic static things that are better not hard-coded
 * like mod details, texture paths, ID's...
 * @author rubensworks (aka kroeserr)
 *
 */
@SuppressWarnings("javadoc")
public class Reference {
	
    // Mod info
    public static final String MOD_ID = "integratedtunnels";
    public static final String MOD_NAME = "Integrated Tunnels";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String MOD_BUILD_NUMBER = "@BUILD_NUMBER@";
    public static final String MOD_CHANNEL = MOD_ID;
    public static final String MOD_MC_VERSION = "@MC_VERSION@";
    public static final String GA_TRACKING_ID = "UA-65307010-10";
    public static final String VERSION_URL = "https://raw.githubusercontent.com/CyclopsMC/Versions/master/1.11/IntegratedTunnels.txt";
    
    // Paths
    public static final String TEXTURE_PATH_GUI = "textures/gui/";
    public static final String TEXTURE_PATH_SKINS = "textures/skins/";
    public static final String TEXTURE_PATH_MODELS = "textures/models/";
    public static final String TEXTURE_PATH_ENTITIES = "textures/entities/";
    public static final String TEXTURE_PATH_GUIBACKGROUNDS = "textures/gui/title/background/";
    public static final String TEXTURE_PATH_ITEMS = "textures/items/";
    public static final String TEXTURE_PATH_PARTICLES = "textures/particles/";
    public static final String MODEL_PATH = "models/";
    
    // MOD ID's
    public static final String MOD_FORGE = "forge";
    public static final String MOD_FORGE_VERSION = "@FORGE_VERSION@";
    public static final String MOD_FORGE_VERSION_MIN = "13.20.0.2201";
    public static final String MOD_CYCLOPSCORE = "cyclopscore";
    public static final String MOD_CYCLOPSCORE_VERSION = "@CYCLOPSCORE_VERSION@";
    public static final String MOD_CYCLOPSCORE_VERSION_MIN = "0.9.1";
    public static final String MOD_INTEGRATEDDYNAMICS = "integrateddynamics";
    public static final String MOD_INTEGRATEDDYNAMICS_VERSION_MIN = "0.6.7";
    public static final String MOD_TESLA = "tesla";
    
    // Dependencies
    public static final String MOD_DEPENDENCIES =
            "required-after:" + MOD_FORGE              + "@[" + MOD_FORGE_VERSION_MIN              + ",);" +
            "required-after:" + MOD_CYCLOPSCORE        + "@[" + MOD_CYCLOPSCORE_VERSION_MIN        + ",);" +
            "required-after:" + MOD_INTEGRATEDDYNAMICS + "@[" + MOD_INTEGRATEDDYNAMICS_VERSION_MIN + ",);";
}
