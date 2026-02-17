package org.cyclops.integratedtunnels.gametest;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.cyclops.cyclopscore.gametest.GameTest;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.block.IgnoredBlockStatus;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;

public class GameTestsWorldBlock {

    public static final String TEMPLATE_EMPTY = Reference.MOD_ID + ":empty10";
    public static final int TIMEOUT = 2000;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testWorldBlockImporterToInterfaceToExporterBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world block importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_BLOCK, new ItemStack(PartTypes.IMPORTER_WORLD_BLOCK.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place world block exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_WORLD_BLOCK, new ItemStack(PartTypes.EXPORTER_WORLD_BLOCK.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place block before importer
        helper.setBlock(POS.west(), Blocks.STONE);

        // Place empty variable in importer and exporter
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.BLOCK_BOOLEAN_IMPORT, variableAspect);
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.World.BLOCK_BOOLEAN_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if items are moved
            helper.assertBlockNotPresent(Blocks.STONE, POS.west());
            helper.assertContainerEmpty(POS.east().east());
            helper.assertBlockPresent(Blocks.COBBLESTONE, POS.east().north());

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), Component.literal("Importer is deactivated"));
            helper.assertValueEqual(
                    PartTypes.IMPORTER_WORLD_BLOCK.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    Component.literal("Block status is incorrect")
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.World.BLOCK_BOOLEAN_IMPORT, Component.literal("Active aspect is incorrect"));
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.World.BLOCK_BOOLEAN_IMPORT).isEmpty(), Component.literal("Active aspect has errors"));

            // Check exporter state
            IPartStateWriter partStateWriter2 = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH)).getState();
            helper.assertFalse(partStateWriter2.isDeactivated(), Component.literal("Importer is deactivated"));
            helper.assertValueEqual(
                    PartTypes.EXPORTER_WORLD_BLOCK.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH)), Direction.NORTH).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    Component.literal("Block status is incorrect")
            );
            helper.assertValueEqual(partStateWriter2.getActiveAspect(), TunnelAspects.Write.World.BLOCK_BOOLEAN_EXPORT, Component.literal("Active aspect is incorrect"));
            helper.assertTrue(partStateWriter2.getErrors(TunnelAspects.Write.World.BLOCK_BOOLEAN_EXPORT).isEmpty(), Component.literal("Active aspect has errors"));
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testWorldBlockImporterToInterfaceToExporterBlockCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world block importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_BLOCK, new ItemStack(PartTypes.IMPORTER_WORLD_BLOCK.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place world block exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_WORLD_BLOCK, new ItemStack(PartTypes.EXPORTER_WORLD_BLOCK.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place block before importer
        helper.setBlock(POS.west(), Blocks.STONE);

        // Place empty variable in importer and exporter
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.BLOCK_ITEMSTACK_IMPORT, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Blocks.COBBLESTONE))));
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.World.BLOCK_BOOLEAN_EXPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            // Check if items are moved
            helper.assertBlockNotPresent(Blocks.STONE, POS.west());
            helper.assertContainerEmpty(POS.east().east());
            helper.assertBlockPresent(Blocks.COBBLESTONE, POS.east().north());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testWorldBlockImporterToInterfaceToExporterBlockIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world block importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_BLOCK, new ItemStack(PartTypes.IMPORTER_WORLD_BLOCK.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place world block exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_WORLD_BLOCK, new ItemStack(PartTypes.EXPORTER_WORLD_BLOCK.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place block before importer
        helper.setBlock(POS.west(), Blocks.STONE);

        // Place empty variable in importer and exporter
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.BLOCK_ITEMSTACK_IMPORT, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Blocks.STONE))));
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.World.BLOCK_BOOLEAN_EXPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            // Check if items are not moved
            helper.assertBlockPresent(Blocks.STONE, POS.west());
            helper.assertContainerEmpty(POS.east().east());
            helper.assertBlockNotPresent(Blocks.COBBLESTONE, POS.east().north());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testWorldBlockImporterToInterfaceToExporterShulkerBox(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world block importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_BLOCK, new ItemStack(PartTypes.IMPORTER_WORLD_BLOCK.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place world block exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_WORLD_BLOCK, new ItemStack(PartTypes.EXPORTER_WORLD_BLOCK.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place shulker box before importer
        helper.setBlock(POS.west(), Blocks.SHULKER_BOX);
        ShulkerBoxBlockEntity shulkerBoxStart = helper.getBlockEntity(POS.west(), ShulkerBoxBlockEntity.class);
        shulkerBoxStart.setItem(0, new ItemStack(Items.APPLE));
        shulkerBoxStart.setItem(1, new ItemStack(Items.DIRT));

        // Place empty variable in importer and exporter
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.BLOCK_BOOLEAN_IMPORT, variableAspect);
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.World.BLOCK_BOOLEAN_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if items are moved
            helper.assertBlockNotPresent(Blocks.STONE, POS.west());
            helper.assertContainerEmpty(POS.east().east());
            helper.assertBlockPresent(Blocks.SHULKER_BOX, POS.east().north());
            helper.assertContainerContains(POS.east().north(), Items.APPLE);
            helper.assertContainerContains(POS.east().north(), Items.DIRT);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT, environment = "integratedtunnels:blacklist_oak_log")
    public void testWorldBlockImporterBlacklistOakLog(GameTestHelper helper) {
        // Test that oak logs (blacklisted) cannot be imported

        // Temporarily modify config for this test
        java.util.List<String> originalBlacklist = org.cyclops.integratedtunnels.GeneralConfig.blockImporterBlacklist;
        org.cyclops.integratedtunnels.GeneralConfig.blockImporterBlacklist = Lists.newArrayList("minecraft:oak_log");

        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world block importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_BLOCK, new ItemStack(PartTypes.IMPORTER_WORLD_BLOCK.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place oak log before importer (should be blacklisted)
        helper.setBlock(POS.west(), Blocks.OAK_LOG);

        // Place empty variable in importer
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.BLOCK_BOOLEAN_IMPORT, variableAspect);

        helper.runAfterDelay(200, () -> {
            try {
                // Check that oak log is still present (was not imported)
                helper.assertBlockPresent(Blocks.OAK_LOG, POS.west());
                // Check that chest is empty (no items imported)
                helper.assertContainerEmpty(POS.east().east());
                helper.succeed();
            } finally {
                // Restore original config
                org.cyclops.integratedtunnels.GeneralConfig.blockImporterBlacklist = originalBlacklist;
            }
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT, environment = "integratedtunnels:blacklist_regex")
    public void testWorldBlockImporterBlacklistRegex(GameTestHelper helper) {
        // Test that regex patterns work for blacklisting

        // Temporarily modify config for this test - blacklist all blocks with "oak" in them
        java.util.List<String> originalBlacklist = org.cyclops.integratedtunnels.GeneralConfig.blockImporterBlacklist;
        org.cyclops.integratedtunnels.GeneralConfig.blockImporterBlacklist = Lists.newArrayList("minecraft:.*oak.*");

        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world block importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_BLOCK, new ItemStack(PartTypes.IMPORTER_WORLD_BLOCK.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place oak planks before importer (should match regex pattern)
        helper.setBlock(POS.west(), Blocks.OAK_PLANKS);

        // Place empty variable in importer
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.BLOCK_BOOLEAN_IMPORT, variableAspect);

        helper.runAfterDelay(200, () -> {
            try {
                // Check that oak planks is still present (was not imported)
                helper.assertBlockPresent(Blocks.OAK_PLANKS, POS.west());
                // Check that chest is empty (no items imported)
                helper.assertContainerEmpty(POS.east().east());
                helper.succeed();
            } finally {
                // Restore original config
                org.cyclops.integratedtunnels.GeneralConfig.blockImporterBlacklist = originalBlacklist;
            }
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT, environment = "integratedtunnels:non_blacklisted")
    public void testWorldBlockImporterNonBlacklistedBlock(GameTestHelper helper) {
        // Test that non-blacklisted blocks can still be imported normally

        // Temporarily modify config for this test - only blacklist oak log
        java.util.List<String> originalBlacklist = org.cyclops.integratedtunnels.GeneralConfig.blockImporterBlacklist;
        org.cyclops.integratedtunnels.GeneralConfig.blockImporterBlacklist = java.util.List.of("minecraft:oak_log");

        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world block importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_BLOCK, new ItemStack(PartTypes.IMPORTER_WORLD_BLOCK.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place stone before importer (should NOT be blacklisted)
        helper.setBlock(POS.west(), Blocks.STONE);

        // Place empty variable in importer
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper, helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.BLOCK_BOOLEAN_IMPORT, variableAspect);

        helper.runAfterDelay(200, () -> {
            try {
                // Check that stone was imported (block is gone)
                helper.assertBlockNotPresent(Blocks.STONE, POS.west());
                // Check that chest has cobblestone (the drop from stone)
                helper.assertContainerContains(POS.east().east(), Items.COBBLESTONE);
                helper.succeed();
            } finally {
                // Restore original config
                org.cyclops.integratedtunnels.GeneralConfig.blockImporterBlacklist = originalBlacklist;
            }
        });
    }

}
