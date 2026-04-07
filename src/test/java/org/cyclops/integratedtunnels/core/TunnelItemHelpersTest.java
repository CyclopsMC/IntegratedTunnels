package org.cyclops.integratedtunnels.core;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author rubensworks
 */
public class TunnelItemHelpersTest {

    static {
        // Bind components so ItemStack construction works in 26.1 (components are normally bound during resource reload)
        DataComponentMap defaultComponents = DataComponentMap.builder().set(DataComponents.MAX_STACK_SIZE, 64).build();
        Items.APPLE.builtInRegistryHolder().bindComponents(defaultComponents);
    }

    @Test
    public void prototypeWithCountSame() {
        ItemStack stack = new ItemStack(Items.APPLE, 1);
        Assertions.assertSame(TunnelItemHelpers.prototypeWithCount(stack, 1), stack);
    }

}
