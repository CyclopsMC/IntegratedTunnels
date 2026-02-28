package org.cyclops.integratedtunnels.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.PartTarget;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.api.part.write.IPartStateWriter;
import org.cyclops.integrateddynamics.api.part.write.IPartTypeWriter;
import org.cyclops.integrateddynamics.core.evaluate.operator.Operators;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.core.part.PartTypes;
import org.cyclops.integrateddynamics.part.aspect.Aspects;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import java.util.List;
import java.util.UUID;

import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForOperator;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableForValue;
import static org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics.createVariableFromReader;

/**
 * Game tests for all advancements in IntegratedTunnels.
 */
@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsAdvancements {

    public static final String TEMPLATE_EMPTY = "empty10";
    public static final int TIMEOUT = 200;
    public static final BlockPos POS = BlockPos.ZERO.offset(2, 0, 2);

    // ===== Helpers =====

    /**
     * Creates a minimal mock ServerPlayer without going through placeNewPlayer(),
     * which avoids issues with custom network payloads in the test environment.
     * The player's PlayerAdvancements is fully initialized via the ServerPlayer constructor.
     */
    private static ServerPlayer createMockPlayer(GameTestHelper helper) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "test-advancement-player");
        return new ServerPlayer(
                helper.getLevel().getServer(), helper.getLevel(), profile, ClientInformation.createDefault()
        ) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return true;
            }
        };
    }

    /**
     * Places a variable item in the writer part's inventory slot for the given aspect,
     * then calls updateActivation with the given player so the part_writer_aspect event fires.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void placeVariableWithPlayer(Level level, PartPos partPos, IAspectWrite<?, ?> aspect, ItemStack variable, ServerPlayer player) {
        PartHelpers.PartStateHolder<?, ?> holder = PartHelpers.getPart(partPos);
        IPartTypeWriter partType = (IPartTypeWriter) holder.getPart();
        IPartStateWriter partState = (IPartStateWriter) holder.getState();

        List<IAspectWrite> aspects = partType.getWriteAspects();
        int slot = -1;
        for (int i = 0; i < aspects.size(); i++) {
            if (aspects.get(i) == aspect) {
                slot = i;
                break;
            }
        }
        if (slot < 0) {
            throw new GameTestAssertException("Aspect not found in part: " + aspect);
        }

        partState.getInventory().setItem(slot, variable);
        partType.updateActivation(PartTarget.fromCenter(partPos), partState, player);
    }

    /**
     * Asserts that a given advancement (by namespace:path) has been completed by the player.
     */
    private static void assertAdvancement(GameTestHelper helper, ServerPlayer player, String namespace, String path) {
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(namespace, path);
        AdvancementHolder advancement = helper.getLevel().getServer().getAdvancements().get(advancementId);
        helper.assertTrue(advancement != null, "Advancement not found: " + advancementId);
        helper.assertTrue(
                player.getAdvancements().getOrStartProgress(advancement).isDone(),
                "Advancement not granted: " + advancementId
        );
    }

    /**
     * Asserts that a given advancement (by namespace:path) has NOT been completed by the player.
     */
    private static void assertAdvancementNotDone(GameTestHelper helper, ServerPlayer player, String namespace, String path) {
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(namespace, path);
        AdvancementHolder advancement = helper.getLevel().getServer().getAdvancements().get(advancementId);
        if (advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            throw new GameTestAssertException("Advancement should NOT have been obtained: " + advancementId);
        }
    }

    // ===== Root advancement (minecraft:inventory_changed) =====

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementRoot(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);

        // Simulate adding a variable to the player's inventory, which triggers inventory_changed
        ItemStack variable = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        player.getInventory().setItem(0, variable);
        CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), variable);

        assertAdvancement(helper, player, Reference.MOD_ID, "root");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementRootNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);

        // Use a non-variable item (dirt) - should NOT trigger the advancement
        ItemStack dirt = new ItemStack(Items.DIRT);
        player.getInventory().setItem(0, dirt);
        CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), dirt);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "root");
        helper.succeed();
    }

    // ===== cyclopscore:item_crafted advancements =====

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementInterfaceItem(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "interfaces/interface_item");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementInterfaceItemNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft interface_fluid instead of interface_item - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_FLUID.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "interfaces/interface_item");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementInterfaceFluid(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_FLUID.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "interfaces/interface_fluid");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementInterfaceFluidNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft interface_item instead of interface_fluid - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "interfaces/interface_fluid");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementInterfaceEnergy(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_ENERGY.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "interfaces/interface_energy");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementInterfaceEnergyNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft interface_item instead of interface_energy - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "interfaces/interface_energy");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementImporterItem(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "importer_exporter/importer_item");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementImporterItemNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft exporter_item instead of importer_item - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "importer_exporter/importer_item");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementExporterItem(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "importer_exporter/exporter_item");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementExporterItemNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft importer_item instead of exporter_item - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "importer_exporter/exporter_item");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementPlayerSimulator(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.PLAYER_SIMULATOR.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "click_sword/player_simulator");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementPlayerSimulatorNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft importer_item instead of player_simulator - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "click_sword/player_simulator");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementWorldImporterBlock(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_BLOCK.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "world_importer_exporter/importer_block");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementWorldImporterBlockNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft importer_world_item instead of importer_world_block - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "world_importer_exporter/importer_block");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementWorldImporterItem(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "world_importer_exporter/importer_item");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementWorldImporterItemNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft importer_world_block instead of importer_world_item - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_BLOCK.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "world_importer_exporter/importer_item");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementWorldExporterBlock(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_BLOCK.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "world_importer_exporter/exporter_block");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementWorldExporterBlockNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft exporter_world_item instead of exporter_world_block - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "world_importer_exporter/exporter_block");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementWorldExporterItem(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_ITEM.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancement(helper, player, Reference.MOD_ID, "world_importer_exporter/exporter_item");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementWorldExporterItemNegative(GameTestHelper helper) {
        ServerPlayer player = createMockPlayer(helper);
        // Craft exporter_world_block instead of exporter_world_item - should NOT trigger the advancement
        ItemStack crafted = new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_BLOCK.getItem());
        EventHooks.firePlayerCraftingEvent(player, crafted, new SimpleContainer(0));
        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "world_importer_exporter/exporter_item");
        helper.succeed();
    }

    // ===== integrateddynamics:part_writer_aspect advancements =====

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementBreakStone(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_BLOCK,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_BLOCK.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        ItemStack variable = createVariableForValue(level, ValueTypes.OBJECT_BLOCK,
                ValueObjectTypeBlock.ValueBlock.of(Blocks.STONE.defaultBlockState()));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.World.BLOCK_BLOCK_IMPORT, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "break_stone/break_stone");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementBreakStoneNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_BLOCK,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_BLOCK.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use dirt block instead of stone - should NOT trigger the advancement
        ItemStack variable = createVariableForValue(level, ValueTypes.OBJECT_BLOCK,
                ValueObjectTypeBlock.ValueBlock.of(Blocks.DIRT.defaultBlockState()));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.World.BLOCK_BLOCK_IMPORT, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "break_stone/break_stone");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementClickSword(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.PLAYER_SIMULATOR,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.PLAYER_SIMULATOR.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        ItemStack variable = createVariableForValue(level, ValueTypes.OBJECT_ITEMSTACK,
                ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.DIAMOND_SWORD)));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "click_sword/click_sword");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementClickSwordNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.PLAYER_SIMULATOR,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.PLAYER_SIMULATOR.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use bone_meal instead of diamond_sword - should NOT trigger the advancement
        ItemStack variable = createVariableForValue(level, ValueTypes.OBJECT_ITEMSTACK,
                ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(Items.BONE_MEAL)));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.Player.CLICK_ITEM_ITEMSTACK, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "click_sword/click_sword");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementDropAllItems(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        ItemStack variable = createVariableForValue(level, ValueTypes.BOOLEAN,
                ValueTypeBoolean.ValueBoolean.of(true));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.World.ENTITYITEM_BOOLEAN_EXPORT, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "drop_all_items/drop_all_items");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementDropAllItemsNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use false instead of true - should NOT trigger the advancement
        ItemStack variable = createVariableForValue(level, ValueTypes.BOOLEAN,
                ValueTypeBoolean.ValueBoolean.of(false));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.World.ENTITYITEM_BOOLEAN_EXPORT, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "drop_all_items/drop_all_items");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementExportEnchantableItems(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Variable is a ValueOperator holding the itemstack_enchantable operator
        ItemStack variable = createVariableForValue(level, ValueTypes.OPERATOR,
                ValueTypeOperator.ValueOperator.of(Operators.OBJECT_ITEMSTACK_ISENCHANTABLE));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.Item.PREDICATE_EXPORT, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "export_enchantable_items/export_enchantable_items");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementExportEnchantableItemsNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use a different operator - not itemstack_enchantable - should NOT trigger the advancement
        ItemStack variable = createVariableForValue(level, ValueTypes.OPERATOR,
                ValueTypeOperator.ValueOperator.of(Operators.OBJECT_ITEMSTACK_ISENCHANTED));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.Item.PREDICATE_EXPORT, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "export_enchantable_items/export_enchantable_items");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementExportItemsLimit(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Variable is an operator expression using relational_lt
        ItemStack variable = createVariableForOperator(level, Operators.RELATIONAL_LT, new int[0]);
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.Item.BOOLEAN_EXPORT, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "export_items_limit/export_items_limit");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementExportItemsLimitNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use relational_gt instead of relational_lt - should NOT trigger the advancement
        ItemStack variable = createVariableForOperator(level, Operators.RELATIONAL_GT, new int[0]);
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.Item.BOOLEAN_EXPORT, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "export_items_limit/export_items_limit");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementFilterStorageDay(GameTestHelper helper) {
        Level level = helper.getLevel();

        // Place cable with world reader (source of the aspect variable)
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                PartTypes.WORLD_READER,
                new ItemStack(PartTypes.WORLD_READER.getItem()));
        PartPos readerPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);

        // Place cable with filter interface
        helper.setBlock(POS.east(), RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS.east()), Direction.EAST,
                org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_FILTERING_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_FILTERING_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos filterPos = PartPos.of(level, helper.absolutePos(POS.east()), Direction.EAST);

        // Create an aspect reader variable for read_boolean_world_isday
        ItemStack variable = createVariableFromReader(level,
                Aspects.Read.World.BOOLEAN_ISDAY,
                PartHelpers.getPart(readerPos).getState());
        placeVariableWithPlayer(level, filterPos, TunnelAspects.Write.ItemFilter.BOOLEAN_SET_FILTER, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "filter_storage_day/filter_storage_day");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementFilterStorageDayNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_FILTERING_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_FILTERING_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use a plain value_type boolean variable instead of an aspect reader - should NOT trigger the advancement
        ItemStack variable = createVariableForValue(level, ValueTypes.BOOLEAN,
                ValueTypeBoolean.ValueBoolean.of(true));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.ItemFilter.BOOLEAN_SET_FILTER, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "filter_storage_day/filter_storage_day");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementFilterStorageMod(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_FILTERING_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_FILTERING_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Variable is an operator expression using operator_pipe
        ItemStack variable = createVariableForOperator(level, Operators.OPERATOR_PIPE, new int[0]);
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.ItemFilter.PREDICATE_SET_FILTER, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "filter_storage_mod/filter_storage_mod");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementFilterStorageModNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_FILTERING_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.INTERFACE_FILTERING_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use relational_lt instead of operator_pipe - should NOT trigger the advancement
        ItemStack variable = createVariableForOperator(level, Operators.RELATIONAL_LT, new int[0]);
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.ItemFilter.PREDICATE_SET_FILTER, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "filter_storage_mod/filter_storage_mod");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementImportAllItems(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        ItemStack variable = createVariableForValue(level, ValueTypes.BOOLEAN,
                ValueTypeBoolean.ValueBoolean.of(true));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.Item.BOOLEAN_IMPORT, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "import_all_items/import_all_items");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementImportAllItemsNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use false instead of true - should NOT trigger the advancement
        ItemStack variable = createVariableForValue(level, ValueTypes.BOOLEAN,
                ValueTypeBoolean.ValueBoolean.of(false));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.Item.BOOLEAN_IMPORT, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "import_all_items/import_all_items");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementImportItemsList(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // No variable condition in the advancement; use an empty variable item
        ItemStack variable = new ItemStack(RegistryEntries.ITEM_VARIABLE);
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.Item.LIST_IMPORT, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "import_items_list/import_items_list");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementImportItemsListNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use boolean_import aspect instead of list_import - should NOT trigger the advancement
        ItemStack variable = createVariableForValue(level, ValueTypes.BOOLEAN,
                ValueTypeBoolean.ValueBoolean.of(true));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.Item.BOOLEAN_IMPORT, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "import_items_list/import_items_list");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementPickupItemsLimit(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        ItemStack variable = createVariableForValue(level, ValueTypes.INTEGER,
                ValueTypeInteger.ValueInteger.of(5));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.World.ENTITYITEM_INTEGER_IMPORT, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "pickup_items_limit/pickup_items_limit");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementPickupItemsLimitNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_ITEM,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.IMPORTER_WORLD_ITEM.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use integer 3 instead of 5 - should NOT trigger the advancement
        ItemStack variable = createVariableForValue(level, ValueTypes.INTEGER,
                ValueTypeInteger.ValueInteger.of(3));
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.World.ENTITYITEM_INTEGER_IMPORT, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "pickup_items_limit/pickup_items_limit");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementPlaceLogwood(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_BLOCK,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_BLOCK.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // The advancement requires "integrateddynamics:string_tag" = Operators.OBJECT_ITEMSTACK_TAG_STACKS,
        // which takes a String (tag name) input and returns matching item stacks.
        ItemStack variable = createVariableForOperator(level, Operators.OBJECT_ITEMSTACK_TAG_STACKS, new int[0]);
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.World.BLOCK_LISTITEMSTACK_EXPORT, variable, player);

        assertAdvancement(helper, player, Reference.MOD_ID, "place_logwood/place_logwood");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT)
    public void testAdvancementPlaceLogwoodNegative(GameTestHelper helper) {
        Level level = helper.getLevel();
        helper.setBlock(POS, RegistryEntries.BLOCK_CABLE.value());
        PartHelpers.addPart(level, helper.absolutePos(POS), Direction.WEST,
                org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_BLOCK,
                new ItemStack(org.cyclops.integratedtunnels.part.PartTypes.EXPORTER_WORLD_BLOCK.getItem()));

        ServerPlayer player = createMockPlayer(helper);
        PartPos partPos = PartPos.of(level, helper.absolutePos(POS), Direction.WEST);
        // Use relational_lt instead of string_tag - should NOT trigger the advancement
        ItemStack variable = createVariableForOperator(level, Operators.RELATIONAL_LT, new int[0]);
        placeVariableWithPlayer(level, partPos, TunnelAspects.Write.World.BLOCK_LISTITEMSTACK_EXPORT, variable, player);

        assertAdvancementNotDone(helper, player, Reference.MOD_ID, "place_logwood/place_logwood");
        helper.succeed();
    }

}
