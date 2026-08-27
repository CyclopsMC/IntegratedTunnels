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
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.cyclopscore.datastructure.DimPos;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.network.INetwork;
import org.cyclops.integrateddynamics.api.network.IPartNetwork;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.block.IgnoredBlockStatus;
import org.cyclops.integrateddynamics.core.evaluate.operator.CurriedOperator;
import org.cyclops.integrateddynamics.core.evaluate.operator.Operators;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.part.PartTypeInterfaceFilteringItem;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableFromReader;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;
import static org.cyclops.integratedtunnels.gametest.GameTestHelpersIntegratedTunnels.setPassiveInteraction;
import static org.cyclops.integratedtunnels.gametest.GameTestHelpersIntegratedTunnels.setTargetSide;
import static org.cyclops.integratedtunnels.gametest.GameTestHelpersIntegratedTunnels.setTargetSideViaSettings;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsItems {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 2000;
    public static final int TICKS_PASSIVE_INTERACTION = 100;
    public static final int TICKS_NETWORK_INIT = 20;
    public static final int TICKS_TRANSFER = 100;
    public static final int SLOT_FURNACE_INPUT = 0;
    public static final int SLOT_FURNACE_FUEL = 1;
    public static final int SLOT_FURNACE_OUTPUT = 2;
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

    /**
     * Regression test for CyclopsMC/IntegratedDynamics#1711.
     *
     * A filtering interface caches its network and part network,
     * which are unset while the part is detached from its network,
     * such as in-between a chunk unload and the corresponding network element revalidation.
     * Updating the part in that window used to crash the server with an NPE on the unset part network,
     * which in turn marked the whole network as crashed, even after a server restart.
     */
    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsImporterToFilteredInterfaceDetachedFromNetwork(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place filtering item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_FILTERING_ITEM, new ItemStack(PartTypes.INTERFACE_FILTERING_ITEM.getItem()));

        // Place redstone reader, as source of the interface's filter variable
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.NORTH,
                org.cyclops.integrateddynamics.core.part.PartTypes.REDSTONE_READER,
                new ItemStack(org.cyclops.integrateddynamics.core.part.PartTypes.REDSTONE_READER.getItem()));

        // Place chests
        helper.setBlock(POS.west(), Blocks.CHEST);
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Insert items in importer chest
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));

        PartPos importerPos = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        PartPos interfacePos = PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST);
        PartPos readerPos = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.NORTH);

        // Place empty variable in importer
        placeVariableInWriter(helper.getLevel(), importerPos, TunnelAspects.Write.Item.BOOLEAN_IMPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        // Place an aspect variable in the filtering interface.
        // This must be an aspect variable rather than an empty or value variable,
        // as only its facade resolves itself through the part network.
        // The redstone reader is not powered, so this filter always evaluates to true.
        ItemStack variableFilter = createVariableFromReader(helper.getLevel(), readerPos, Aspects.Read.Redstone.BOOLEAN_LOW);
        placeVariableInWriter(helper.getLevel(), interfacePos, TunnelAspects.Write.ItemFilter.BOOLEAN_SET_FILTER, variableFilter);

        helper.runAfterDelay(TICKS_NETWORK_INIT + TICKS_TRANSFER, () -> {
            // The filter has been applied by now, so the first item has been imported through the interface
            helper.assertContainerContains(POS.east().east(), Items.WHITE_WOOL);

            PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(interfacePos);
            PartTypeInterfaceFilteringItem.State partStateInterface = (PartTypeInterfaceFilteringItem.State) partStateHolder.getState();
            INetwork network = NetworkHelpers.getNetworkChecked(interfacePos);
            IPartNetwork partNetwork = NetworkHelpers.getPartNetworkChecked(network);
            PartTarget target = PartTypes.INTERFACE_FILTERING_ITEM.getTarget(interfacePos, partStateInterface);

            // Detach the interface from its network, like onNetworkRemoval does on chunk unload,
            // and mark its filter aspect as requiring an update, like onVariableContentsUpdated does.
            PartTypes.INTERFACE_FILTERING_ITEM.removeTargetFromNetwork(network, partStateInterface);
            partStateInterface.requireAspectUpdate();

            // Updating the part while detached must not throw
            PartTypes.INTERFACE_FILTERING_ITEM.update(network, partNetwork, target, partStateInterface);

            // Re-attach the interface, like afterNetworkReAlive does on revalidation
            PartTypes.INTERFACE_FILTERING_ITEM.addTargetToNetwork(network, target,
                    partStateInterface.getPriority(), partStateInterface.getChannelInterface(), partStateInterface);

            // Insert another item, which must be imported through the re-attached interface
            ChestBlockEntity chestInAfter = helper.getBlockEntity(POS.west());
            chestInAfter.setItem(0, new ItemStack(Items.ACACIA_LEAVES));
        });

        helper.succeedWhen(() -> {
            // Check if items are moved
            helper.assertContainerContains(POS.east().east(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.east().east(), Items.ACACIA_LEAVES);
            helper.assertContainerEmpty(POS.west());

            // Check filtering interface state
            IPartStateWriter partStateInterface = (IPartStateWriter) PartHelpers.getPart(interfacePos).getState();
            helper.assertFalse(partStateInterface.isDeactivated(), "Filtering interface is deactivated");
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

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsInterfaceToExporterPredicateValidSingle(GameTestHelper helper) {
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
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.OPERATOR, ValueTypeOperator.ValueOperator.of(new CurriedOperator(
                Operators.RELATIONAL_EQUALS,
                new Variable<>(ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.WHITE_WOOL)))
        )));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.Item.PREDICATE_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if items are moved
            ChestBlockEntity chestOut = helper.getBlockEntity(POS.east().east());
            helper.assertTrue(chestIn.getItem(0).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestIn.getItem(1).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestIn.getItem(2).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestOut.getItem(0).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(1).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(2).isEmpty(), "Incorrect output item was moved");
            helper.assertContainerContains(POS.east().east(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.west(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.west(), Items.DIAMOND_PICKAXE);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsInterfaceToExporterPredicateInvalidMultiple(GameTestHelper helper) {
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
        chestIn.setItem(3, new ItemStack(Items.WHITE_WOOL));

        // Place empty variable in exporter
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.OPERATOR, ValueTypeOperator.ValueOperator.of(new CurriedOperator(
                Operators.RELATIONAL_EQUALS,
                new Variable<>(ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.WHITE_WOOL)))
        )));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.Item.PREDICATE_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if items are moved
            ChestBlockEntity chestOut = helper.getBlockEntity(POS.east().east());
            helper.assertFalse(chestIn.getItem(0).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestIn.getItem(1).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestIn.getItem(2).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestIn.getItem(3).isEmpty(), "Incorrect input item was moved");
            helper.assertTrue(chestOut.getItem(0).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(1).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(2).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(3).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestIn.getItem(0).getItem() == Items.WHITE_WOOL, "Incorrect output item type was moved");
            helper.assertContainerContains(POS.west(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.west(), Items.DIAMOND_PICKAXE);
            helper.assertTrue(chestIn.getItem(3).getItem() == Items.WHITE_WOOL, "Incorrect output item type was moved");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsInterfaceToExporterPredicateSlotBasedValid(GameTestHelper helper) {
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
        chestIn.setItem(3, new ItemStack(Items.WHITE_WOOL));

        // Place empty variable in exporter
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.OPERATOR, ValueTypeOperator.ValueOperator.of(new CurriedOperator(
                Operators.RELATIONAL_EQUALS,
                new Variable<>(ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.WHITE_WOOL)))
        )));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.Item.PREDICATE_EXPORT, variableAspect);

        // Enable slot-based
        PartPos posExporter = PartPos.of(DimPos.of(helper.getLevel(), helper.absolutePos(POS.east())), Direction.EAST);
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posExporter);
        IAspectProperties properties = TunnelAspects.Write.Item.PREDICATE_EXPORT.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posExporter), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.Item.PROP_PREDICATE_SLOTBASED, ValueTypeBoolean.ValueBoolean.of(true));
        partStateHolder.getState().setAspectProperties(TunnelAspects.Write.Item.PREDICATE_EXPORT, properties);

        helper.succeedWhen(() -> {
            // Check if items are moved
            ChestBlockEntity chestOut = helper.getBlockEntity(POS.east().east());
            helper.assertTrue(chestIn.getItem(0).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestIn.getItem(1).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestIn.getItem(2).isEmpty(), "Incorrect input item was moved");
            helper.assertTrue(chestIn.getItem(3).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestOut.getItem(0).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(1).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(2).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(0).getItem() == Items.WHITE_WOOL, "Incorrect output item type was moved");
            helper.assertTrue(chestOut.getItem(0).getCount() == 2, "Incorrect output item size was moved");
            helper.assertContainerContains(POS.west(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.west(), Items.DIAMOND_PICKAXE);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsInterfaceToExporterPredicateSlotBasedInvalid(GameTestHelper helper) {
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
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL, 2));
        chestIn.setItem(1, new ItemStack(Items.ACACIA_LEAVES));
        chestIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place empty variable in exporter
        ItemStack variableAspect = createVariableForValue(helper.getLevel(), ValueTypes.OPERATOR, ValueTypeOperator.ValueOperator.of(new CurriedOperator(
                Operators.RELATIONAL_EQUALS,
                new Variable<>(ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.WHITE_WOOL)))
        )));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST), TunnelAspects.Write.Item.PREDICATE_EXPORT, variableAspect);

        // Enable slot-based
        PartPos posExporter = PartPos.of(DimPos.of(helper.getLevel(), helper.absolutePos(POS.east())), Direction.EAST);
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posExporter);
        IAspectProperties properties = TunnelAspects.Write.Item.PREDICATE_EXPORT.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posExporter), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.Item.PROP_PREDICATE_SLOTBASED, ValueTypeBoolean.ValueBoolean.of(true));
        partStateHolder.getState().setAspectProperties(TunnelAspects.Write.Item.PREDICATE_EXPORT, properties);

        helper.succeedWhen(() -> {
            // Check if items are moved
            ChestBlockEntity chestOut = helper.getBlockEntity(POS.east().east());
            helper.assertFalse(chestIn.getItem(0).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestIn.getItem(1).isEmpty(), "Incorrect input item was moved");
            helper.assertFalse(chestIn.getItem(2).isEmpty(), "Incorrect input item was moved");
            helper.assertTrue(chestOut.getItem(0).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(1).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestOut.getItem(2).isEmpty(), "Incorrect output item was moved");
            helper.assertTrue(chestIn.getItem(0).getItem() == Items.WHITE_WOOL, "Incorrect output item type was moved");
            helper.assertTrue(chestIn.getItem(0).getCount() == 2, "Incorrect output item size was moved");
            helper.assertContainerContains(POS.west(), Items.ACACIA_LEAVES);
            helper.assertContainerContains(POS.west(), Items.DIAMOND_PICKAXE);
        });
    }

    /**
     * Setup an importer with a false boolean aspect, so that it never imports actively,
     * next to a hopper that tries to push items into it.
     * @return The position of the importer part.
     */
    protected static PartPos setupPassiveImporter(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));

        // Place item interface with a chest to store the network's items in
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place a hopper that pushes items into the importer
        helper.setBlock(POS.west(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.EAST));
        HopperBlockEntity hopperIn = helper.getBlockEntity(POS.west());
        hopperIn.setItem(0, new ItemStack(Items.WHITE_WOOL));

        // Make the importer never import anything by itself
        PartPos posImporter = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        placeVariableInWriter(helper.getLevel(), posImporter, TunnelAspects.Write.Item.BOOLEAN_IMPORT,
                createVariableForValue(helper.getLevel(), ValueTypes.BOOLEAN, ValueTypeBoolean.ValueBoolean.of(false)));

        return posImporter;
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsPassiveImporterIgnoreFilter(GameTestHelper helper) {
        PartPos posImporter = setupPassiveImporter(helper);
        setPassiveInteraction(posImporter, TunnelAspects.Write.Item.BOOLEAN_IMPORT, true, true);

        helper.succeedWhen(() -> {
            // Check if the hopper was able to push its items into the network
            helper.assertContainerEmpty(POS.west());
            helper.assertContainerContains(POS.east().east(), Items.WHITE_WOOL);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsPassiveImporterRespectFilter(GameTestHelper helper) {
        PartPos posImporter = setupPassiveImporter(helper);
        setPassiveInteraction(posImporter, TunnelAspects.Write.Item.BOOLEAN_IMPORT, true, false);

        helper.runAfterDelay(TICKS_PASSIVE_INTERACTION, () -> {
            // Check if the hopper was not able to push its items into the network
            helper.assertContainerContains(POS.west(), Items.WHITE_WOOL);
            helper.assertContainerEmpty(POS.east().east());
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsPassiveImporterDisabled(GameTestHelper helper) {
        PartPos posImporter = setupPassiveImporter(helper);
        setPassiveInteraction(posImporter, TunnelAspects.Write.Item.BOOLEAN_IMPORT, false, true);

        helper.runAfterDelay(TICKS_PASSIVE_INTERACTION, () -> {
            // Check if the hopper was not able to push its items into the network
            helper.assertContainerContains(POS.west(), Items.WHITE_WOOL);
            helper.assertContainerEmpty(POS.east().east());
            helper.succeed();
        });
    }

    /**
     * Setup an exporter with a false boolean aspect, so that it never exports actively,
     * below which a hopper tries to pull items out of it.
     * @return The position of the exporter part.
     */
    protected static PartPos setupPassiveExporter(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS.above(), RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east().above(), RegistryEntries.BLOCK_CABLE.value());

        // Place item interface with a chest holding the network's items
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.above()), Direction.WEST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));
        helper.setBlock(POS.west().above(), Blocks.CHEST);
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.west().above());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));

        // Place item exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east().above()), Direction.DOWN, PartTypes.EXPORTER_ITEM, new ItemStack(PartTypes.EXPORTER_ITEM.getItem()));

        // Place a hopper that pulls items out of the exporter
        helper.setBlock(POS.east(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.NORTH));

        // Make the exporter never export anything by itself
        PartPos posExporter = PartPos.of(helper.getLevel(), helper.absolutePos(POS.east().above()), Direction.DOWN);
        placeVariableInWriter(helper.getLevel(), posExporter, TunnelAspects.Write.Item.BOOLEAN_EXPORT,
                createVariableForValue(helper.getLevel(), ValueTypes.BOOLEAN, ValueTypeBoolean.ValueBoolean.of(false)));

        return posExporter;
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsPassiveExporterIgnoreFilter(GameTestHelper helper) {
        PartPos posExporter = setupPassiveExporter(helper);
        setPassiveInteraction(posExporter, TunnelAspects.Write.Item.BOOLEAN_EXPORT, true, true);

        helper.succeedWhen(() -> {
            // Check if the hopper was able to pull items out of the network
            helper.assertContainerContains(POS.east(), Items.WHITE_WOOL);
            helper.assertContainerEmpty(POS.west().above());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsPassiveExporterRespectFilter(GameTestHelper helper) {
        PartPos posExporter = setupPassiveExporter(helper);
        setPassiveInteraction(posExporter, TunnelAspects.Write.Item.BOOLEAN_EXPORT, true, false);

        helper.runAfterDelay(TICKS_PASSIVE_INTERACTION, () -> {
            // Check if the hopper was not able to pull items out of the network
            helper.assertContainerEmpty(POS.east());
            helper.assertContainerContains(POS.west().above(), Items.WHITE_WOOL);
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsPassiveExporterDisabled(GameTestHelper helper) {
        PartPos posExporter = setupPassiveExporter(helper);
        setPassiveInteraction(posExporter, TunnelAspects.Write.Item.BOOLEAN_EXPORT, false, true);

        helper.runAfterDelay(TICKS_PASSIVE_INTERACTION, () -> {
            // Check if the hopper was not able to pull items out of the network
            helper.assertContainerEmpty(POS.east());
            helper.assertContainerContains(POS.west().above(), Items.WHITE_WOOL);
            helper.succeed();
        });
    }

    /**
     * Set up an item interface that targets a furnace from above,
     * with an item in the furnace's input slot (only reachable from above)
     * and an item in the furnace's output slot (only reachable from below).
     *
     * The exporter that empties the network into a chest is not placed yet,
     * so that the target side of the interface can first be changed
     * while the interface is already part of a live network.
     *
     * @return The position of the interface part.
     */
    protected static PartPos setupInterfaceTargetSide(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS.above(), RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east().above(), RegistryEntries.BLOCK_CABLE.value());

        // Place furnace below the cable, and fill its input and output slot
        helper.setBlock(POS, Blocks.FURNACE);
        FurnaceBlockEntity furnace = helper.getBlockEntity(POS);
        furnace.setItem(SLOT_FURNACE_INPUT, new ItemStack(Items.WHITE_WOOL));
        furnace.setItem(SLOT_FURNACE_OUTPUT, new ItemStack(Items.DIAMOND));

        // Place item interface, targeting the furnace from above
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.above()), Direction.DOWN, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place output chest for the exporter that is placed later
        helper.setBlock(POS.east().east().above(), Blocks.CHEST);

        return PartPos.of(helper.getLevel(), helper.absolutePos(POS.above()), Direction.DOWN);
    }

    /**
     * Place an item exporter that exports everything inside the network into a chest.
     */
    protected static void placeInterfaceTargetSideExporter(GameTestHelper helper) {
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east().above()), Direction.EAST, PartTypes.EXPORTER_ITEM, new ItemStack(PartTypes.EXPORTER_ITEM.getItem()));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east().above()), Direction.EAST),
                TunnelAspects.Write.Item.BOOLEAN_EXPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));
    }

    /**
     * Check that only the furnace's output slot was exposed to the network.
     */
    protected static void assertInterfaceTargetSideExported(GameTestHelper helper) {
        FurnaceBlockEntity furnace = helper.getBlockEntity(POS);
        helper.assertTrue(furnace.getItem(SLOT_FURNACE_INPUT).is(Items.WHITE_WOOL), "Furnace input slot was exposed to the network");
        helper.assertTrue(furnace.getItem(SLOT_FURNACE_OUTPUT).isEmpty(), "Furnace output slot was not exposed to the network");
        helper.assertContainerContains(POS.east().east().above(), Items.DIAMOND);
    }

    /**
     * An interface must only expose the side of the target block that it is configured with,
     * even if that side is changed after the interface has been added to the network.
     */
    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsInterfaceTargetSideExport(GameTestHelper helper) {
        PartPos posInterface = setupInterfaceTargetSide(helper);

        helper.startSequence()
                // Let the interface add itself to the network with its default target side
                .thenIdle(TICKS_NETWORK_INIT)
                .thenExecute(() -> setTargetSide(posInterface, Direction.DOWN))
                // Let the network index catch up with the new target side
                .thenIdle(TICKS_NETWORK_INIT)
                .thenExecute(() -> placeInterfaceTargetSideExporter(helper))
                .thenIdle(TICKS_TRANSFER)
                .thenExecute(() -> assertInterfaceTargetSideExported(helper))
                .thenSucceed();
    }

    /**
     * Just like {@link #testItemsInterfaceTargetSideExport(GameTestHelper)},
     * but with the target side being changed in the same way as the part settings gui does.
     */
    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsInterfaceTargetSideExportViaSettings(GameTestHelper helper) {
        PartPos posInterface = setupInterfaceTargetSide(helper);

        helper.startSequence()
                // Let the interface add itself to the network with its default target side
                .thenIdle(TICKS_NETWORK_INIT)
                .thenExecute(() -> setTargetSideViaSettings(posInterface, Direction.DOWN))
                // Let the network index catch up with the new target side
                .thenIdle(TICKS_NETWORK_INIT)
                .thenExecute(() -> placeInterfaceTargetSideExporter(helper))
                .thenIdle(TICKS_TRANSFER)
                .thenExecute(() -> assertInterfaceTargetSideExported(helper))
                .thenSucceed();
    }

    /**
     * Insertions into an interface must also only apply to the configured target side,
     * so coal must end up in the furnace's fuel slot instead of its input slot.
     */
    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testItemsInterfaceTargetSideImport(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS.above(), RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east().above(), RegistryEntries.BLOCK_CABLE.value());

        // Place empty furnace below the cable
        helper.setBlock(POS, Blocks.FURNACE);

        // Place item interface, targeting the furnace from above
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.above()), Direction.DOWN, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));
        PartPos posInterface = PartPos.of(helper.getLevel(), helper.absolutePos(POS.above()), Direction.DOWN);

        // Place input chest with coal for the importer that is placed later
        helper.setBlock(POS.east().east().above(), Blocks.CHEST);
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.east().east().above());
        chestIn.setItem(0, new ItemStack(Items.COAL));

        helper.startSequence()
                // Let the interface add itself to the network with its default target side
                .thenIdle(TICKS_NETWORK_INIT)
                .thenExecute(() -> setTargetSide(posInterface, Direction.DOWN))
                .thenExecute(() -> {
                    // Place item importer that imports everything from the chest into the network
                    PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east().above()), Direction.EAST, PartTypes.IMPORTER_ITEM, new ItemStack(PartTypes.IMPORTER_ITEM.getItem()));
                    placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east().above()), Direction.EAST),
                            TunnelAspects.Write.Item.BOOLEAN_IMPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));
                })
                .thenIdle(TICKS_TRANSFER)
                .thenExecute(() -> {
                    FurnaceBlockEntity furnace = helper.getBlockEntity(POS);
                    helper.assertTrue(furnace.getItem(SLOT_FURNACE_INPUT).isEmpty(), "Furnace input slot was exposed to the network");
                    helper.assertTrue(furnace.getItem(SLOT_FURNACE_FUEL).is(Items.COAL), "Furnace fuel slot was not exposed to the network");
                    helper.assertContainerEmpty(POS.east().east().above());
                })
                .thenSucceed();
    }

}
