package org.cyclops.integratedtunnels;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ObjectHolder;
import org.cyclops.integratedtunnels.core.part.ContainerInterfaceSettings;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
public class RegistryEntries {

    @ObjectHolder(registryName = "item", value = "integratedtunnels:part_interface_item")
    public static final Item ITEM_PART_INTERFACE = null;
    @ObjectHolder(registryName = "item", value = "integratedtunnels:dummy_pickaxe")
    public static final Item ITEM_DUMMY_PICKAXE = null;

    @ObjectHolder(registryName = "menu", value = "integratedtunnels:part_interface_settings")
    public static final MenuType<ContainerInterfaceSettings> CONTAINER_INTERFACE_SETTINGS = null;

}
