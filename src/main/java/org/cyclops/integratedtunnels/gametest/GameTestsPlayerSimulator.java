package org.cyclops.integratedtunnels.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.property.IAspectProperties;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.core.block.IgnoredBlockStatus;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
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
public class GameTestsPlayerSimulator {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 2000;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 1, 2);

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorMilkCow(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place player simulator
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.PLAYER_SIMULATOR, new ItemStack(PartTypes.PLAYER_SIMULATOR.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place cow before player simulator
        helper.spawnWithNoFreeWill(EntityType.COW, POS.west());
        helper.setBlock(POS.west().below(), Blocks.STONE);

        // Insert some items into interface
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.east().east());
        chestIn.setItem(0, new ItemStack(Items.WHITE_WOOL));
        chestIn.setItem(1, new ItemStack(Items.BUCKET));
        chestIn.setItem(2, new ItemStack(Items.DIAMOND_PICKAXE));

        // Place bucket variable in click item
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.BUCKET))));

        helper.succeedWhen(() -> {
            // Check bucket is filled
            helper.assertContainerContains(POS.east().east(), Items.WHITE_WOOL);
            helper.assertContainerContains(POS.east().east(), Items.MILK_BUCKET);
            helper.assertContainerContains(POS.east().east(), Items.DIAMOND_PICKAXE);
            helper.assertTrue(chestIn.getItem(3).isEmpty(), "Chest contains too many items");

            // Check cow still exists
            helper.assertEntityPresent(EntityType.COW);

            // Check importer state
            IPartStateWriter partStateWriter = (IPartStateWriter) PartHelpers.getPart(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)).getState();
            helper.assertFalse(partStateWriter.isDeactivated(), "Importer is deactivated");
            helper.assertValueEqual(
                    PartTypes.PLAYER_SIMULATOR.getBlockState(PartHelpers.getPartContainerChecked(PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST)), Direction.WEST).getValue(IgnoredBlockStatus.STATUS),
                    IgnoredBlockStatus.Status.ACTIVE,
                    "Block status is incorrect"
            );
            helper.assertValueEqual(partStateWriter.getActiveAspect(), TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, "Active aspect is incorrect");
            helper.assertTrue(partStateWriter.getErrors(TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK).isEmpty(), "Active aspect has errors");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorKillCow(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place player simulator
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.PLAYER_SIMULATOR, new ItemStack(PartTypes.PLAYER_SIMULATOR.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place cow before player simulator
        helper.spawnWithNoFreeWill(EntityType.COW, POS.west());
        helper.setBlock(POS.west().below(), Blocks.STONE);

        // Build prison around cow
        helper.setBlock(POS.west().north(), Blocks.ACACIA_FENCE);
        helper.setBlock(POS.west().south(), Blocks.ACACIA_FENCE);
        helper.setBlock(POS.west().west().north(), Blocks.ACACIA_FENCE);
        helper.setBlock(POS.west().west(), Blocks.ACACIA_FENCE);
        helper.setBlock(POS.west().west().south(), Blocks.ACACIA_FENCE);

        // Insert some items into interface
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.east().east());
        chestIn.setItem(0, new ItemStack(Items.DIAMOND_SWORD));

        // Enable click any item aspect
        PartPos posPlayerSimulator = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        placeVariableInWriter(helper.getLevel(), posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_BOOLEAN, createVariableForValue(helper.getLevel(), ValueTypes.BOOLEAN, ValueTypeBoolean.ValueBoolean.of(true)));

        // Set aspect to left-click
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posPlayerSimulator);
        IAspectProperties properties = TunnelAspects.Write.Player.CLICK_ITEM_BOOLEAN.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posPlayerSimulator), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.Player.PROP_RIGHT_CLICK, ValueTypeBoolean.ValueBoolean.of(false));
        partStateHolder.getState().setAspectProperties(TunnelAspects.Write.Player.CLICK_ITEM_BOOLEAN, properties);

        helper.succeedWhen(() -> {
            // Check sword still exists
            helper.assertContainerContains(POS.east().east(), Items.DIAMOND_SWORD);

            // Check cow is dead
            helper.assertEntityNotPresent(EntityType.COW);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorFlipLever(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place player simulator
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.PLAYER_SIMULATOR, new ItemStack(PartTypes.PLAYER_SIMULATOR.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place lever before player simulator
        helper.setBlock(POS.west(), Blocks.LEVER);

        // Enable empty click aspect
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Player.CLICK_EMPTY_BOOLEAN, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            // Check lever is flipped
            helper.assertBlockProperty(POS.west(), LeverBlock.POWERED, true);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorPlaceWaterBucket(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place player simulator
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.PLAYER_SIMULATOR, new ItemStack(PartTypes.PLAYER_SIMULATOR.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place block a bit before player simulator to right-click bucket against
        helper.setBlock(POS.west().west(), Blocks.STONE);
        helper.setBlock(POS.west().north(), Blocks.STONE);
        helper.setBlock(POS.west().south(), Blocks.STONE);

        // Insert some items into interface
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.east().east());
        chestIn.setItem(0, new ItemStack(Items.WATER_BUCKET));

        // Enable item click aspect
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Player.CLICK_ITEM_BOOLEAN, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            // Check bucket is drained
            helper.assertContainerContains(POS.east().east(), Items.BUCKET);

            // Check water is placed
            helper.assertBlockPresent(Blocks.WATER, POS.west());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorBreakBlockPickaxe(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place player simulator
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.PLAYER_SIMULATOR, new ItemStack(PartTypes.PLAYER_SIMULATOR.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.west(), Blocks.STONE);
        helper.setBlock(POS.below().west(), Blocks.STONE); // So the item does not fall outside of the search area

        // Place stone before player simulator
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Insert some items into interface
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.east().east());
        chestIn.setItem(0, new ItemStack(Items.DIAMOND_PICKAXE));

        // Enable click any item aspect
        PartPos posPlayerSimulator = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        placeVariableInWriter(helper.getLevel(), posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_BOOLEAN, createVariableForValue(helper.getLevel(), ValueTypes.BOOLEAN, ValueTypeBoolean.ValueBoolean.of(true)));

        // Set aspect to left-click
        PartHelpers.PartStateHolder partStateHolder = PartHelpers.getPart(posPlayerSimulator);
        IAspectProperties properties = TunnelAspects.Write.Player.CLICK_ITEM_BOOLEAN.getProperties(partStateHolder.getPart(), PartTarget.fromCenter(posPlayerSimulator), partStateHolder.getState());
        properties.setValue(TunnelAspectWriteBuilders.Player.PROP_RIGHT_CLICK, ValueTypeBoolean.ValueBoolean.of(false));
        partStateHolder.getState().setAspectProperties(TunnelAspects.Write.Player.CLICK_ITEM_BOOLEAN, properties);

        helper.succeedWhen(() -> {
            // Check pickaxe still exists
            helper.assertContainerContains(POS.east().east(), Items.DIAMOND_PICKAXE);

            // Check cobblestone was dropped
            helper.assertItemEntityPresent(Items.COBBLESTONE);
            helper.assertBlockNotPresent(Blocks.STONE, POS.west());
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorPlaceBlock(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place player simulator
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.PLAYER_SIMULATOR, new ItemStack(PartTypes.PLAYER_SIMULATOR.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place block a bit before player simulator to right-click bucket against
        helper.setBlock(POS.west().west().west(), Blocks.STONE);

        // Insert some items into interface
        ChestBlockEntity chestIn = helper.getBlockEntity(POS.east().east());
        chestIn.setItem(0, new ItemStack(Items.DIRT));

        // Enable item click aspect
        placeVariableInWriter(helper.getLevel(), PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST), TunnelAspects.Write.Player.CLICK_ITEM_BOOLEAN, new ItemStack(RegistryEntries.ITEM_VARIABLE));

        helper.succeedWhen(() -> {
            // Check inventory is empty
            helper.assertContainerEmpty(POS.east().east());

            // Check block is placed
            helper.assertBlockPresent(Blocks.DIRT, POS.west().west());
        });
    }

}
