package org.cyclops.integratedtunnels.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.portal.DimensionTransition;
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
import org.cyclops.integratedtunnels.core.ExtendedFakePlayer;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspectWriteBuilders;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.placeVariableInWriter;
import static org.cyclops.integratedtunnels.gametest.GameTestHelpersIntegratedTunnels.setNetworkInventory;
import static org.cyclops.integratedtunnels.gametest.GameTestHelpersIntegratedTunnels.setPriority;
import static org.cyclops.integratedtunnels.gametest.GameTestHelpersIntegratedTunnels.setRightClickDuration;

@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsPlayerSimulator {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 2000;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 1, 2);

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorFakePlayerChangeDimensionNoOp(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerLevel endLevel = level.getServer().getLevel(Level.END);
        helper.assertTrue(endLevel != null, "The End level is not available");

        ExtendedFakePlayer fakePlayer = new ExtendedFakePlayer(level);
        Entity returnedEntity = fakePlayer.changeDimension(new DimensionTransition(endLevel, fakePlayer, DimensionTransition.DO_NOTHING));

        helper.assertValueEqual(returnedEntity, fakePlayer, "Fake player changeDimension return value is incorrect");
        helper.assertValueEqual(fakePlayer.level(), level, "Fake player changed dimensions unexpectedly");
        helper.succeed();
    }

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
        helper.spawn(EntityType.COW, POS.west());

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

    /**
     * Prepare a player simulator that is connected to a chest via an item interface,
     * with a stone wall in front of it to catch shot projectiles.
     * @param helper The game test helper.
     * @return The chest that is exposed to the network.
     */
    protected static ChestBlockEntity prepareProjectileWeaponNetwork(GameTestHelper helper) {
        // Place cable
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());

        // Place player simulator
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS), Direction.WEST, PartTypes.PLAYER_SIMULATOR, new ItemStack(PartTypes.PLAYER_SIMULATOR.getItem()));

        // Place item interface
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.EAST, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));

        // Place chest for interface
        helper.setBlock(POS.east().east(), Blocks.CHEST);

        // Place block before the player simulator, so shot projectiles stay inside the test area
        helper.setBlock(POS.west().west(), Blocks.STONE);

        return helper.getBlockEntity(POS.east().east());
    }

    protected static int countItems(Container container, Item item) {
        int count = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack itemStack = container.getItem(slot);
            if (itemStack.is(item)) {
                count += itemStack.getCount();
            }
        }
        return count;
    }

    protected static ItemStack findItem(Container container, Item item) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack itemStack = container.getItem(slot);
            if (itemStack.is(item)) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorShootBowNetworkInventory(GameTestHelper helper) {
        ChestBlockEntity chestIn = prepareProjectileWeaponNetwork(helper);

        // Insert a bow and arrows into interface
        chestIn.setItem(0, new ItemStack(Items.BOW));
        chestIn.setItem(1, new ItemStack(Items.ARROW, 64));

        // Click with the bow, and allow the simulated player to use the network as inventory
        PartPos posPlayerSimulator = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        placeVariableInWriter(helper.getLevel(), posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.BOW))));
        setNetworkInventory(posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, true);

        helper.succeedWhen(() -> {
            // Check that exactly one arrow was taken from the network
            helper.assertValueEqual(countItems(chestIn, Items.ARROW), 63, "Arrow count");

            // Check that an arrow was shot
            helper.assertEntityPresent(EntityType.ARROW);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorShootBowNoNetworkInventory(GameTestHelper helper) {
        ChestBlockEntity chestIn = prepareProjectileWeaponNetwork(helper);

        // Insert a bow and arrows into interface
        chestIn.setItem(0, new ItemStack(Items.BOW));
        chestIn.setItem(1, new ItemStack(Items.ARROW, 64));

        // Click with the bow, without giving the simulated player access to the network as inventory
        PartPos posPlayerSimulator = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        placeVariableInWriter(helper.getLevel(), posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.BOW))));

        helper.runAfterDelay(200, () -> {
            // Check that no arrow was taken from the network
            helper.assertValueEqual(countItems(chestIn, Items.ARROW), 64, "Arrow count");

            // Check that no arrow was shot
            helper.assertEntityNotPresent(EntityType.ARROW);

            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorChargeCrossbowNetworkInventory(GameTestHelper helper) {
        ChestBlockEntity chestIn = prepareProjectileWeaponNetwork(helper);

        // Insert a crossbow and arrows into interface
        chestIn.setItem(0, new ItemStack(Items.CROSSBOW));
        chestIn.setItem(1, new ItemStack(Items.ARROW, 64));

        // Click with the crossbow, and allow the simulated player to use the network as inventory
        PartPos posPlayerSimulator = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        placeVariableInWriter(helper.getLevel(), posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.CROSSBOW))));
        setNetworkInventory(posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, true);

        // A crossbow only loads an arrow after being charged for 25 ticks within a single click
        PartHelpers.getPart(posPlayerSimulator).getState().setUpdateInterval(26);

        helper.succeedWhen(() -> {
            // Check that exactly one arrow was taken from the network
            helper.assertValueEqual(countItems(chestIn, Items.ARROW), 63, "Arrow count");

            // Check that the crossbow was loaded with an arrow
            ChargedProjectiles chargedProjectiles = findItem(chestIn, Items.CROSSBOW).get(DataComponents.CHARGED_PROJECTILES);
            helper.assertTrue(chargedProjectiles != null && chargedProjectiles.contains(Items.ARROW), "The crossbow was not loaded with an arrow");
        });
    }

    /**
     * More different items than fit in a player inventory,
     * so that items behind them can only be reached if the whole network is available to the player.
     */
    public static final Item[] MANY_ITEMS = new Item[]{
            Items.DIRT, Items.STONE, Items.COBBLESTONE, Items.GRANITE, Items.DIORITE,
            Items.ANDESITE, Items.SAND, Items.GRAVEL, Items.OAK_LOG, Items.BIRCH_LOG,
            Items.SPRUCE_LOG, Items.JUNGLE_LOG, Items.ACACIA_LOG, Items.OAK_PLANKS, Items.BIRCH_PLANKS,
            Items.SPRUCE_PLANKS, Items.JUNGLE_PLANKS, Items.ACACIA_PLANKS, Items.GLASS, Items.BRICK,
            Items.CLAY_BALL, Items.COAL, Items.CHARCOAL, Items.IRON_INGOT, Items.GOLD_INGOT,
            Items.COPPER_INGOT, Items.REDSTONE, Items.LAPIS_LAZULI, Items.QUARTZ, Items.EMERALD,
            Items.DIAMOND, Items.WHEAT, Items.CARROT, Items.POTATO, Items.APPLE,
            Items.BONE, Items.STRING,
    };

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorShootBowNetworkInventoryManyItems(GameTestHelper helper) {
        ChestBlockEntity chestIn = prepareProjectileWeaponNetwork(helper);

        // Place a second item interface with a chest, which is filled first due to its higher priority
        PartHelpers.addPart(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH, PartTypes.INTERFACE_ITEM, new ItemStack(PartTypes.INTERFACE_ITEM.getItem()));
        helper.setBlock(POS.east().north(), Blocks.CHEST);
        setPriority(PartPos.of(helper.getLevel(), helper.absolutePos(POS.east()), Direction.NORTH), 1);
        ChestBlockEntity chestFiller = helper.getBlockEntity(POS.east().north());

        // Fill the network with more different items than fit in a player inventory
        for (int i = 0; i < MANY_ITEMS.length; i++) {
            if (i < chestFiller.getContainerSize()) {
                chestFiller.setItem(i, new ItemStack(MANY_ITEMS[i]));
            } else {
                chestIn.setItem(i - chestFiller.getContainerSize(), new ItemStack(MANY_ITEMS[i]));
            }
        }

        // Insert a bow and arrows into interface, behind all other items
        chestIn.setItem(chestIn.getContainerSize() - 2, new ItemStack(Items.BOW));
        chestIn.setItem(chestIn.getContainerSize() - 1, new ItemStack(Items.ARROW, 64));

        // Click with the bow, and allow the simulated player to use the network as inventory
        PartPos posPlayerSimulator = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        placeVariableInWriter(helper.getLevel(), posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.BOW))));
        setNetworkInventory(posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, true);

        helper.succeedWhen(() -> {
            // Check that exactly one arrow was taken from the network
            helper.assertValueEqual(countItems(chestIn, Items.ARROW) + countItems(chestFiller, Items.ARROW), 63, "Arrow count");

            // Check that an arrow was shot
            helper.assertEntityPresent(EntityType.ARROW);
        });
    }

    /**
     * The number of ticks to wait before starting to click.
     * The first click of a simulated player holds right click for all the time that passed before it,
     * so this makes sure that a shorter configured duration is what is actually being tested.
     */
    public static final int DELAY_CLICK = 40;

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorShootBowRightClickDurationTooShort(GameTestHelper helper) {
        ChestBlockEntity chestIn = prepareProjectileWeaponNetwork(helper);

        // Insert a bow and arrows into interface
        chestIn.setItem(0, new ItemStack(Items.BOW));
        chestIn.setItem(1, new ItemStack(Items.ARROW, 64));

        // Click with the bow, while holding right click too short to draw it
        PartPos posPlayerSimulator = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        helper.runAfterDelay(DELAY_CLICK, () -> {
            placeVariableInWriter(helper.getLevel(), posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.BOW))));
            setNetworkInventory(posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, true);
            setRightClickDuration(posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, 2);
        });

        helper.runAfterDelay(DELAY_CLICK + 200, () -> {
            // Check that no arrow was taken from the network
            helper.assertValueEqual(countItems(chestIn, Items.ARROW), 64, "Arrow count");

            // Check that no arrow was shot
            helper.assertEntityNotPresent(EntityType.ARROW);

            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testPlayerSimulatorChargeCrossbowRightClickDuration(GameTestHelper helper) {
        ChestBlockEntity chestIn = prepareProjectileWeaponNetwork(helper);

        // Insert a crossbow and arrows into interface
        chestIn.setItem(0, new ItemStack(Items.CROSSBOW));
        chestIn.setItem(1, new ItemStack(Items.ARROW, 64));

        // Click with the crossbow, while holding right click long enough to charge it,
        // which takes longer than the time between two clicks
        PartPos posPlayerSimulator = PartPos.of(helper.getLevel(), helper.absolutePos(POS), Direction.WEST);
        placeVariableInWriter(helper.getLevel(), posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, createVariableForValue(helper.getLevel(), ValueTypes.OBJECT_ITEMSTACK, ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.CROSSBOW))));
        setNetworkInventory(posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, true);
        setRightClickDuration(posPlayerSimulator, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, 26);

        helper.succeedWhen(() -> {
            // Check that exactly one arrow was taken from the network
            helper.assertValueEqual(countItems(chestIn, Items.ARROW), 63, "Arrow count");

            // Check that the crossbow was loaded with an arrow
            ChargedProjectiles chargedProjectiles = findItem(chestIn, Items.CROSSBOW).get(DataComponents.CHARGED_PROJECTILES);
            helper.assertTrue(chargedProjectiles != null && chargedProjectiles.contains(Items.ARROW), "The crossbow was not loaded with an arrow");
        });
    }

}
