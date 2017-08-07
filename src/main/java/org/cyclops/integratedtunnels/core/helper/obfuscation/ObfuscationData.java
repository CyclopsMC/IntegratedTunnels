package org.cyclops.integratedtunnels.core.helper.obfuscation;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Entries used for getting private fields and methods by using it in
 * {@link ReflectionHelper#getPrivateValue(Class, Object, String...)}.
 * These MCP mappings should be updated with every MC update!
 * @author rubensworks *
 */
public class ObfuscationData {
    /**
     * Field from {@link net.minecraft.block.Block}
     */
    public static final String[] BLOCK_GETSILKTOUCHDROP = new String[] { "getSilkTouchDrop", "func_180643_i" };

    /**
     * Field from {@link net.minecraft.server.management.PlayerInteractionManager}
     */
    public static final String[] PLAYERINTERACTIIONMANAGER_DURABILITYREMAININGONBLOCK = new String[] { "durabilityRemainingOnBlock", "field_73094_o" };
	
}
