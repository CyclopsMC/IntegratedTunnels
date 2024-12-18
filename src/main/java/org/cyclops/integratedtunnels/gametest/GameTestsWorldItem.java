package org.cyclops.integratedtunnels.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsWorldItem {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 2000;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testWorldItemImporterToInterfaceToExporterBoolean(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_ITEM, new ItemStack(PartTypes.IMPORTER_WORLD_ITEM.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place world item exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_WORLD_ITEM, new ItemStack(PartTypes.EXPORTER_WORLD_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place block item before importer
        helper.spawnItem(Blocks.STONE.asItem(), POS.west());

        // Place empty variable in importer and exporter
        ItemStack variableAspect = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.ENTITYITEM_BOOLEAN_IMPORT, variableAspect);
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.World.ENTITYITEM_BOOLEAN_EXPORT, variableAspect);

        helper.succeedWhen(() -> {
            // Check if items are moved
            helper.assertItemEntityNotPresent(Blocks.STONE.asItem(), POS.west(), 0.25);
            helper.assertContainerEmpty(POS.west());
            helper.assertItemEntityPresent(Blocks.STONE.asItem(), POS.east().north(), 0.25);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testWorldItemImporterToInterfaceToExporterBlockCorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_ITEM, new ItemStack(PartTypes.IMPORTER_WORLD_ITEM.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place world item exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_WORLD_ITEM, new ItemStack(PartTypes.EXPORTER_WORLD_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place block item before importer
        helper.spawnItem(Blocks.STONE.asItem(), POS.west());

        // Place empty variable in importer and exporter
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.ENTITYITEM_ITEMSTACK_IMPORT, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Blocks.STONE))));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.World.ENTITYITEM_BOOLEAN_EXPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            // Check if items are moved
            helper.assertItemEntityNotPresent(Blocks.STONE.asItem(), POS.west(), 0.25);
            helper.assertContainerEmpty(POS.west());
            helper.assertItemEntityPresent(Blocks.STONE.asItem(), POS.east().north(), 0.25);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testWorldItemImporterToInterfaceToExporterBlockIncorrect(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place world item importer
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.IMPORTER_WORLD_ITEM, new ItemStack(PartTypes.IMPORTER_WORLD_ITEM.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place world item exporter
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.EXPORTER_WORLD_ITEM, new ItemStack(PartTypes.EXPORTER_WORLD_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place block item before importer
        helper.spawnItem(Blocks.STONE.asItem(), POS.west());

        // Place empty variable in importer and exporter
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.World.ENTITYITEM_ITEMSTACK_IMPORT, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Blocks.COBBLESTONE))));
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), TunnelAspects.Write.World.ENTITYITEM_BOOLEAN_EXPORT, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            // Check if items are not moved
            helper.assertItemEntityPresent(Blocks.STONE.asItem(), POS.west(), 0.25);
            helper.assertContainerEmpty(POS.west());
            helper.assertItemEntityNotPresent(Blocks.STONE.asItem(), POS.east().north(), 0.25);
        });
    }

}
