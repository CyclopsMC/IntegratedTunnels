package org.cyclops.integratedtunnels.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.block.IgnoredBlockStatus;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsItems {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 2000;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsImporterToInterfaceBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Insert items in importer chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));
        chestIn.setItem(1, new ItemStack(Items.ACACIA_LEAVES));
        chestIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in importer
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Item.BOOLEAN_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if items are moved
            helper.assertContainerContains(POS.east().east(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.east().east(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.east().east(), Items.DIAMOND_PICKAXE);
            helper.assertContainerEmpty(POS.west());

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypes.IMPORTER_ITEM.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Item.BOOLEAN_IMPORT, "Active aspect is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Item.BOOLEAN_IMPORT).isEmpty(), "Active aspect has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsInterfaceToExporterBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place item exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.EXPORTER_ITEM, new ItemStack(PartTypes.EXPORTER_ITEM.getItem()));

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Insert items in interface chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));
        chestIn.setItem(1, new ItemStack(Items.ACACIA_LEAVES));
        chestIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in exporter
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.Item.BOOLEAN_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if items are moved
            helper.assertContainerContains(POS.east().east(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.east().east(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.east().east(), Items.DIAMOND_PICKAXE);
            helper.assertContainerEmpty(POS.west());

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Exporter is deactivated");
            helper.assertValueEqual(
                    PartTypes.EXPORTER_ITEM.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Item.BOOLEAN_EXPORT, "Active aspect is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Item.BOOLEAN_EXPORT).isEmpty(), "Active aspect has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsImporterToInterfaceToExporterBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place item exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_ITEM, new ItemStack(PartTypes.EXPORTER_ITEM.getItem()));

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);
        helper.setBlock(POS.east().north(), Blocks.CHEST);

        // Insert items in importer chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));
        chestIn.setItem(1, new ItemStack(Items.ACACIA_LEAVES));
        chestIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Item.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in exporter
        ItemStack variableAspectExporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.Item.BOOLEAN_EXPORT, variableAspectExporter);

        helper.succeedWhen(() -> {
            // Check if items are moved
            helper.assertContainerContains(POS.east().north(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.east().north(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.east().north(), Items.DIAMOND_PICKAXE);
            helper.assertContainerEmpty(POS.west());
            helper.assertContainerEmpty(POS.east().east());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsImporterToInterfaceItem(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Insert items in importer chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));
        chestIn.setItem(1, new ItemStack(Items.ACACIA_LEAVES));
        chestIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.ACACIA_LEAVES)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Item.ITEMSTACK_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if items are moved
            ChestBlockEntity chestOut = helper.getBlockEntity(POS.east().east());
            helper.assertFalse(chestIn.getItem(0).isEmpty(), "Incorrect input item was moved");
            helper.assertTrue(chestIn.getItem(1).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestIn.getItem(2).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestOut.getItem(0).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(1).isEmpty(), "Incorrect output item was moved");
            helper.assertContainerContains(POS.west(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.east().east(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.west(), Items.DIAMOND_PICKAXE);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsImporterToInterfaceBooleanReducedRate(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Insert items in importer chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));
        chestIn.setItem(1, new ItemStack(Items.ACACIA_LEAVES));
        chestIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in importer
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        PartPos posImporter = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        placeVariableInWriter(helper.getLevel(), posImporter, TunnelAspects.Write.Item.BOOLEAN_IMPORT, variableAspect);

        // Reduce item transfer rate of importer
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posImporter);
        IAspectProperties properties = TunnelAspects.Write.Item.BOOLEAN_IMPORT.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posImporter), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.Item.PROP_RATE, ValueTypeInteger.ValueInteger.of(1));
        partStateHolder.getState().setAspectProperties(TunnelAspects.Write.Item.BOOLEAN_IMPORT, properties);

        helper.succeedWhen(() -> {
            // Check if items are moved
            helper.assertContainerContains(POS.east().east(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.east().east(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.east().east(), Items.DIAMOND_PICKAXE);
            helper.assertContainerEmpty(POS.west());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsNoMoveDisconnectedFakeCable(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        // No cable for interface!

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place item interface as player
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack itemStack = new ItemStack(PartTypes.INTERFACE_ITEM.getItem());
        player.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
        helper.placeAt(player, itemStack, POS.east().east().east(), Direction.WEST);

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place hopper above input chest
        helper.setBlock(POS.west().above(), Blocks.HOPPER);

        // Insert items in input hopper
        HopperBlockEntity hopperIn = helper.getBlockEntity(POS.west().above());
        hopperIn.setItem(0, new ItemStack(Items.WHITE_WOOL));
        hopperIn.setItem(1, new ItemStack(Items.ACACIA_LEAVES));
        hopperIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in importer
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Item.BOOLEAN_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if items are not moved
            helper.assertContainerContains(POS.west(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.west(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.west(), Items.DIAMOND_PICKAXE);
            helper.assertContainerEmpty(POS.east().east());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsImporterToFilteredInterfaceBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FILTERING_ITEM, new ItemStack(PartTypes.INTERFACE_FILTERING_ITEM.getItem()));

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Insert items in importer chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));
        chestIn.setItem(1, new ItemStack(Items.ACACIA_LEAVES));
        chestIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Item.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in filtering interface
        ItemStack variableAspectInterface = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.ItemFilter.BOOLEAN_SET_FILTER, variableAspectInterface);

        helper.succeedWhen(() -> {
            // Check if items are moved
            helper.assertContainerContains(POS.east().east(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.east().east(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.east().east(), Items.DIAMOND_PICKAXE);
            helper.assertContainerEmpty(POS.west());

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypes.IMPORTER_ITEM.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status importer is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Item.BOOLEAN_IMPORT, "Active aspect importer is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Item.BOOLEAN_IMPORT).isEmpty(), "Active aspect importer has errors");

            // Check filtering interface state
            IPartStateWriter partStateInterface = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateInterface.isDeactivated(), "Filtering interface is deactivated");
            helper.assertValueEqual(
                    PartTypes.INTERFACE_FILTERING_ITEM.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status filtering interface is incorrect"
            );
            helper.assertValueEqual(partStateInterface.getActiveAspect(), TunnelAspects.Write.ItemFilter.BOOLEAN_SET_FILTER, "Active aspect filtering interface is incorrect");
            helper.assertTrue(partStateInterface.getErrors(TunnelAspects.Write.ItemFilter.BOOLEAN_SET_FILTER).isEmpty(), "Active aspect filtering interface has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsImporterToFilteredInterfaceItem(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FILTERING_ITEM, new ItemStack(PartTypes.INTERFACE_FILTERING_ITEM.getItem()));

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Insert items in importer chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));
        chestIn.setItem(1, new ItemStack(Items.ACACIA_LEAVES));
        chestIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in importer
        ItemStack variableAspectImporter = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Item.BOOLEAN_IMPORT, variableAspectImporter);

        // Place empty variable in filtering interface
        ItemStack variableAspectInterface = createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.ACACIA_LEAVES)));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.ItemFilter.ITEMSTACK_SET_FILTER, variableAspectInterface);

        helper.succeedWhen(() -> {
            // Check if items are moved
            ChestBlockEntity chestOut = helper.getBlockEntity(POS.east().east());
            helper.assertFalse(chestIn.getItem(0).isEmpty(), "Incorrect input item was moved");
            helper.assertTrue(chestIn.getItem(1).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestIn.getItem(2).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestOut.getItem(0).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(1).isEmpty(), "Incorrect output item was moved");
            helper.assertContainerContains(POS.west(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.east().east(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.west(), Items.DIAMOND_PICKAXE);

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypes.IMPORTER_ITEM.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status importer is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Item.BOOLEAN_IMPORT, "Active aspect importer is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Item.BOOLEAN_IMPORT).isEmpty(), "Active aspect importer has errors");

            // Check filtering interface state
            IPartStateWriter partStateInterface = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)).getState();
            helper.assertFalse(partStateInterface.isDeactivated(), "Filtering interface is deactivated");
            helper.assertValueEqual(
                    PartTypes.INTERFACE_FILTERING_ITEM.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST)), Direction.EAST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status filtering interface is incorrect"
            );
            helper.assertValueEqual(partStateInterface.getActiveAspect(), TunnelAspects.Write.ItemFilter.ITEMSTACK_SET_FILTER, "Active aspect filtering interface is incorrect");
            helper.assertTrue(partStateInterface.getErrors(TunnelAspects.Write.ItemFilter.ITEMSTACK_SET_FILTER).isEmpty(), "Active aspect filtering interface has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsImporterToInterfacesRoundRobin(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east().above(), RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east().above().above(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place item interfaces
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east().above()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east().above().above()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);
        helper.setBlock(POS.east().east().above(), Blocks.CHEST);
        helper.setBlock(POS.east().east().above().above(), Blocks.CHEST);

        // Insert items in importer chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL, 30));

        // Place empty variable in importer
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        PartPos posImporter = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        placeVariableInWriter(helper.getLevel(), posImporter, TunnelAspects.Write.Item.BOOLEAN_IMPORT, variableAspect);

        // Reduce item transfer rate of importer
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posImporter);
        IAspectProperties properties = TunnelAspects.Write.Item.BOOLEAN_IMPORT.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posImporter), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.Item.PROP_RATE, ValueTypeInteger.ValueInteger.of(1));
        properties.setValue(TunnelAspectWriteBuilders.PROP_ROUNDROBIN, ValueTypeBoolean.ValueBoolean.of(true));
        partStateHolder.getState().setAspectProperties(TunnelAspects.Write.Item.BOOLEAN_IMPORT, properties);

        helper.succeedWhen(() -> {
            // Check if items are moved
            ChestBlockEntity chest1 = helper.getBlockEntity(POS.east().east());
            ChestBlockEntity chest2 = helper.getBlockEntity(POS.east().east().above());
            ChestBlockEntity chest3 = helper.getBlockEntity(POS.east().east().above().above());
            helper.assertTrue(chest1.getItem(0).getItem() == Items.WHITE_WOOL, "chest 1 does not contain white wool");
            helper.assertTrue(chest1.getItem(0).getCount() == 10, "chest 1 does not contain 10 white wool");
            helper.assertTrue(chest2.getItem(0).getItem() == Items.WHITE_WOOL, "chest 2 does not contain white wool");
            helper.assertTrue(chest2.getItem(0).getCount() == 10, "chest 2 does not contain 10 white wool");
            helper.assertTrue(chest3.getItem(0).getItem() == Items.WHITE_WOOL, "chest 3 does not contain white wool");
            helper.assertTrue(chest3.getItem(0).getCount() == 10, "chest 3 does not contain 10 white wool");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsImporterToInterfaceItemList(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Insert items in importer chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));
        chestIn.setItem(1, new ItemStack(Items.ACACIA_LEAVES));
        chestIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.ACACIA_LEAVES)),
                ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.DIAMOND_PICKAXE))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Item.LIST_IMPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if items are moved
            ChestBlockEntity chestOut = helper.getBlockEntity(POS.east().east());
            helper.assertFalse(chestIn.getItem(0).isEmpty(), "Incorrect input item was moved");
            helper.assertTrue(chestIn.getItem(1).isEmpty(), "Incorrect input item was moved");
            helper.assertTrue(chestIn.getItem(2).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestOut.getItem(0).isEmpty(), "Incorrect output item was moved");
            helper.assertFalse(chestOut.getItem(1).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(2).isEmpty(), "Incorrect output item was moved");
            helper.assertContainerContains(POS.west(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.east().east(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.east().east(), Items.DIAMOND_PICKAXE);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsImporterToInterfaceItemListBlacklist(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Insert items in importer chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));
        chestIn.setItem(1, new ItemStack(Items.ACACIA_LEAVES));
        chestIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in importer
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.LIST, ValueTypeList.ValueList.ofAll(
                ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.WHITE_WOOL))
        ));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Item.LIST_IMPORT, variableAspect);

        // Enable blacklist
        PartPos posImporter = PartPos.of(DimPos.of(helper.getLevel(), helper.absolutePos(POS)), Direction.WEST);
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posImporter);
        IAspectProperties properties = TunnelAspects.Write.Item.LIST_IMPORT.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posImporter), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.PROP_BLACKLIST, ValueTypeBoolean.ValueBoolean.of(true));
        partStateHolder.getState().setAspectProperties(TunnelAspects.Write.Fluid.LIST_IMPORT, properties);

        helper.succeedWhen(() -> {
            // Check if items are moved
            ChestBlockEntity chestOut = helper.getBlockEntity(POS.east().east());
            helper.assertFalse(chestIn.getItem(0).isEmpty(), "Incorrect input item was moved");
            helper.assertTrue(chestIn.getItem(1).isEmpty(), "Incorrect input item was moved");
            helper.assertTrue(chestIn.getItem(2).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestOut.getItem(0).isEmpty(), "Incorrect output item was moved");
            helper.assertFalse(chestOut.getItem(1).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(2).isEmpty(), "Incorrect output item was moved");
            helper.assertContainerContains(POS.west(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.east().east(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.east().east(), Items.DIAMOND_PICKAXE);
        });
    }

}
