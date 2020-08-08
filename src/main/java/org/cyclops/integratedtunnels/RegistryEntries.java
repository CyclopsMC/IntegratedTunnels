package org.cyclops.integratedtunnels;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;
import org.cyclops.integratedtunnels.core.part.ContainerInterfaceSettings;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
public class RegistryEntries {

    @ObjectHolder("integratedtunnels:part_interface_item")
    public static final Item ITEM_PART_INTERFACE = null;
    @ObjectHolder("integratedtunnels:dummy_pickaxe")
    public static final Item ITEM_DUMMY_PICKAXE = null;

    @ObjectHolder("integratedtunnels:part_interface_settings")
    public static final ContainerType<ContainerInterfaceSettings> CONTAINER_INTERFACE_SETTINGS = null;

}
