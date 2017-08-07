package org.cyclops.integratedtunnels.core.helper.obfuscation;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Helper for getting private fields or methods.
 * @author rubensworks
 *
 */
public class ObfuscationHelpers {

    /**
     * Call the protected {@link net.minecraft.block.Block#getSilkTouchDrop(IBlockState)}
     * @param blockState The block state.
     * @return The drop
     */
    public static ItemStack getSilkTouchDrop(IBlockState blockState) {
        Method method = ReflectionHelper.findMethod(Block.class,
                ObfuscationData.BLOCK_GETSILKTOUCHDROP[0], ObfuscationData.BLOCK_GETSILKTOUCHDROP[1], IBlockState.class);
        try {
            return (ItemStack) method.invoke(blockState.getBlock(), blockState);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return ItemStack.EMPTY;
    }

    /**
     * Get the private value {@link PlayerInteractionManager#durabilityRemainingOnBlock}.
     * @param playerInteractionManager A player interaction manager.
     * @return The remaining block durability.
     */
    public static int getDurabilityRemaining(PlayerInteractionManager playerInteractionManager) {
        return ReflectionHelper.getPrivateValue(PlayerInteractionManager.class, playerInteractionManager, ObfuscationData.PLAYERINTERACTIIONMANAGER_DURABILITYREMAININGONBLOCK);
    }

    /**
     * Set the private value {@link PlayerInteractionManager#durabilityRemainingOnBlock}.
     * @param playerInteractionManager A player interaction manager.
     * @param durabilityRemaining The remaining block durability.
     */
    public static void setDurabilityRemaining(PlayerInteractionManager playerInteractionManager, int durabilityRemaining) {
        ReflectionHelper.setPrivateValue(PlayerInteractionManager.class, playerInteractionManager, durabilityRemaining, ObfuscationData.PLAYERINTERACTIIONMANAGER_DURABILITYREMAININGONBLOCK);
    }
	
}
