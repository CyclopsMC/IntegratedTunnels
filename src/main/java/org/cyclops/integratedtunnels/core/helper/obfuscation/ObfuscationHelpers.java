package org.cyclops.integratedtunnels.core.helper.obfuscation;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
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
	
}
