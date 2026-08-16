package org.cyclops.integratedtunnels.gametest;

import com.google.common.math.Stats;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.TimeUtil;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.datastructure.Wrapper;
import org.cyclops.integrateddynamics.core.network.diagnostics.NetworkDiagnostics;
import org.cyclops.integratedtunnels.IntegratedTunnels;
import org.cyclops.integratedtunnels.Reference;
import org.cyclops.integratedtunnels.command.CommandGenerateTunnels;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Game tests for performance benchmarking of tunnel operations.
 * These tests generate networks with different presets and measure their performance.
 * Results are written to runs/gameTestServer/logs/benchmark_results.txt for CI processing.
 *
 * These tests only do actual work when the PERFORMANCE_BENCHMARK_ENABLED environment variable is set,
 * so that regular game test runs are not slowed down by them.
 *
 * @author rubensworks
 */
@GameTestHolder(Reference.MOD_ID)
@PrefixGameTestTemplate(false)
public class GameTestsPerformance {

    public static final int EXECUTION_SECONDS = 10;
    public static final int WARMUP_TICKS = 200;
    public static final int TIMEOUT_TICKS = (EXECUTION_SECONDS + 20) * 20;
    public static final int SIZE = 9; // Max 9, as the grid would otherwise leak out of the template.
    public static final String TEMPLATE_EMPTY = "empty10";
    public static final BlockPos START_POS = BlockPos.ZERO;

    /**
     * The number of cells that are added or removed after warming up, for the churn benchmarks.
     */
    public static final int CHURN_CELLS = 50;

    private static final String RESULTS_FILE = "logs/benchmark_results.txt";

    /**
     * Check if performance benchmarking is enabled via environment variable.
     *
     * @return true if PERFORMANCE_BENCHMARK_ENABLED environment variable is set to "true"
     */
    private static boolean isBenchmarkingEnabled() {
        // Check environment variable first
        String envVar = System.getenv("PERFORMANCE_BENCHMARK_ENABLED");
        if (envVar != null && "true".equalsIgnoreCase(envVar)) {
            return true;
        }

        // Check system property as fallback
        String sysProp = System.getProperty("PERFORMANCE_BENCHMARK_ENABLED");
        return sysProp != null && "true".equalsIgnoreCase(sysProp);
    }

    static {
        if (isBenchmarkingEnabled()) {
            // Initialize empty file
            writeResults(new ArrayList<>(), false);
        }
    }

    /*
     * Storage observation: the cost of the ingredient observers that watch the containers
     * that interfaces expose to the network.
     */

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_interfaces_item_idle")
    public void testPerformanceItemInterfaces(GameTestHelper helper) {
        testPerformance(helper, "interfaces_item_idle", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateItemInterfaces(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_interfaces_fluid_idle")
    public void testPerformanceFluidInterfaces(GameTestHelper helper) {
        testPerformance(helper, "interfaces_fluid_idle", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateFluidInterfaces(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_interfaces_energy_idle")
    public void testPerformanceEnergyInterfaces(GameTestHelper helper) {
        testPerformance(helper, "interfaces_energy_idle", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateEnergyInterfaces(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_interfaces_item_idle_deep")
    public void testPerformanceItemInterfacesDeep(GameTestHelper helper) {
        testPerformance(helper, "interfaces_item_idle_deep", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateItemInterfacesDeep(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    /*
     * Active transfer: the cost of continuously moving ingredients between the network and the world.
     */

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_items_transfer")
    public void testPerformanceItemTransfer(GameTestHelper helper) {
        testPerformance(helper, "items_transfer", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateItemTransfer(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_fluids_transfer")
    public void testPerformanceFluidTransfer(GameTestHelper helper) {
        testPerformance(helper, "fluids_transfer", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateFluidTransfer(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_energy_transfer")
    public void testPerformanceEnergyTransfer(GameTestHelper helper) {
        testPerformance(helper, "energy_transfer", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateEnergyTransfer(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_items_transfer_predicate")
    public void testPerformanceItemTransferPredicate(GameTestHelper helper) {
        testPerformance(helper, "items_transfer_predicate", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateItemTransferPredicate(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_items_filtering_interfaces")
    public void testPerformanceItemFilteringInterfaces(GameTestHelper helper) {
        testPerformance(helper, "items_filtering_interfaces", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateItemFilteringInterfaces(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    /*
     * Index scaling: the cost of querying the network's ingredient index as it grows.
     */

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_items_index_query")
    public void testPerformanceItemIndexQuery(GameTestHelper helper) {
        testPerformance(helper, "items_index_query", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateItemIndexQuery(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    /*
     * Topology churn: the cost of registering and unregistering positions in the ingredient networks.
     */

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_interfaces_item_append")
    public void testPerformanceItemInterfacesAppend(GameTestHelper helper) {
        testPerformance(helper, "interfaces_item_append", (measureServerTickTimeNow) -> {
            CommandGenerateTunnels.TunnelsGenerationHelper.generateEmptyGrid(helper.getLevel(), helper.absolutePos(START_POS), SIZE);
            addInterfacesPostWarmup(helper, CHURN_CELLS, WARMUP_TICKS);
            // Measure server tick time right after the interfaces have been added
            helper.runAfterDelay(WARMUP_TICKS + CHURN_CELLS, measureServerTickTimeNow);
        });
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_interfaces_item_remove")
    public void testPerformanceItemInterfacesRemove(GameTestHelper helper) {
        testPerformance(helper, "interfaces_item_remove", (measureServerTickTimeNow) -> {
            CommandGenerateTunnels.TunnelsGenerationHelper.generateItemInterfaces(helper.getLevel(), helper.absolutePos(START_POS), SIZE);
            removeInterfacesPostWarmup(helper, CHURN_CELLS, WARMUP_TICKS);
            // Measure server tick time right after the interfaces have been removed
            helper.runAfterDelay(WARMUP_TICKS + CHURN_CELLS, measureServerTickTimeNow);
        });
    }

    /*
     * World-interacting parts: the cost of parts that mutate the world instead of a container.
     */

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_world_block_churn")
    public void testPerformanceWorldBlockChurn(GameTestHelper helper) {
        testPerformance(helper, "world_block_churn", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateWorldBlockChurn(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_world_entityitem_churn")
    public void testPerformanceWorldEntityItemChurn(GameTestHelper helper) {
        testPerformance(helper, "world_entityitem_churn", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generateWorldEntityItemChurn(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    @GameTest(template = TEMPLATE_EMPTY, timeoutTicks = TIMEOUT_TICKS, batch = "performance_player_simulator")
    public void testPerformancePlayerSimulators(GameTestHelper helper) {
        testPerformance(helper, "player_simulator", (measureServerTickTimeNow) ->
                CommandGenerateTunnels.TunnelsGenerationHelper.generatePlayerSimulators(helper.getLevel(), helper.absolutePos(START_POS), SIZE));
    }

    /**
     * Construct the given network, let it warm up, and measure its performance for {@link #EXECUTION_SECONDS}.
     * @param helper The game test helper.
     * @param networkName The name of the network preset, used as benchmark identifier.
     * @param networkConstructor Constructs the network.
     *                           It is passed a runnable that captures the server tick time at the moment it is called,
     *                           which is useful for presets that only cause load during a part of the measurement.
     */
    public static void testPerformance(GameTestHelper helper, String networkName, Consumer<Runnable> networkConstructor) {
        if (!isBenchmarkingEnabled()) {
            IntegratedTunnels.clog(Level.INFO, "Performance benchmarking disabled (PERFORMANCE_BENCHMARK_ENABLED not set)");
            helper.succeed();
            return;
        }

        ensureResultsDirectory();

        // Calculate average server-wide tick time
        Wrapper<Double> avgServerTickTime = new Wrapper<>(0D);
        Runnable measureServerTickTimeNow = () -> avgServerTickTime.set(Stats.meanOf(helper.getLevel().getServer().getTickTimesNanos()) / TimeUtil.NANOSECONDS_PER_MILLISECOND);
        networkConstructor.accept(measureServerTickTimeNow);

        // Measure the network performance
        String measurementId = networkName + "_" + System.currentTimeMillis();
        Wrapper<UUID> measurementUUID = new Wrapper<>();
        helper.runAfterDelay(WARMUP_TICKS, () -> {
            // Wait a few seconds to warm up the code before starting measurement
            measurementUUID.set(NetworkDiagnostics.getInstance().startMeasurementWithoutPlayer(measurementId, EXECUTION_SECONDS));
        });

        // Wait for measurement to complete, then retrieve results
        helper.succeedWhen(() -> {
            if (measurementUUID.get() == null || !NetworkDiagnostics.getInstance().isMeasurementComplete(measurementUUID.get())) {
                throw new GameTestAssertException("Measurement did not complete in time: " + measurementId);
            }

            double avgTickTime = NetworkDiagnostics.getInstance().getMeasurementAverageTickTime(measurementUUID.get());
            NetworkDiagnostics.getInstance().clearMeasurement(measurementUUID.get());

            // Calculate average server-wide tick time
            if (avgServerTickTime.get() == 0D) {
                measureServerTickTimeNow.run();
            }

            List<String> results = new ArrayList<>();
            results.add(String.format("preset=%s size=%d avgNetworkTickTime=%.2f avgServerTickTime=%.2f", networkName, SIZE, avgTickTime, avgServerTickTime.get()));
            writeResults(results, true);

            CommandGenerateTunnels.TunnelsGenerationHelper.clearGrid(helper.getLevel(), helper.absolutePos(START_POS), SIZE);
        });
    }

    private static void ensureResultsDirectory() {
        try {
            Files.createDirectories(Paths.get("logs"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized void writeResults(List<String> results, boolean append) {
        try {
            String content = String.join("\n", results);
            if (append && Files.exists(Paths.get(RESULTS_FILE))) {
                String existingString = Files.readString(Paths.get(RESULTS_FILE));
                content = (existingString.isEmpty() ? content : existingString + content) + "\n";
            }
            Files.write(Paths.get(RESULTS_FILE), content.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add one item interface with a filled chest per tick, starting at the given delay.
     */
    private static void addInterfacesPostWarmup(GameTestHelper helper, int count, int delayOffset) {
        List<BlockPos> cells = CommandGenerateTunnels.TunnelsGenerationHelper.getCells(helper.absolutePos(START_POS), SIZE);
        for (int i = 0; i < Math.min(count, cells.size()); i++) {
            final BlockPos cell = cells.get(i);
            final int index = i;
            helper.runAfterDelay(delayOffset + i, () -> CommandGenerateTunnels.TunnelsGenerationHelper
                    .addItemInterfaceCell(helper.getLevel(), cell, index * CommandGenerateTunnels.TunnelsGenerationHelper.DEFAULT_VARIETY));
        }
    }

    /**
     * Remove one item interface with its chest per tick, starting at the given delay.
     */
    private static void removeInterfacesPostWarmup(GameTestHelper helper, int count, int delayOffset) {
        List<BlockPos> cells = CommandGenerateTunnels.TunnelsGenerationHelper.getCells(helper.absolutePos(START_POS), SIZE);
        for (int i = 0; i < Math.min(count, cells.size()); i++) {
            final BlockPos cell = cells.get(i);
            helper.runAfterDelay(delayOffset + i, () -> CommandGenerateTunnels.TunnelsGenerationHelper
                    .removeCell(helper.getLevel(), cell));
        }
    }
}
