package org.cyclops.integratedtunnels.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.integratedtunnels.IntegratedTunnels;

import java.util.Collection;
import java.util.Collections;

/**
 * Config for a dummy pickaxe that can harvest everything.
 * @author rubensworks
 */
public class ItemDummyPickAxeConfig extends ItemConfig {

    public ItemDummyPickAxeConfig() {
        super(
                IntegratedTunnels._instance,
                "dummy_pickaxe",
                eConfig -> new ItemDummyPickAxe(new Item.Properties())
        );
    }

    @Override
    protected Collection<ItemStack> getDefaultCreativeTabEntries() {
        return Collections.emptyList();
    }
}
