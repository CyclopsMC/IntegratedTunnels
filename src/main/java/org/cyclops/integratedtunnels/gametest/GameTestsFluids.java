package org.cyclops.integratedtunnels.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.blockentity.BlockEntityDryingBasin;
import org.cyclops.integrateddynamics.core.block.IgnoredBlockStatus;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeList;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsFluids {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 2000;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidImporterToInterfaceBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_FLUID, new ItemStack(PartTypes.IMPORTER_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluid in importer basin
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in importer
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Fluid.BOOLEAN_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 1_000, "Basin out does not contain fluids");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 0, "Basin in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypes.IMPORTER_FLUID.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Fluid.BOOLEAN_IMPORT, "Active aspect is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Fluid.BOOLEAN_IMPORT).isEmpty(), "Active aspect has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidInterfaceToExporterBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place fluid exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.EXPORTER_FLUID, new ItemStack(PartTypes.EXPORTER_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert items in interface chest
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in exporter
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.Fluid.BOOLEAN_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 1_000, "Basin out does not contain fluids");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 0, "Basin in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Exporter is deactivated");
            helper.assertValueEqual(
                    PartTypes.EXPORTER_FLUID.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Fluid.BOOLEAN_EXPORT, "Active aspect is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Fluid.BOOLEAN_EXPORT).isEmpty(), "Active aspect has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidImporterToInterfaceToExporterBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_FLUID, new ItemStack(PartTypes.IMPORTER_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place fluid exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_FLUID, new ItemStack(PartTypes.EXPORTER_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().north(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluids in interface basin
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Fluid.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in exporter
        ItemStack variableAspectExporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.Fluid.BOOLEAN_EXPORT, variableAspectExporter);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinInterface = helper.getBlockEntity(POS.east().east());
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().north());
            helper.assertValueEqual(basinInterface.getTank().getFluidAmount(), 0, "Basin interface was not drained");
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 1_000, "Basin out does not contain fluids");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 0, "Basin in was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidsImporterToInterfaceFluidCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_FLUID, new ItemStack(PartTypes.IMPORTER_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluids in importer basin
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_FLUIDSTACK, ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.WATER, 100)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Fluid.FLUIDSTACK_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 1_000, "Basin out does not contain fluids");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 0, "Basin in was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidsImporterToInterfaceFluidIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_FLUID, new ItemStack(PartTypes.IMPORTER_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluids in importer basin
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_FLUIDSTACK, ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.LAVA, 100)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Fluid.FLUIDSTACK_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if fluid is not moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 0, "Basin out was filled");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 1_000, "Basin in was drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidsImporterToFilteredInterfaceBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_FLUID, new ItemStack(PartTypes.IMPORTER_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FILTERING_FLUID, new ItemStack(PartTypes.INTERFACE_FILTERING_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluids in importer basin
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Fluid.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in filtering interface
        ItemStack variableAspectInterface = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.FluidFilter.BOOLEAN_SET_FILTER, variableAspectInterface);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 1_000, "Basin out does not contain fluids");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 0, "Basin in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypes.IMPORTER_FLUID.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status importer is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Fluid.BOOLEAN_IMPORT, "Active aspect importer is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Fluid.BOOLEAN_IMPORT).isEmpty(), "Active aspect importer has errors");

            // Check filtering interface state
            IPartStateWriter partStateInterface = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateInterface.isDeactivated(), "Filtering interface is deactivated");
            helper.assertValueEqual(
                    PartTypes.INTERFACE_FILTERING_FLUID.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status filtering interface is incorrect"
            );
            helper.assertValueEqual(partStateInterface.getActiveAspect(), TunnelAspects.Write.FluidFilter.BOOLEAN_SET_FILTER, "Active aspect filtering interface is incorrect");
            helper.assertTrue(partStateInterface.getErrors(TunnelAspects.Write.FluidFilter.BOOLEAN_SET_FILTER).isEmpty(), "Active aspect filtering interface has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidsImporterToFilteredInterfaceFluidCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_FLUID, new ItemStack(PartTypes.IMPORTER_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FILTERING_FLUID, new ItemStack(PartTypes.INTERFACE_FILTERING_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluids in importer basin
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Fluid.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in filtering interface
        ItemStack variableAspectInterface = createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_FLUIDSTACK, ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.WATER, 100)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.FluidFilter.FLUIDSTACK_SET_FILTER, variableAspectInterface);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 1_000, "Basin out does not contain fluids");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 0, "Basin in was not drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypes.IMPORTER_FLUID.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status importer is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Fluid.BOOLEAN_IMPORT, "Active aspect importer is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Fluid.BOOLEAN_IMPORT).isEmpty(), "Active aspect importer has errors");

            // Check filtering interface state
            IPartStateWriter partStateInterface = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateInterface.isDeactivated(), "Filtering interface is deactivated");
            helper.assertValueEqual(
                    PartTypes.INTERFACE_FILTERING_FLUID.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status filtering interface is incorrect"
            );
            helper.assertValueEqual(partStateInterface.getActiveAspect(), TunnelAspects.Write.FluidFilter.FLUIDSTACK_SET_FILTER, "Active aspect filtering interface is incorrect");
            helper.assertTrue(partStateInterface.getErrors(TunnelAspects.Write.FluidFilter.FLUIDSTACK_SET_FILTER).isEmpty(), "Active aspect filtering interface has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidsImporterToFilteredInterfaceFluidIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_FLUID, new ItemStack(PartTypes.IMPORTER_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FILTERING_FLUID, new ItemStack(PartTypes.INTERFACE_FILTERING_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluids in importer basin
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Fluid.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in filtering interface
        ItemStack variableAspectInterface = createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_FLUIDSTACK, ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.LAVA, 100)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.FluidFilter.FLUIDSTACK_SET_FILTER, variableAspectInterface);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 0, "Basin out contains fluids");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 1_000, "Basin in was drained");

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypes.IMPORTER_FLUID.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status importer is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Fluid.BOOLEAN_IMPORT, "Active aspect importer is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Fluid.BOOLEAN_IMPORT).isEmpty(), "Active aspect importer has errors");

            // Check filtering interface state
            IPartStateWriter partStateInterface = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateInterface.isDeactivated(), "Filtering interface is deactivated");
            helper.assertValueEqual(
                    PartTypes.INTERFACE_FILTERING_FLUID.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status filtering interface is incorrect"
            );
            helper.assertValueEqual(partStateInterface.getActiveAspect(), TunnelAspects.Write.FluidFilter.FLUIDSTACK_SET_FILTER, "Active aspect filtering interface is incorrect");
            helper.assertTrue(partStateInterface.getErrors(TunnelAspects.Write.FluidFilter.FLUIDSTACK_SET_FILTER).isEmpty(), "Active aspect filtering interface has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidsImporterToInterfaceFluidListCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_FLUID, new ItemStack(PartTypes.IMPORTER_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluids in importer basin
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.LAVA, 100)),
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.WATER, 100))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Fluid.LIST_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 1_000, "Basin out does not contain fluids");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 0, "Basin in was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidsImporterToInterfaceFluidListIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_FLUID, new ItemStack(PartTypes.IMPORTER_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluids in importer basin
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in importer
        ItemStack variableAspectImporter = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.LAVA, 100)),
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.FLOWING_LAVA, 100))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Fluid.LIST_IMPORT, variableAspectImporter);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 0, "Basin out contains fluids");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 1_000, "Basin in was drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidsImporterToInterfaceFluidListBlacklistCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_FLUID, new ItemStack(PartTypes.IMPORTER_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluids in importer basin
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.LAVA, 100)),
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.FLOWING_LAVA, 100))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Fluid.LIST_IMPORT, variableAspect);

        // Enable blacklist
        PartPos posImporter = PartPos.of(DimPos.of(helper.getLevel(), helper.absolutePos(POS)), Direction.WEST);
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posImporter);
        IAspectProperties properties = TunnelAspects.Write.Fluid.LIST_IMPORT.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posImporter), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(true));
        partStateHolder.getState().setAspectProperties(TunnelAspects.Write.Fluid.LIST_IMPORT, properties);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 1_000, "Basin out does not contain fluids");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 0, "Basin in was not drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidsImporterToInterfaceFluidListBlacklistIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_FLUID, new ItemStack(PartTypes.IMPORTER_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluids in importer basin
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.WATER, 100)),
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.LAVA, 100))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Fluid.LIST_IMPORT, variableAspect);

        // Enable blacklist
        PartPos posImporter = PartPos.of(DimPos.of(helper.getLevel(), helper.absolutePos(POS)), Direction.WEST);
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posImporter);
        IAspectProperties properties = TunnelAspects.Write.Fluid.LIST_IMPORT.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posImporter), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(true));
        partStateHolder.getState().setAspectProperties(TunnelAspects.Write.Fluid.LIST_IMPORT, properties);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 0, "Basin out contains fluids");
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 1_000, "Basin in was drained");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidInterfaceToWorldExporterListCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place fluid world exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.EXPORTER_WORLD_FLUID, new ItemStack(PartTypes.EXPORTER_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluid in tank
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place variable in exporter
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.LAVA, 1000)),
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.WATER, 1000))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.World.FLUID_LIST_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 0, "Basin in was not drained");
            helper.assertBlockPresent(Blocks.WATER, POS.east().east());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidInterfaceToWorldExporterListIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place fluid world exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.EXPORTER_WORLD_FLUID, new ItemStack(PartTypes.EXPORTER_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluid in tank
        BlockEntityDryingBasin basinIn = helper.getBlockEntity(POS.west());
        basinIn.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));

        // Place variable in exporter
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.LAVA, 1000)),
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.FLOWING_LAVA, 1000))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.World.FLUID_LIST_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if fluid is not moved
            helper.assertValueEqual(basinIn.getTank().getFluidAmount(), 1_000, "Basin in was drained");
            helper.assertBlockNotPresent(Blocks.WATER, POS.east().east());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidInterfacesToWorldExporterListCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid interfaces
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.NORTH, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.SOUTH, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place fluid world exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.EXPORTER_WORLD_FLUID, new ItemStack(PartTypes.EXPORTER_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.north(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.south(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluid in tank
        BlockEntityDryingBasin basinIn1 = helper.getBlockEntity(POS.west());
        BlockEntityDryingBasin basinIn2 = helper.getBlockEntity(POS.north());
        BlockEntityDryingBasin basinIn3 = helper.getBlockEntity(POS.south());
        basinIn1.getTank().setFluid(new FluidStack(RegistryEntries.FLUID_MENRIL_RESIN, 1_000));
        basinIn2.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));
        basinIn3.getTank().setFluid(new FluidStack(Fluids.LAVA, 1_000));

        // Place variable in exporter
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.WATER, 1000)),
                ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.LAVA, 1000))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.World.FLUID_LIST_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            helper.assertValueEqual(basinIn1.getTank().getFluidAmount(), 1000, "Basin in 1 was drained");
            helper.assertValueEqual(basinIn2.getTank().getFluidAmount(), 0, "Basin in 2 was not drained");
            helper.assertValueEqual(basinIn3.getTank().getFluidAmount(), 1000, "Basin in 3 was drained");
            helper.assertBlockPresent(Blocks.WATER, POS.east().east());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testFluidInterfaceToExporterFluidFromSubnet(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place fluid exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.EXPORTER_FLUID, new ItemStack(PartTypes.EXPORTER_FLUID.getItem()));

        // Place cable for subnet
        helper.setBlock(POS.west(), RegistryEntries.BLOCK_CABLE.value());

        // Place fluid interfaces in subnet
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.west()), Direction.WEST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.west()), Direction.NORTH, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.west()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place drying basins
        helper.setBlock(POS.west().west(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.west().north(), RegistryEntries.BLOCK_DRYING_BASIN.get());
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Insert fluids in subnet basins
        BlockEntityDryingBasin basinIn1 = helper.getBlockEntity(POS.west().west());
        basinIn1.getTank().setFluid(new FluidStack(Fluids.WATER, 1_000));
        BlockEntityDryingBasin basinIn2 = helper.getBlockEntity(POS.west().north());
        basinIn2.getTank().setFluid(new FluidStack(RegistryEntries.FLUID_MENRIL_RESIN, 1_000));

        // Place empty variable in exporter
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_FLUIDSTACK, ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(RegistryEntries.FLUID_MENRIL_RESIN, 100)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.Fluid.FLUIDSTACK_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if fluid is moved
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east());
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 1_000, "Basin out does not contain fluids");
            helper.assertValueEqual(basinOut.getTank().getFluidType(), RegistryEntries.FLUID_MENRIL_RESIN.get(), "Basin out does not contain the correct fluid type");
            helper.assertValueEqual(basinIn1.getTank().getFluidAmount(), 1_000, "Basin in 1 was incorrectly drained");
            helper.assertValueEqual(basinIn2.getTank().getFluidAmount(), 0, "Basin in 2 was not drained");
        });
    }

}
