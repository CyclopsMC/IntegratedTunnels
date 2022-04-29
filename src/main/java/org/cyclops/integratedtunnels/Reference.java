package org.cyclops.integratedtunnels;

import org.cyclops.cyclopscore.helper.MinecraftHelpers;

import net.minecraft.util.Direction;

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
    public static final String GA_TRACKING_ID = "UA-65307010-10";
    public static final String VERSION_URL = "https://raw.githubusercontent.com/CyclopsMC/Versions/master/" + MinecraftHelpers.getMinecraftVersionMajorMinor() + "/IntegratedTunnels.txt";
    
    // MOD ID's
    public static final String MOD_FORGE = "forge";
    public static final String MOD_CYCLOPSCORE = "cyclopscore";
    public static final String MOD_INTEGRATEDDYNAMICS = "integrateddynamics";
    
    // Entity Inventory Sidings
    public static final Direction ENTITY_ARMOR_SIDE = Direction.NORTH;
    public static final Direction ENTITY_INVENTORY_SIDE = Direction.UP;
}
