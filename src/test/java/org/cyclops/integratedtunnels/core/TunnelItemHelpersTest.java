package org.cyclops.integratedtunnels.core;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author rubensworks
 */
public class TunnelItemHelpersTest {

    @Test
    public void prototypeWithCountSame() {
        ItemStack stack = new ItemStack(Items.APPLE, 1);
        Assertions.assertSame(TunnelItemHelpers.prototypeWithCount(stack, 1), stack);
    }

}
