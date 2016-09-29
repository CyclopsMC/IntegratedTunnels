package org.cyclops.integratedtunnels.part;

import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.part.IPartTypeRegistry;

/**
 * @author rubensworks
 */
public class PartTypes {

    public static final IPartTypeRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager().getRegistry(IPartTypeRegistry.class);

    public static void load() {}

    public static final PartTypeInterfaceEnergy INTERFACE_ENERGY = REGISTRY.register(new PartTypeInterfaceEnergy("interfaceEnergy"));
    public static final PartTypeImporterEnergy IMPORTER_ENERGY = REGISTRY.register(new PartTypeImporterEnergy("importerEnergy"));
    public static final PartTypeExporterEnergy EXPORTER_ENERGY = REGISTRY.register(new PartTypeExporterEnergy("exporterEnergy"));

    public static final PartTypeInterfaceItem INTERFACE_ITEM = REGISTRY.register(new PartTypeInterfaceItem("interfaceItem"));
    public static final PartTypeImporterItem IMPORTER_ITEM = REGISTRY.register(new PartTypeImporterItem("importerItem"));
    public static final PartTypeExporterItem EXPORTER_ITEM = REGISTRY.register(new PartTypeExporterItem("exporterItem"));


}
