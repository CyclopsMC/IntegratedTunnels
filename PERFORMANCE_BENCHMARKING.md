# Performance Benchmarking Setup

This document describes the performance benchmarking infrastructure for Integrated Tunnels,
which measures the continuous performance of tunnel operations on Integrated Dynamics networks.
Performance results are tracked in https://github.com/CyclopsMC/cyclops-performance-results

This setup mirrors the one of [Integrated Dynamics](https://github.com/CyclopsMC/IntegratedDynamics/blob/master-1.21-lts/PERFORMANCE_BENCHMARKING.md),
and reuses its measurement infrastructure.
Where Integrated Dynamics measures the cost of the network graph and variable evaluation,
these benchmarks measure the cost of what Integrated Tunnels adds on top of it:
ingredient observation, ingredient transfer, index querying, and world interaction.

## Overview

The performance benchmarking system consists of three main components:

1. **GitHub Workflow** (`.github/workflows/performance.yml`)
   - Executes on the same triggers as CI (push and pull_request)
   - Runs game tests to measure network performance
   - Uses `benchmark-action/github-action-benchmark` to track performance evolution

2. **Game Tests** (`src/main/java/org/cyclops/integratedtunnels/gametest/GameTestsPerformance.java`)
   - Generates networks with different presets for benchmarking
   - Measures performance metrics for each preset
   - Writes results to `runs/gameTestServer/logs/benchmark_results.txt`

3. **Network Generation Command** (`src/main/java/org/cyclops/integratedtunnels/command/CommandGenerateTunnels.java`)
   - Provides `/integratedtunnels generatetunnels` command for manual testing
   - Supports all presets that the benchmarks use, plus `clear`
   - Can be used in both single-player and multiplayer environments

## Grid Layout

All presets are built on top of the same grid layout, so that their results are comparable:

- Even Y levels are fully filled with logic cables.
- Odd Y levels alternate between logic cables and free **cells**.

Every cell is a free position that is fully surrounded by cables of a single network.
This means a cell can hold a container that is observed by an interface on the cable below it,
and that is simultaneously targeted by importers or exporters on the cables next to it.
A grid of size 9 (the size used in the benchmarks, which is the largest that fits in the game test template)
contains 569 cables and 160 cells.

## Network Presets

### Storage observation

These presets isolate the cost of the ingredient observers that watch every container
that an interface exposes to the network.
No ingredients are moved: this is the idle cost of simply having storage attached to a network.

| Preset | Description |
| --- | --- |
| `interfaces_item_idle` | Every cell holds a chest with 9 distinct item types, exposed by an item interface |
| `interfaces_fluid_idle` | Every cell holds a filled drying basin, exposed by a fluid interface |
| `interfaces_energy_idle` | Every cell holds a filled energy battery, exposed by an energy interface |
| `interfaces_item_idle_deep` | Only 64 cells, but with completely filled chests, which shifts the cost from the number of observed positions to the number of observed slots: 1728 observed slots across 64 positions, against 1440 slots across 160 positions for `interfaces_item_idle` |

### Active transfer

These presets continuously move ingredients around.
Every other cell is network storage, and the remaining cells run an exporter that pushes
ingredients out of the network into the cell, plus an importer that pulls them back in.

| Preset | Description |
| --- | --- |
| `items_transfer` | Items are continuously exported and imported again |
| `fluids_transfer` | Fluids are continuously exported and imported again |
| `energy_transfer` | Energy is continuously exported and imported again |
| `items_transfer_predicate` | As `items_transfer`, but driven by predicate aspects, which additionally measures the cost of evaluating a predicate per candidate ingredient. The predicate matches on the raw item, because itemstack equality also compares stack sizes |
| `items_filtering_interfaces` | As `items_transfer`, but with filtering item interfaces as network storage |

### Index scaling

| Preset | Description |
| --- | --- |
| `items_index_query` | Three quarters of the cells hold chests with distinct item types, resulting in a large network index. The remaining cells continuously query one specific itemstack out of that index and import it back |

### Topology churn

| Preset | Description |
| --- | --- |
| `interfaces_item_append` | Starts from a cable-only grid, and adds one item interface with a filled chest per tick after warming up, measuring the cost of registering positions in the ingredient networks |
| `interfaces_item_remove` | Starts from a fully populated grid, and removes one item interface with its chest per tick after warming up, measuring the cost of unregistering positions and splitting networks |

### World-interacting parts

| Preset | Description |
| --- | --- |
| `world_block_churn` | Block exporters continuously place blocks into the cells, while block importers break them again |
| `world_entityitem_churn` | Entity item exporters continuously drop items into the cells, while entity item importers pick them up again |
| `player_simulator` | Player simulators continuously simulate right-clicks |

## Performance Metrics

The benchmarking system measures two key metrics:

- **Average Network Tick Time (ms)**: The average time the Integrated Dynamics network subsystem takes
  to process one game tick. This is the sum of the time spent in the network's parts
  (which for these benchmarks are mostly Integrated Tunnels parts)
  and the time spent in the ingredient observers of the item, fluid and energy channels.
- **Average Server Tick Time (ms)**: The average time the entire Minecraft server takes per game tick.
  This measures the overall server performance impact, including both the network and all other server operations.
- **Network Size**: The edge length of the generated grid

These metrics are tracked separately in the benchmark results to distinguish between network-specific
performance and overall server performance impact.

## Game Test Execution

The game tests are automatically executed as part of the GitHub workflow:

```bash
PERFORMANCE_BENCHMARK_ENABLED=true ./gradlew runGameTestServer
```

This command:
1. Starts a game test server
2. Runs `GameTestsPerformance` game tests
3. Generates networks with different presets
4. Measures performance metrics
5. Writes results to `runs/gameTestServer/logs/benchmark_results.txt`

When `PERFORMANCE_BENCHMARK_ENABLED` is not set, all of these game tests succeed immediately without
generating or measuring anything, so that regular `./gradlew runGameTestServer` runs are not slowed down.

## Result Format

Results are written in the following format:
```
preset=interfaces_item_idle size=9 avgNetworkTickTime=6.25 avgServerTickTime=3.50
preset=items_transfer size=9 avgNetworkTickTime=7.50 avgServerTickTime=4.20
```

Results are then converted to JSON format for the benchmark action.
Each preset generates two metrics - one for network tick time and one for server tick time:
```json
[
  {
    "name": "NETWORK LOAD: interfaces_item_idle_size_9",
    "unit": "tick time (ms)",
    "value": 6.25
  },
  {
    "name": "SERVER LOAD: interfaces_item_idle_size_9",
    "unit": "tick time (ms)",
    "value": 3.50
  }
]
```

## Benchmark Tracking

The `benchmark-action/github-action-benchmark` GitHub action automatically:
- Stores benchmark results in the `CyclopsMC/IntegratedTunnels/<branch>/benchmarks` directory
  of the `CyclopsMC/cyclops-performance-results` repository
  (note the extra repository name segment compared to Integrated Dynamics,
  which is needed because both mods share branch names)
- Generates historical performance charts
- Alerts when performance degrades beyond 250% of baseline
- Creates comments on commits and PRs when alerts are triggered

## Manual Testing

To manually test tunnel performance in a Minecraft world:

1. **Generate a network of item interfaces**:
   ```
   /integratedtunnels generatetunnels iteminterfaces 9
   ```

2. **Generate a network that continuously transfers items**:
   ```
   /integratedtunnels generatetunnels itemtransfer 9
   ```

3. **Measure network performance** (provided by Integrated Dynamics):
   ```
   /integrateddynamics networkdiagnostics measure 10
   ```

4. **Clear generated networks**:
   ```
   /integratedtunnels generatetunnels clear 50
   ```

Note that the generation is `O(size^3)`, and that every cell adds a container that is observed every tick,
so sizes much larger than 9 will quickly become very heavy.

## Integration with CI/CD

The performance workflow runs on:
- Every push to any `master*` or `feature*` branch
- Every pull request

Performance degradation is tracked across commits and branches, helping to identify performance regressions
early in the development cycle.

## Adding New Benchmarks

To add new network presets or benchmarks:

1. Add a new preset enum value in `CommandGenerateTunnels.TunnelsPreset`
2. Add a corresponding generation method in `CommandGenerateTunnels.TunnelsGenerationHelper`,
   and dispatch to it from `TunnelsGenerationHelper.generate`
3. Add a new `@GameTest` method in `GameTestsPerformance`, with a unique `batch` name,
   so that the benchmark runs in isolation from the other benchmarks
4. The workflow will automatically execute and track the new benchmark
