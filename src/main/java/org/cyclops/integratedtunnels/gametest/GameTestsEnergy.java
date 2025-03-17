package org.cyclops.integratedtunnels.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.blockentity.BlockEntityEnergyBattery;
import org.cyclops.integrateddynamics.core.block.IgnoredBlockStatus;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsEnergy {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 2000;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testEnergyImporterToInterfaceBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place energy importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ENERGY, new ItemStack(PartTypes.IMPORTER_ENERGY.getItem()));

        // Place energy interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ENERGY, new ItemStack(PartTypes.INTERFACE_ENERGY.getItem()));

        // Place batteries
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());

        // Insert energy in interface battery
        BlockEntityEnergyBattery batteryIn = helper.getBlockEntity(POS.west());
        batteryIn.setEnergyStored(10_000);

        // Place empty variable in importer
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Energy.BOOLEAN_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if energy is moved
            BlockEntityEnergyBattery batteryOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(batteryOut.getEnergyStored(), 10_000, "Battery out does not contain energy");
            helper.assertValueEqual(batteryIn.getEnergyStored(), 0, "Battery in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypes.IMPORTER_ENERGY.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Energy.BOOLEAN_IMPORT, "Active aspect is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Energy.BOOLEAN_IMPORT).isEmpty(), "Active aspect has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testEnergyInterfaceToExporterBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place energy interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.INTERFACE_ENERGY, new ItemStack(PartTypes.INTERFACE_ENERGY.getItem()));

        // Place energy exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.EXPORTER_ENERGY, new ItemStack(PartTypes.EXPORTER_ENERGY.getItem()));

        // Place batteries
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());

        // Insert energy in interface battery
        BlockEntityEnergyBattery batteryIn = helper.getBlockEntity(POS.west());
        batteryIn.setEnergyStored(10_000);

        // Place empty variable in exporter
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.Energy.BOOLEAN_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if energy is moved
            BlockEntityEnergyBattery batteryOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(batteryOut.getEnergyStored(), 10_000, "Battery out does not contain energy");
            helper.assertValueEqual(batteryIn.getEnergyStored(), 0, "Battery in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Exporter is deactivated");
            helper.assertValueEqual(
                    PartTypes.EXPORTER_ENERGY.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Energy.BOOLEAN_EXPORT, "Active aspect is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Energy.BOOLEAN_EXPORT).isEmpty(), "Active aspect has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testEnergyImporterToInterfaceToExporterBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ENERGY, new ItemStack(PartTypes.IMPORTER_ENERGY.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ENERGY, new ItemStack(PartTypes.INTERFACE_ENERGY.getItem()));

        // Place item exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_ENERGY, new ItemStack(PartTypes.EXPORTER_ENERGY.getItem()));

        // Place batteries
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());
        helper.setBlock(POS.east().north(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());

        // Insert energy in interface battery
        BlockEntityEnergyBattery batteryIn = helper.getBlockEntity(POS.west());
        batteryIn.setEnergyStored(10_000);

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Energy.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in exporter
        ItemStack variableAspectExporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.Energy.BOOLEAN_EXPORT, variableAspectExporter);

        helper.succeedWhen(() -> {
            // Check if energy is moved
            BlockEntityEnergyBattery batteryInterface = helper.getBlockEntity(POS.east().east());
            BlockEntityEnergyBattery batteryOut = helper.getBlockEntity(POS.east().north());
            helper.assertValueEqual(batteryInterface.getEnergyStored(), 0, "Battery interface was not drained");
            helper.assertValueEqual(batteryOut.getEnergyStored(), 10_000, "Battery out does not contain energy");
            helper.assertValueEqual(batteryIn.getEnergyStored(), 0, "Battery in was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testEnergyImporterToInterfaceLong(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ENERGY, new ItemStack(PartTypes.IMPORTER_ENERGY.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ENERGY, new ItemStack(PartTypes.INTERFACE_ENERGY.getItem()));

        // Place batteries
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());

        // Insert energy in importer battery
        BlockEntityEnergyBattery batteryIn = helper.getBlockEntity(POS.west());
        batteryIn.setEnergyStored(10_000);

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LONG, ValueTypeLong.ValueLong.of(10));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Energy.LONG_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if energy is moved
            BlockEntityEnergyBattery batteryOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(batteryOut.getEnergyStored(), 10_000, "Battery out does not contain energy");
            helper.assertValueEqual(batteryIn.getEnergyStored(), 0, "Battery in was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testEnergysImporterToFilteredInterfaceBooleanTrue(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place energy importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ENERGY, new ItemStack(PartTypes.IMPORTER_ENERGY.getItem()));

        // Place energy interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FILTERING_ENERGY, new ItemStack(PartTypes.INTERFACE_FILTERING_ENERGY.getItem()));

        // Place batteries
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());

        // Insert energy in interface battery
        BlockEntityEnergyBattery batteryIn = helper.getBlockEntity(POS.west());
        batteryIn.setEnergyStored(10_000);

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Energy.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in filtering interface
        ItemStack variableAspectInterface = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.EnergyFilter.BOOLEAN_SET_FILTER, variableAspectInterface);

        helper.succeedWhen(() -> {
            // Check if energy is moved
            BlockEntityEnergyBattery batteryOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(batteryOut.getEnergyStored(), 10_000, "Battery out does not contain energy");
            helper.assertValueEqual(batteryIn.getEnergyStored(), 0, "Battery in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypes.IMPORTER_ENERGY.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status importer is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Energy.BOOLEAN_IMPORT, "Active aspect importer is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Energy.BOOLEAN_IMPORT).isEmpty(), "Active aspect importer has errors");

            // Check filtering interface state
            IPartStateWriter partStateInterface = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateInterface.isDeactivated(), "Filtering interface is deactivated");
            helper.assertValueEqual(
                    PartTypes.INTERFACE_FILTERING_ENERGY.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status filtering interface is incorrect"
            );
            helper.assertValueEqual(partStateInterface.getActiveAspect(), TunnelAspects.Write.EnergyFilter.BOOLEAN_SET_FILTER, "Active aspect filtering interface is incorrect");
            helper.assertTrue(partStateInterface.getErrors(TunnelAspects.Write.EnergyFilter.BOOLEAN_SET_FILTER).isEmpty(), "Active aspect filtering interface has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testEnergysImporterToFilteredInterfaceBooleanFalse(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place energy importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ENERGY, new ItemStack(PartTypes.IMPORTER_ENERGY.getItem()));

        // Place energy interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FILTERING_ENERGY, new ItemStack(PartTypes.INTERFACE_FILTERING_ENERGY.getItem()));

        // Place batteries
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_ENERGY_BATTERY.get());

        // Insert energy in interface battery
        BlockEntityEnergyBattery batteryIn = helper.getBlockEntity(POS.west());
        batteryIn.setEnergyStored(10_000);

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Energy.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in filtering interface
        ItemStack variableAspectInterface = createVariableForValue(helper.getLevel(), ValueTypes.BOOLEAN, ValueTypeBoolean.ValueBoolean.of(false));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.EnergyFilter.BOOLEAN_SET_FILTER, variableAspectInterface);

        helper.succeedWhen(() -> {
            // Check if energy is not moved
            BlockEntityEnergyBattery batteryOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(batteryOut.getEnergyStored(), 0, "Battery out contains energy");
            helper.assertValueEqual(batteryIn.getEnergyStored(), 10_000, "Battery in was drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypes.IMPORTER_ENERGY.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status importer is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Energy.BOOLEAN_IMPORT, "Active aspect importer is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Energy.BOOLEAN_IMPORT).isEmpty(), "Active aspect importer has errors");

            // Check filtering interface state
            IPartStateWriter partStateInterface = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateInterface.isDeactivated(), "Filtering interface is deactivated");
            helper.assertValueEqual(
                    PartTypes.INTERFACE_FILTERING_ENERGY.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status filtering interface is incorrect"
            );
            helper.assertValueEqual(partStateInterface.getActiveAspect(), TunnelAspects.Write.EnergyFilter.BOOLEAN_SET_FILTER, "Active aspect filtering interface is incorrect");
            helper.assertTrue(partStateInterface.getErrors(TunnelAspects.Write.EnergyFilter.BOOLEAN_SET_FILTER).isEmpty(), "Active aspect filtering interface has errors");
        });
    }

}
