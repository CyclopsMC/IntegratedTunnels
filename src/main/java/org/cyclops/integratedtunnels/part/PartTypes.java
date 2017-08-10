package org.cyclops.integratedtunnels.part;

import org.cyclops.integrateddynamics.IntegratedDynamics;
import org.cyclops.integrateddynamics.api.part.IPartTypeRegistry;

/**
 * @author rubensworks
 */
public class PartTypes {

    public static final IPartTypeRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager().getRegistry(IPartTypeRegistry.class);

    public static void load() {}

    public static final PartTypeInterfaceEnergy INTERFACE_ENERGY = REGISTRY.register(new PartTypeInterfaceEnergy("interface_energy"));
    public static final PartTypeImporterEnergy IMPORTER_ENERGY = REGISTRY.register(new PartTypeImporterEnergy("importer_energy"));
    public static final PartTypeExporterEnergy EXPORTER_ENERGY = REGISTRY.register(new PartTypeExporterEnergy("exporter_energy"));
    public static final PartTypeImporterWorldEnergy IMPORTER_WORLD_ENERGY = REGISTRY.register(new PartTypeImporterWorldEnergy("importer_world_energy"));
    public static final PartTypeExporterWorldEnergy EXPORTER_WORLD_ENERGY = REGISTRY.register(new PartTypeExporterWorldEnergy("exporter_world_energy"));

    public static final PartTypeInterfaceItem INTERFACE_ITEM = REGISTRY.register(new PartTypeInterfaceItem("interface_item"));
    public static final PartTypeImporterItem IMPORTER_ITEM = REGISTRY.register(new PartTypeImporterItem("importer_item"));
    public static final PartTypeExporterItem EXPORTER_ITEM = REGISTRY.register(new PartTypeExporterItem("exporter_item"));
    public static final PartTypeImporterWorldItem IMPORTER_WORLD_ITEM = REGISTRY.register(new PartTypeImporterWorldItem("importer_world_item"));
    public static final PartTypeExporterWorldItem EXPORTER_WORLD_ITEM = REGISTRY.register(new PartTypeExporterWorldItem("exporter_world_item"));

    public static final PartTypeInterfaceFluid INTERFACE_FLUID = REGISTRY.register(new PartTypeInterfaceFluid("interface_fluid"));
    public static final PartTypeImporterFluid IMPORTER_FLUID = REGISTRY.register(new PartTypeImporterFluid("importer_fluid"));
    public static final PartTypeExporterFluid EXPORTER_FLUID = REGISTRY.register(new PartTypeExporterFluid("exporter_fluid"));
    public static final PartTypeImporterWorldFluid IMPORTER_WORLD_FLUID = REGISTRY.register(new PartTypeImporterWorldFluid("importer_world_fluid"));
    public static final PartTypeExporterWorldFluid EXPORTER_WORLD_FLUID = REGISTRY.register(new PartTypeExporterWorldFluid("exporter_world_fluid"));

    public static final PartTypeImporterWorldBlock IMPORTER_WORLD_BLOCK = REGISTRY.register(new PartTypeImporterWorldBlock("importer_world_block"));
    public static final PartTypeExporterWorldBlock EXPORTER_WORLD_BLOCK = REGISTRY.register(new PartTypeExporterWorldBlock("exporter_world_block"));

    public static final PartTypePlayerSimulator PLAYER_SIMULATOR = REGISTRY.register(new PartTypePlayerSimulator("player_simulator"));


}
