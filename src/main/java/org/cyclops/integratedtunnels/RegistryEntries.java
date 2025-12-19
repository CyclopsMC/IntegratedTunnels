package org.cyclops.integratedtunnels;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.cyclops.integratedtunnels.core.part.ContainerInterfaceSettings;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
public class RegistryEntries {

    public static final DeferredHolder<Item, Item> ITEM_PART_INTERFACE = DeferredHolder.create(Registries.ITEM, Identifier.parse("integratedtunnels:part_interface_item"));
    public static final DeferredHolder<Item, Item> ITEM_DUMMY_PICKAXE = DeferredHolder.create(Registries.ITEM, Identifier.parse("integratedtunnels:dummy_pickaxe"));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerInterfaceSettings>> CONTAINER_INTERFACE_SETTINGS = DeferredHolder.create(Registries.MENU, Identifier.parse("integratedtunnels:part_interface_settings"));

}
