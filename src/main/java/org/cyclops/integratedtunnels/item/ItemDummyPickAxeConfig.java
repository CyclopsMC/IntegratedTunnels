package org.cyclops.integratedtunnels.item;

import net.minecraft.world.item.ItemStack;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.integratedtunnels.IntegratedTunnels;

import java.util.Collection;
import java.util.Collections;

/**
 * Config for a dummy pickaxe that can harvest everything.
 * @author rubensworks
 */
public class ItemDummyPickAxeConfig extends ItemConfigCommon<IModBase> {

    public ItemDummyPickAxeConfig() {
        super(
                IntegratedTunnels._instance,
                "dummy_pickaxe",
                (eConfig, properties) -> new ItemDummyPickAxe(properties)
        );
    }

    @Override
    public Collection<ItemStack> getDefaultCreativeTabEntries() {
        return Collections.emptyList();
    }
}
