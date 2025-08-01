package org.cyclops.integratedtunnels.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.cyclops.cyclopscore.gametest.GameTest;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.blockentity.BlockEntityDryingBasin;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;

public class GameTestsWorldFluid {

    public static final String TEMPLATE_EMPTY = Reference.MOD_ID + ":empty10";
    public static final int TIMEOUT = 2000;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testWorldFluidImporterToInterfaceToExporterBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_FLUID, new ItemStack(PartTypes.IMPORTER_WORLD_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place world fluid exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_WORLD_FLUID, new ItemStack(PartTypes.EXPORTER_WORLD_FLUID.getItem()));

        // Place drying basin for interface
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Place fluid before importer
        helper.setBlock(POS.west(), Blocks.WATER);

        // Place empty variable in importer and exporter
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.FLUID_BOOLEAN_IMPORT, variableAspect);
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.World.FLUID_BOOLEAN_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if fluids are moved
            helper.assertBlockNotPresent(Blocks.WATER, POS.west());
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east(), BlockEntityDryingBasin.class);
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 0, Component.literal("Basin contains fluid"));
            helper.assertBlockPresent(Blocks.WATER, POS.east().north());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testWorldFluidImporterToInterfaceToExporterBlockCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_FLUID, new ItemStack(PartTypes.IMPORTER_WORLD_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place world fluid exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_WORLD_FLUID, new ItemStack(PartTypes.EXPORTER_WORLD_FLUID.getItem()));

        // Place drying basin for interface
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Place fluid before importer
        helper.setBlock(POS.west(), Blocks.WATER);

        // Place empty variable in importer and exporter
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.FLUID_FLUIDSTACK_IMPORT, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_FLUIDSTACK, ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.WATER, 10))));
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.World.FLUID_FLUIDSTACK_EXPORT, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_FLUIDSTACK, ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.WATER, 10))));

        helper.succeedWhen(() -> {
            // Check if fluids are moved
            helper.assertBlockNotPresent(Blocks.WATER, POS.west());
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east(), BlockEntityDryingBasin.class);
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 0, Component.literal("Basin contains fluid"));
            helper.assertBlockPresent(Blocks.WATER, POS.east().north());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testWorldFluidImporterToInterfaceToExporterBlockIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world fluid importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_FLUID, new ItemStack(PartTypes.IMPORTER_WORLD_FLUID.getItem()));

        // Place fluid interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FLUID, new ItemStack(PartTypes.INTERFACE_FLUID.getItem()));

        // Place world fluid exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_WORLD_FLUID, new ItemStack(PartTypes.EXPORTER_WORLD_FLUID.getItem()));

        // Place drying basin for interface
        helper.setBlock(POS.east().east(), RegistryEntries.BLOCK_DRYING_BASIN.get());

        // Place fluid before importer
        helper.setBlock(POS.west(), Blocks.WATER);

        // Place empty variable in importer and exporter
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.FLUID_FLUIDSTACK_IMPORT, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_FLUIDSTACK, ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.LAVA, 10))));
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.World.FLUID_FLUIDSTACK_EXPORT, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_FLUIDSTACK, ValueObjectTypeFluidStack.ValueFluidStack.of(new FluidStack(Fluids.LAVA, 10))));

        helper.succeedWhen(() -> {
            // Check if fluids are not moved
            helper.assertBlockPresent(Blocks.WATER, POS.west());
            BlockEntityDryingBasin basinOut = helper.getBlockEntity(POS.east().east(), BlockEntityDryingBasin.class);
            helper.assertValueEqual(basinOut.getTank().getFluidAmount(), 0, Component.literal("Basin contains fluid"));
            helper.assertBlockNotPresent(Blocks.WATER, POS.east().north());
        });
    }

}
