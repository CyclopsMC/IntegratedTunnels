package org.cyclops.integratedtunnels.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.integratedtunnels.IntegratedTunnels;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * Config for a dummy pickaxe that can harvest everything.
 * @author rubensworks
 */
public class ItemDummyPickAxeConfig extends ItemConfigCommon<IModBase> {

    public ItemDummyPickAxeConfig() {
        super(
                IntegratedTunnels._instance,
                "dummy_pickaxe",
                (eConfig, properties) -> new ItemDummyPickAxe(properties.pickaxe(ToolMaterial.DIAMOND, 1, 1))
        );
    }

    @Override
    public Collection<Supplier<ItemStack>> getDefaultCreativeTabEntries() {
        return Collections.emptyList();
    }
}
