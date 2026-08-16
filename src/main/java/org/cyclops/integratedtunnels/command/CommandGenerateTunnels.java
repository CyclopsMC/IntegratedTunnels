package org.cyclops.integratedtunnels.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.cyclops.cyclopscore.command.argument.ArgumentTypeEnum;
import org.cyclops.integrateddynamics.RegistryEntries;
import org.cyclops.integrateddynamics.api.part.IPartType;
import org.cyclops.integrateddynamics.api.part.PartPos;
import org.cyclops.integrateddynamics.api.part.aspect.IAspectWrite;
import org.cyclops.integrateddynamics.block.BlockCable;
import org.cyclops.integrateddynamics.blockentity.BlockEntityDryingBasin;
import org.cyclops.integrateddynamics.blockentity.BlockEntityEnergyBattery;
import org.cyclops.integrateddynamics.core.evaluate.operator.CurriedOperator;
import org.cyclops.integrateddynamics.core.evaluate.operator.Operators;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypeOperator;
import org.cyclops.integrateddynamics.core.evaluate.variable.ValueTypes;
import org.cyclops.integrateddynamics.core.evaluate.variable.Variable;
import org.cyclops.integrateddynamics.core.helper.CableHelpers;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.helper.PartHelpers;
import org.cyclops.integrateddynamics.gametest.GameTestHelpersIntegratedDynamics;
import org.cyclops.integratedtunnels.part.PartTypes;
import org.cyclops.integratedtunnels.part.aspect.TunnelAspects;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Command for generating tunnel networks with different presets.
 *
 * These presets are used for performance benchmarking,
 * both from within game tests, and for manual profiling inside a real world.
 *
 * @author rubensworks
 */
public class CommandGenerateTunnels implements Command<CommandSourceStack> {

    public static LiteralArgumentBuilder<CommandSourceStack> make() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("generatetunnels")
                .requires((commandSource) -> commandSource.hasPermission(2));

        // Add the preset subcommand with optional size argument
        builder.then(Commands.argument("preset", new ArgumentTypeEnum(TunnelsPreset.class))
                .executes(new CommandGenerateTunnelsExecutor(true, false))
                .then(Commands.argument("size", IntegerArgumentType.integer(1, 100))
                        .executes(new CommandGenerateTunnelsExecutor(true, true))));

        return builder;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().sendFailure(Component.literal("Please specify one of the presets: " + joinPresets())
                .withStyle(ChatFormatting.RED));
        return 0;
    }

    /**
     * @return All preset names, joined by a comma.
     */
    public static String joinPresets() {
        StringBuilder sb = new StringBuilder();
        for (TunnelsPreset preset : TunnelsPreset.values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(preset.name().toLowerCase());
        }
        return sb.toString();
    }

    /**
     * The available network presets.
     */
    public enum TunnelsPreset {
        /** Only cables, no parts: the baseline that the append benchmark grows from. */
        EMPTY,
        /** Item interfaces observing filled chests. */
        ITEMINTERFACES,
        /** Fluid interfaces observing filled drying basins. */
        FLUIDINTERFACES,
        /** Energy interfaces observing filled energy batteries. */
        ENERGYINTERFACES,
        /** A few item interfaces observing completely filled chests. */
        ITEMINTERFACESDEEP,
        /** Item interfaces with item exporters and importers continuously moving items around. */
        ITEMTRANSFER,
        /** Fluid interfaces with fluid exporters and importers continuously moving fluids around. */
        FLUIDTRANSFER,
        /** Energy interfaces with energy exporters and importers continuously moving energy around. */
        ENERGYTRANSFER,
        /** As {@link #ITEMTRANSFER}, but driven by predicate aspects instead of boolean aspects. */
        ITEMTRANSFERPREDICATE,
        /** As {@link #ITEMTRANSFER}, but with filtering item interfaces as network storage. */
        ITEMFILTERINGINTERFACES,
        /** Many item interfaces holding distinct item types, queried by itemstack exporters. */
        ITEMINDEXQUERY,
        /** World block exporters and importers continuously placing and breaking blocks. */
        WORLDBLOCKCHURN,
        /** World entity item exporters and importers continuously dropping and picking up items. */
        WORLDENTITYITEMCHURN,
        /** Player simulators continuously simulating right-clicks. */
        PLAYERSIMULATORS,
        /** Remove everything that the other presets generate. */
        CLEAR,
    }

    /**
     * Executor for the generatetunnels command.
     */
    public static class CommandGenerateTunnelsExecutor implements Command<CommandSourceStack> {
        private final boolean hasPreset;
        private final boolean hasSize;

        public CommandGenerateTunnelsExecutor(boolean hasPreset, boolean hasSize) {
            this.hasPreset = hasPreset;
            this.hasSize = hasSize;
        }

        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            if (!hasPreset) {
                context.getSource().sendFailure(Component.literal("Please specify one of the presets: " + joinPresets())
                        .withStyle(ChatFormatting.RED));
                return 0;
            }

            TunnelsPreset preset = ArgumentTypeEnum.getValue(context, "preset", TunnelsPreset.class);
            ServerLevel level = context.getSource().getLevel();
            BlockPos playerPos = BlockPos.containing(context.getSource().getPosition());
            int size = hasSize ? IntegerArgumentType.getInteger(context, "size") : getDefaultSize(preset);

            if (preset == TunnelsPreset.CLEAR) {
                context.getSource().sendSuccess(
                        () -> Component.literal("Clearing generated tunnel networks within radius: " + size)
                                .withStyle(ChatFormatting.GREEN),
                        true);
                TunnelsGenerationHelper.clearGrid(level, playerPos, size);
                return 1;
            }

            context.getSource().sendSuccess(
                    () -> Component.literal("Generating tunnels preset: " + preset.name().toLowerCase()
                                    + " (size: " + size + "x" + size + "x" + size + ")")
                            .withStyle(ChatFormatting.GREEN),
                    true);
            TunnelsGenerationHelper.generate(preset, level, playerPos.above(2), size);

            return 1;
        }

        /**
         * Get the default size for the given preset.
         */
        private int getDefaultSize(TunnelsPreset preset) {
            return preset == TunnelsPreset.CLEAR ? 50 : 9;
        }
    }

    /**
     * Helper class for tunnel network generation logic, shared between command and game tests.
     *
     * All presets are built on top of the same grid layout:
     * cable planes at even Y levels, and a checkerboard of cables and free "cells" at odd Y levels.
     * Every cell is a free position that is surrounded by cables of a single network,
     * so it can hold a container that is observed or targeted by parts on the surrounding cables.
     */
    public static class TunnelsGenerationHelper {

        /**
         * Item types used to fill up the generated containers.
         */
        public static final List<Item> ITEM_POOL = Lists.newArrayList(
                Items.STONE, Items.COBBLESTONE, Items.DIRT, Items.SAND, Items.GRAVEL,
                Items.OAK_LOG, Items.BIRCH_LOG, Items.SPRUCE_LOG, Items.IRON_INGOT, Items.GOLD_INGOT,
                Items.DIAMOND, Items.EMERALD, Items.REDSTONE, Items.COAL, Items.CHARCOAL,
                Items.APPLE, Items.BREAD, Items.WHEAT, Items.CARROT, Items.POTATO,
                Items.WHITE_WOOL, Items.RED_WOOL, Items.BLUE_WOOL, Items.GREEN_WOOL, Items.GLASS,
                Items.OBSIDIAN, Items.NETHERRACK, Items.QUARTZ, Items.LAPIS_LAZULI, Items.FLINT,
                Items.BONE, Items.STRING, Items.FEATHER, Items.LEATHER, Items.PAPER,
                Items.BOOK, Items.STICK, Items.TORCH, Items.CLAY_BALL, Items.SUGAR
        );

        /**
         * The item that is placed into and broken out of the world by the world block churn preset.
         */
        public static final Item CHURN_BLOCK_ITEM = Items.COBBLESTONE;

        /**
         * The number of distinct item types that are inserted into each regular storage container.
         */
        public static final int DEFAULT_VARIETY = 9;

        /**
         * The number of cells that the "deep" preset uses.
         */
        public static final int DEEP_CELLS = 16;

        /**
         * The amount of fluid that is inserted into each generated fluid container.
         */
        private static final int FLUID_AMOUNT = 1_000;

        /**
         * The amount of energy that is inserted into each generated energy container.
         */
        private static final int ENERGY_AMOUNT = 10_000;

        /**
         * Generate the given preset.
         * @param preset The preset to generate.
         * @param level The level to generate in.
         * @param startPos The lowest corner of the generated grid.
         * @param size The edge length of the generated grid.
         */
        public static void generate(TunnelsPreset preset, ServerLevel level, BlockPos startPos, int size) {
            switch (preset) {
                case EMPTY -> generateEmptyGrid(level, startPos, size);
                case ITEMINTERFACES -> generateItemInterfaces(level, startPos, size);
                case FLUIDINTERFACES -> generateFluidInterfaces(level, startPos, size);
                case ENERGYINTERFACES -> generateEnergyInterfaces(level, startPos, size);
                case ITEMINTERFACESDEEP -> generateItemInterfacesDeep(level, startPos, size);
                case ITEMTRANSFER -> generateItemTransfer(level, startPos, size);
                case FLUIDTRANSFER -> generateFluidTransfer(level, startPos, size);
                case ENERGYTRANSFER -> generateEnergyTransfer(level, startPos, size);
                case ITEMTRANSFERPREDICATE -> generateItemTransferPredicate(level, startPos, size);
                case ITEMFILTERINGINTERFACES -> generateItemFilteringInterfaces(level, startPos, size);
                case ITEMINDEXQUERY -> generateItemIndexQuery(level, startPos, size);
                case WORLDBLOCKCHURN -> generateWorldBlockChurn(level, startPos, size);
                case WORLDENTITYITEMCHURN -> generateWorldEntityItemChurn(level, startPos, size);
                case PLAYERSIMULATORS -> generatePlayerSimulators(level, startPos, size);
                case CLEAR -> clearGrid(level, startPos, size);
            }
        }

        /**
         * Generate a grid of cables without any parts or containers.
         * @param level The level to generate in.
         * @param startPos The lowest corner of the generated grid.
         * @param size The edge length of the generated grid.
         */
        public static void generateEmptyGrid(ServerLevel level, BlockPos startPos, int size) {
            List<BlockPos> placedPositions = Lists.newArrayList();

            // Place all cables at once without triggering a network init for each of them,
            // as that would make generation unusably slow for larger sizes.
            BlockCable.SKIP_NETWORK_INIT = true;
            try {
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        for (int z = 0; z < size; z++) {
                            if (isCablePosition(x, y, z)) {
                                BlockPos pos = startPos.offset(x, y, z);
                                level.setBlock(pos, RegistryEntries.BLOCK_CABLE.value().defaultBlockState(), 2);
                                placedPositions.add(pos);
                            }
                        }
                    }
                }
            } finally {
                BlockCable.SKIP_NETWORK_INIT = false;
            }

            for (BlockPos pos : placedPositions) {
                CableHelpers.updateConnectionsNeighbours(level, pos, CableHelpers.ALL_SIDES);
            }

            NetworkHelpers.initNetwork(level, startPos, null);
        }

        /**
         * @param x The local X coordinate within the grid.
         * @param y The local Y coordinate within the grid.
         * @param z The local Z coordinate within the grid.
         * @return If a cable should be placed at the given local grid coordinate.
         */
        private static boolean isCablePosition(int x, int y, int z) {
            // Even Y levels are fully filled with cables,
            // odd Y levels alternate between cables and free cells.
            return y % 2 == 0 || (x + z) % 2 == 0;
        }

        /**
         * Get all cell positions of the grid, in a deterministic order.
         *
         * A cell is a free position at an odd Y level that is surrounded by cables,
         * so that it can hold a container that is observed or targeted by the surrounding parts.
         *
         * @param startPos The lowest corner of the grid.
         * @param size The edge length of the grid.
         * @return The absolute cell positions.
         */
        public static List<BlockPos> getCells(BlockPos startPos, int size) {
            List<BlockPos> cells = Lists.newArrayList();
            for (int y = 1; y < size; y += 2) {
                for (int x = 0; x < size; x++) {
                    for (int z = 0; z < size; z++) {
                        if (!isCablePosition(x, y, z)) {
                            cells.add(startPos.offset(x, y, z));
                        }
                    }
                }
            }
            return cells;
        }

        /**
         * Get the direction from the given cell towards a horizontally neighbouring cable.
         * @param level The level.
         * @param cell A cell position.
         * @return The direction, or null if the cell has no horizontally neighbouring cable.
         */
        @Nullable
        public static Direction getSideCableDirection(ServerLevel level, BlockPos cell) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (level.getBlockState(cell.relative(direction)).getBlock() == RegistryEntries.BLOCK_CABLE.value()) {
                    return direction;
                }
            }
            return null;
        }

        /**
         * Add a part on the cable below the given cell, targeting the cell.
         */
        private static PartPos addPartBelow(ServerLevel level, BlockPos cell, IPartType partType) {
            BlockPos cablePos = cell.below();
            PartHelpers.addPart(level, cablePos, Direction.UP, partType, new ItemStack(partType.getItem()));
            return PartPos.of(level, cablePos, Direction.UP);
        }

        /**
         * Add a part on a cable next to the given cell, targeting the cell.
         * @return The position of the added part, or null if the cell has no horizontal cable neighbour.
         */
        @Nullable
        private static PartPos addPartBeside(ServerLevel level, BlockPos cell, IPartType partType) {
            Direction sideDirection = getSideCableDirection(level, cell);
            if (sideDirection == null) {
                return null;
            }
            BlockPos cablePos = cell.relative(sideDirection);
            Direction side = sideDirection.getOpposite();
            PartHelpers.addPart(level, cablePos, side, partType, new ItemStack(partType.getItem()));
            return PartPos.of(level, cablePos, side);
        }

        /**
         * Enable the given aspect on the given part by inserting an empty variable.
         */
        private static void activate(ServerLevel level, PartPos partPos, IAspectWrite<?, ?> aspect) {
            activate(level, partPos, aspect, new ItemStack(RegistryEntries.ITEM_VARIABLE));
        }

        /**
         * Enable the given aspect on the given part by inserting a copy of the given variable.
         */
        private static void activate(ServerLevel level, PartPos partPos, IAspectWrite<?, ?> aspect, ItemStack variable) {
            GameTestHelpersIntegratedDynamics.placeVariableInWriter(level, partPos, aspect, variable.copy());
        }

        /**
         * Create a variable holding a predicate that matches the given item.
         */
        public static ItemStack createItemPredicate(ServerLevel level, Item item) {
            return GameTestHelpersIntegratedDynamics.createVariableForValue(level, ValueTypes.OPERATOR,
                    ValueTypeOperator.ValueOperator.of(new CurriedOperator(
                            Operators.RELATIONAL_EQUALS,
                            new Variable<>(ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(item)))
                    )));
        }

        /**
         * Create a full stack of the given item.
         */
        private static ItemStack createFullStack(Item item) {
            ItemStack itemStack = new ItemStack(item);
            itemStack.setCount(itemStack.getMaxStackSize());
            return itemStack;
        }

        /**
         * Place a chest at the given cell, and fill it with the given number of distinct item types.
         * @param level The level.
         * @param cell The cell position.
         * @param variety The number of distinct item types to insert.
         * @param itemOffset The offset within {@link #ITEM_POOL} to start inserting from.
         */
        public static void placeChest(ServerLevel level, BlockPos cell, int variety, int itemOffset) {
            level.setBlock(cell, Blocks.CHEST.defaultBlockState(), 2);
            if (variety <= 0) {
                return;
            }
            if (level.getBlockEntity(cell) instanceof ChestBlockEntity chest) {
                for (int slot = 0; slot < Math.min(variety, chest.getContainerSize()); slot++) {
                    chest.setItem(slot, createFullStack(ITEM_POOL.get(Math.floorMod(itemOffset + slot, ITEM_POOL.size()))));
                }
            }
        }

        /**
         * Place a chest at the given cell, and fill it completely with the given item.
         */
        public static void placeChestOf(ServerLevel level, BlockPos cell, Item item) {
            level.setBlock(cell, Blocks.CHEST.defaultBlockState(), 2);
            if (level.getBlockEntity(cell) instanceof ChestBlockEntity chest) {
                for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                    chest.setItem(slot, createFullStack(item));
                }
            }
        }

        /**
         * Place a drying basin at the given cell, optionally filled with water.
         */
        public static void placeFluidContainer(ServerLevel level, BlockPos cell, boolean filled) {
            level.setBlock(cell, RegistryEntries.BLOCK_DRYING_BASIN.get().defaultBlockState(), 2);
            if (filled && level.getBlockEntity(cell) instanceof BlockEntityDryingBasin basin) {
                basin.getTank().setFluid(new FluidStack(Fluids.WATER, FLUID_AMOUNT));
            }
        }

        /**
         * Place an energy battery at the given cell, optionally filled with energy.
         */
        public static void placeEnergyContainer(ServerLevel level, BlockPos cell, boolean filled) {
            level.setBlock(cell, RegistryEntries.BLOCK_ENERGY_BATTERY.get().defaultBlockState(), 2);
            if (filled && level.getBlockEntity(cell) instanceof BlockEntityEnergyBattery battery) {
                battery.setEnergyStored(ENERGY_AMOUNT);
            }
        }

        /**
         * Notify all cables around the given cells that their neighbours have changed,
         * so that newly added parts and containers are picked up by the network.
         */
        private static void updateCells(ServerLevel level, List<BlockPos> cells) {
            for (BlockPos cell : cells) {
                for (Direction direction : Direction.values()) {
                    BlockPos pos = cell.relative(direction);
                    if (level.getBlockState(pos).getBlock() == RegistryEntries.BLOCK_CABLE.value()) {
                        level.updateNeighborsAt(pos, RegistryEntries.BLOCK_CABLE.value());
                    }
                }
            }
        }

        /**
         * Generate a grid where every cell holds a filled chest that is exposed to the network
         * by an item interface.
         * This isolates the cost of the ingredient observer for the item channel.
         */
        public static void generateItemInterfaces(ServerLevel level, BlockPos startPos, int size) {
            generateInterfaces(level, startPos, size, IngredientKind.ITEM, DEFAULT_VARIETY, false, 0);
        }

        /**
         * Generate a grid where every cell holds a filled drying basin that is exposed to the network
         * by a fluid interface.
         */
        public static void generateFluidInterfaces(ServerLevel level, BlockPos startPos, int size) {
            generateInterfaces(level, startPos, size, IngredientKind.FLUID, DEFAULT_VARIETY, false, 0);
        }

        /**
         * Generate a grid where every cell holds a filled energy battery that is exposed to the network
         * by an energy interface.
         */
        public static void generateEnergyInterfaces(ServerLevel level, BlockPos startPos, int size) {
            generateInterfaces(level, startPos, size, IngredientKind.ENERGY, DEFAULT_VARIETY, false, 0);
        }

        /**
         * Generate a grid with only a few item interfaces, but with completely filled chests.
         * Compared to {@link #generateItemInterfaces}, this shifts the observer cost
         * from the number of observed positions to the number of observed slots.
         */
        public static void generateItemInterfacesDeep(ServerLevel level, BlockPos startPos, int size) {
            generateInterfaces(level, startPos, size, IngredientKind.ITEM, Integer.MAX_VALUE, false, DEEP_CELLS);
        }

        /**
         * Generate a grid where every cell holds a container that is exposed to the network by an interface.
         * @param level The level.
         * @param startPos The lowest corner of the grid.
         * @param size The edge length of the grid.
         * @param kind The ingredient kind to generate interfaces for.
         * @param variety The number of distinct item types per container, for item containers.
         * @param filtering If filtering interfaces should be used instead of regular interfaces.
         * @param maxCells The maximum number of cells to use, or 0 for all cells.
         */
        private static void generateInterfaces(ServerLevel level, BlockPos startPos, int size, IngredientKind kind,
                                               int variety, boolean filtering, int maxCells) {
            generateEmptyGrid(level, startPos, size);

            List<BlockPos> cells = getCells(startPos, size);
            if (maxCells > 0 && cells.size() > maxCells) {
                cells = cells.subList(0, maxCells);
            }

            for (int i = 0; i < cells.size(); i++) {
                BlockPos cell = cells.get(i);
                placeStorageContainer(level, cell, kind, variety, i * DEFAULT_VARIETY);
                PartPos interfacePos = addPartBelow(level, cell, kind.getInterfacePartType(filtering));
                if (filtering) {
                    activate(level, interfacePos, kind.getBooleanFilterAspect());
                }
            }

            updateCells(level, cells);
        }

        /**
         * Place the storage container of the given ingredient kind at the given cell.
         */
        private static void placeStorageContainer(ServerLevel level, BlockPos cell, IngredientKind kind,
                                                  int variety, int itemOffset) {
            switch (kind) {
                case ITEM -> placeChest(level, cell, variety, itemOffset);
                case FLUID -> placeFluidContainer(level, cell, true);
                case ENERGY -> placeEnergyContainer(level, cell, true);
            }
        }

        /**
         * Place an empty container of the given ingredient kind at the given cell.
         */
        private static void placeEmptyContainer(ServerLevel level, BlockPos cell, IngredientKind kind) {
            switch (kind) {
                case ITEM -> placeChest(level, cell, 0, 0);
                case FLUID -> placeFluidContainer(level, cell, false);
                case ENERGY -> placeEnergyContainer(level, cell, false);
            }
        }

        /**
         * Generate a grid where items are continuously moved between the network and the world.
         */
        public static void generateItemTransfer(ServerLevel level, BlockPos startPos, int size) {
            generateTransfer(level, startPos, size, IngredientKind.ITEM, false, false);
        }

        /**
         * Generate a grid where fluids are continuously moved between the network and the world.
         */
        public static void generateFluidTransfer(ServerLevel level, BlockPos startPos, int size) {
            generateTransfer(level, startPos, size, IngredientKind.FLUID, false, false);
        }

        /**
         * Generate a grid where energy is continuously moved between the network and the world.
         */
        public static void generateEnergyTransfer(ServerLevel level, BlockPos startPos, int size) {
            generateTransfer(level, startPos, size, IngredientKind.ENERGY, false, false);
        }

        /**
         * Generate a grid where items are continuously moved around, driven by predicate aspects.
         * Compared to {@link #generateItemTransfer}, this additionally measures the cost of
         * evaluating a predicate for every candidate ingredient.
         */
        public static void generateItemTransferPredicate(ServerLevel level, BlockPos startPos, int size) {
            generateTransfer(level, startPos, size, IngredientKind.ITEM, true, false);
        }

        /**
         * Generate a grid where items are continuously moved around,
         * with filtering item interfaces as network storage.
         */
        public static void generateItemFilteringInterfaces(ServerLevel level, BlockPos startPos, int size) {
            generateTransfer(level, startPos, size, IngredientKind.ITEM, false, true);
        }

        /**
         * Generate a grid where every other cell is network storage,
         * and the remaining cells continuously export ingredients out of the network and import them back.
         * @param level The level.
         * @param startPos The lowest corner of the grid.
         * @param size The edge length of the grid.
         * @param kind The ingredient kind to transfer.
         * @param predicate If predicate aspects should be used instead of boolean aspects.
         * @param filtering If filtering interfaces should be used as storage.
         */
        private static void generateTransfer(ServerLevel level, BlockPos startPos, int size, IngredientKind kind,
                                             boolean predicate, boolean filtering) {
            generateEmptyGrid(level, startPos, size);

            List<BlockPos> cells = getCells(startPos, size);
            for (int i = 0; i < cells.size(); i++) {
                BlockPos cell = cells.get(i);
                if (i % 2 == 0) {
                    // Storage cell
                    placeStorageContainer(level, cell, kind, DEFAULT_VARIETY, i * DEFAULT_VARIETY);
                    PartPos interfacePos = addPartBelow(level, cell, kind.getInterfacePartType(filtering));
                    if (filtering) {
                        activate(level, interfacePos, kind.getBooleanFilterAspect());
                    }
                } else {
                    // Churn cell: export out of the network, and import back in
                    placeEmptyContainer(level, cell, kind);

                    PartPos exporter = addPartBelow(level, cell, kind.getExporterPartType());
                    PartPos importer = addPartBeside(level, cell, kind.getImporterPartType());
                    if (predicate) {
                        ItemStack itemPredicate = createItemPredicate(level, ITEM_POOL.get(Math.floorMod(i, ITEM_POOL.size())));
                        activate(level, exporter, TunnelAspects.Write.Item.PREDICATE_EXPORT, itemPredicate);
                        if (importer != null) {
                            activate(level, importer, TunnelAspects.Write.Item.PREDICATE_IMPORT, itemPredicate);
                        }
                    } else {
                        activate(level, exporter, kind.getBooleanExportAspect());
                        if (importer != null) {
                            activate(level, importer, kind.getBooleanImportAspect());
                        }
                    }
                }
            }

            updateCells(level, cells);
        }

        /**
         * Generate a grid where a large number of item interfaces hold distinct item types,
         * and where a part of the cells continuously queries specific itemstacks out of the resulting index.
         */
        public static void generateItemIndexQuery(ServerLevel level, BlockPos startPos, int size) {
            generateEmptyGrid(level, startPos, size);

            List<BlockPos> cells = getCells(startPos, size);
            for (int i = 0; i < cells.size(); i++) {
                BlockPos cell = cells.get(i);
                if (i % 4 == 3) {
                    // Query cell: export one specific itemstack out of the network, and import it back
                    placeChest(level, cell, 0, 0);
                    Item queriedItem = ITEM_POOL.get(Math.floorMod(i, ITEM_POOL.size()));
                    PartPos exporter = addPartBelow(level, cell, PartTypes.EXPORTER_ITEM);
                    activate(level, exporter, TunnelAspects.Write.Item.ITEMSTACK_EXPORT,
                            GameTestHelpersIntegratedDynamics.createVariableForValue(level, ValueTypes.OBJECT_ITEMSTACK,
                                    ValueObjectTypeItemStack.ValueItemStack.of(new ItemStack(queriedItem))));
                    PartPos importer = addPartBeside(level, cell, PartTypes.IMPORTER_ITEM);
                    if (importer != null) {
                        activate(level, importer, TunnelAspects.Write.Item.BOOLEAN_IMPORT);
                    }
                } else {
                    // Storage cell, holding a distinct set of item types
                    placeChest(level, cell, DEFAULT_VARIETY, i * DEFAULT_VARIETY);
                    addPartBelow(level, cell, PartTypes.INTERFACE_ITEM);
                }
            }

            updateCells(level, cells);
        }

        /**
         * Generate a grid where world block exporters continuously place blocks into the cells,
         * and world block importers break them again.
         */
        public static void generateWorldBlockChurn(ServerLevel level, BlockPos startPos, int size) {
            generateEmptyGrid(level, startPos, size);

            List<BlockPos> cells = getCells(startPos, size);
            for (int i = 0; i < cells.size(); i++) {
                BlockPos cell = cells.get(i);
                if (i % 2 == 0) {
                    // Storage cell, holding the blocks that are being placed
                    placeChestOf(level, cell, CHURN_BLOCK_ITEM);
                    addPartBelow(level, cell, PartTypes.INTERFACE_ITEM);
                } else {
                    // Churn cell: place a block into the cell, and break it again
                    PartPos exporter = addPartBelow(level, cell, PartTypes.EXPORTER_WORLD_BLOCK);
                    activate(level, exporter, TunnelAspects.Write.World.BLOCK_BOOLEAN_EXPORT);
                    PartPos importer = addPartBeside(level, cell, PartTypes.IMPORTER_WORLD_BLOCK);
                    if (importer != null) {
                        activate(level, importer, TunnelAspects.Write.World.BLOCK_BOOLEAN_IMPORT);
                    }
                }
            }

            updateCells(level, cells);
        }

        /**
         * Generate a grid where world entity item exporters continuously drop items into the cells,
         * and world entity item importers pick them up again.
         */
        public static void generateWorldEntityItemChurn(ServerLevel level, BlockPos startPos, int size) {
            generateEmptyGrid(level, startPos, size);

            List<BlockPos> cells = getCells(startPos, size);
            for (int i = 0; i < cells.size(); i++) {
                BlockPos cell = cells.get(i);
                if (i % 2 == 0) {
                    // Storage cell, holding the items that are being dropped
                    placeChest(level, cell, DEFAULT_VARIETY, i * DEFAULT_VARIETY);
                    addPartBelow(level, cell, PartTypes.INTERFACE_ITEM);
                } else {
                    // Churn cell: drop items into the cell, and pick them up again
                    PartPos exporter = addPartBelow(level, cell, PartTypes.EXPORTER_WORLD_ITEM);
                    activate(level, exporter, TunnelAspects.Write.World.ENTITYITEM_BOOLEAN_EXPORT);
                    PartPos importer = addPartBeside(level, cell, PartTypes.IMPORTER_WORLD_ITEM);
                    if (importer != null) {
                        activate(level, importer, TunnelAspects.Write.World.ENTITYITEM_BOOLEAN_IMPORT);
                    }
                }
            }

            updateCells(level, cells);
        }

        /**
         * Generate a grid where player simulators continuously simulate right-clicks into the cells.
         */
        public static void generatePlayerSimulators(ServerLevel level, BlockPos startPos, int size) {
            generateEmptyGrid(level, startPos, size);

            List<BlockPos> cells = getCells(startPos, size);
            for (int i = 0; i < cells.size(); i++) {
                BlockPos cell = cells.get(i);
                if (i % 2 == 0) {
                    // Storage cell, so that the simulators operate on a realistic network
                    placeChest(level, cell, DEFAULT_VARIETY, i * DEFAULT_VARIETY);
                    addPartBelow(level, cell, PartTypes.INTERFACE_ITEM);
                } else {
                    PartPos simulator = addPartBelow(level, cell, PartTypes.PLAYER_SIMULATOR);
                    activate(level, simulator, TunnelAspects.Write.Player.CLICK_EMPTY_BOOLEAN);
                }
            }

            updateCells(level, cells);
        }

        /**
         * Add an item interface with a filled chest at the given cell.
         * This is used to measure the cost of growing a network at runtime.
         * @param level The level.
         * @param cell The cell to add an interface for.
         * @param itemOffset The offset within {@link #ITEM_POOL} to start inserting from.
         */
        public static void addItemInterfaceCell(ServerLevel level, BlockPos cell, int itemOffset) {
            placeChest(level, cell, DEFAULT_VARIETY, itemOffset);
            addPartBelow(level, cell, PartTypes.INTERFACE_ITEM);
            updateCells(level, Lists.newArrayList(cell));
        }

        /**
         * Remove the container of the given cell, together with the cable that holds its part.
         * This is used to measure the cost of shrinking a network at runtime.
         * @param level The level.
         * @param cell The cell to remove.
         */
        public static void removeCell(ServerLevel level, BlockPos cell) {
            level.destroyBlock(cell, false);
            level.destroyBlock(cell.below(), false);
        }

        /**
         * Remove all blocks that the presets of this command can generate,
         * within the given radius of the given position.
         * @param level The level.
         * @param centerPos The center position.
         * @param radius The radius to clear.
         */
        public static void clearGrid(ServerLevel level, BlockPos centerPos, int radius) {
            BlockCable.SKIP_NETWORK_INIT = true;

            try {
                for (int x = centerPos.getX() - radius; x <= centerPos.getX() + radius; x++) {
                    for (int y = centerPos.getY() - radius; y <= centerPos.getY() + radius; y++) {
                        for (int z = centerPos.getZ() - radius; z <= centerPos.getZ() + radius; z++) {
                            BlockPos pos = new BlockPos(x, y, z);
                            if (isGeneratedBlock(level.getBlockState(pos).getBlock())) {
                                level.destroyBlock(pos, false);
                            }
                        }
                    }
                }
            } finally {
                BlockCable.SKIP_NETWORK_INIT = false;
            }
        }

        /**
         * @param block A block.
         * @return If the given block is one that the presets of this command can generate.
         */
        private static boolean isGeneratedBlock(Block block) {
            return block == RegistryEntries.BLOCK_CABLE.value()
                    || block == Blocks.CHEST
                    || block == RegistryEntries.BLOCK_DRYING_BASIN.get()
                    || block == RegistryEntries.BLOCK_ENERGY_BATTERY.get()
                    || block == Block.byItem(CHURN_BLOCK_ITEM);
        }

        /**
         * The ingredient kinds that can be transferred over a network.
         */
        public enum IngredientKind {
            ITEM,
            FLUID,
            ENERGY;

            public IPartType getInterfacePartType(boolean filtering) {
                return switch (this) {
                    case ITEM -> filtering ? PartTypes.INTERFACE_FILTERING_ITEM : PartTypes.INTERFACE_ITEM;
                    case FLUID -> filtering ? PartTypes.INTERFACE_FILTERING_FLUID : PartTypes.INTERFACE_FLUID;
                    case ENERGY -> filtering ? PartTypes.INTERFACE_FILTERING_ENERGY : PartTypes.INTERFACE_ENERGY;
                };
            }

            public IPartType getExporterPartType() {
                return switch (this) {
                    case ITEM -> PartTypes.EXPORTER_ITEM;
                    case FLUID -> PartTypes.EXPORTER_FLUID;
                    case ENERGY -> PartTypes.EXPORTER_ENERGY;
                };
            }

            public IPartType getImporterPartType() {
                return switch (this) {
                    case ITEM -> PartTypes.IMPORTER_ITEM;
                    case FLUID -> PartTypes.IMPORTER_FLUID;
                    case ENERGY -> PartTypes.IMPORTER_ENERGY;
                };
            }

            public IAspectWrite<?, ?> getBooleanExportAspect() {
                return switch (this) {
                    case ITEM -> TunnelAspects.Write.Item.BOOLEAN_EXPORT;
                    case FLUID -> TunnelAspects.Write.Fluid.BOOLEAN_EXPORT;
                    case ENERGY -> TunnelAspects.Write.Energy.BOOLEAN_EXPORT;
                };
            }

            public IAspectWrite<?, ?> getBooleanImportAspect() {
                return switch (this) {
                    case ITEM -> TunnelAspects.Write.Item.BOOLEAN_IMPORT;
                    case FLUID -> TunnelAspects.Write.Fluid.BOOLEAN_IMPORT;
                    case ENERGY -> TunnelAspects.Write.Energy.BOOLEAN_IMPORT;
                };
            }

            public IAspectWrite<?, ?> getBooleanFilterAspect() {
                return switch (this) {
                    case ITEM -> TunnelAspects.Write.ItemFilter.BOOLEAN_SET_FILTER;
                    case FLUID -> TunnelAspects.Write.FluidFilter.BOOLEAN_SET_FILTER;
                    case ENERGY -> TunnelAspects.Write.EnergyFilter.BOOLEAN_SET_FILTER;
                };
            }
        }
    }
}
