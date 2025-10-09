# Changelog for Minecraft 1.21.1
All notable changes to this project will be documented in this file.

<a name="1.21.1-1.9.0"></a>
## [1.21.1-1.9.0](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.44...1.21.1-1.9.0) - 2025-10-07 07:49:11


### Added
* Add missing PASSIVE_IO property

### Fixed
* Fix passive fluid interaction consuming too much

Internal changes: (required for Integrated Mekanism)
* Make PositionedOperatorIngredientIndex better extensible

<a name="1.21.1-1.8.44"></a>
## [1.21.1-1.8.44](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.43...1.21.1-1.8.44) - 2025-08-08 22:06:22 +0200


### Fixed
* Fix logical flaw in IEnergyTarget

<a name="1.21.1-1.8.43"></a>
## [1.21.1-1.8.43](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.42...1.21.1-1.8.43) - 2025-08-08 21:47:42 +0200


### Fixed
* Fix fluid transfer with Cauldrons not working, Closes #346

<a name="1.21.1-1.8.42"></a>
## [1.21.1-1.8.42](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.41...1.21.1-1.8.42) - 2025-07-30 16:51:04 +0200


### Added
* Add translations through Crowdin (#343)
* Add PT_BR localization (#345)

### Fixed
* Fix Player Simulator dupe with Inventory Pets, Closes #339
* Fix some spelling and grammar typos in lang

<a name="1.21.1-1.8.41"></a>
## [1.21.1-1.8.41](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.40...1.21.1-1.8.41) - 2025-06-16 18:47:03 +0200


### Fixed
* Fix Player Simulator duping placed items, regression due to #339 fix
* Fix player simulator not consuming PASSed items, Closes #339

<a name="1.21.1-1.8.40"></a>
## [1.21.1-1.8.40](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.39...1.21.1-1.8.40) - 2025-05-31 21:39:36 +0200


### Fixed
* Fix aspect icons in Network Reader not loading

<a name="1.21.1-1.8.39"></a>
## [1.21.1-1.8.39](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.38...1.21.1-1.8.39) - 2025-05-31 21:06:31 +0200


### Fixed
* Fix aspect icons in Network Reader not loading

<a name="1.21.1-1.8.38"></a>
## [1.21.1-1.8.38](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.37...1.21.1-1.8.38) - 2025-05-25 07:02:00 +0200


### Fixed
* Fix cursor centering on gui switching, Closes CyclopsMC/IntegratedDynamics#1514

<a name="1.21.1-1.8.37"></a>
## [1.21.1-1.8.37](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.36...1.21.1-1.8.37) - 2025-03-29 14:26:41 +0100


### Added
* Update ru_ru.json (#327)

### Fixed
* Fix fluid lists not always matching correctly, Closes #333
* Fix data components being lost with World Block Exporter, Closes #328

<a name="1.21.1-1.8.36"></a>
## [1.21.1-1.8.36](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.35...1.21.1-1.8.36) - 2025-03-10 07:23:54 +0100


### Fixed
* Fix data components being lost with World Block Exporter, Closes #328

<a name="1.21.1-1.8.35"></a>
## [1.21.1-1.8.35](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.34...1.21.1-1.8.35) - 2025-02-22 17:13:01 +0100


### Fixed
* Fix world block exporter not passing item to use context
  This could lead to issues with certain modded blocks.
  Closes CyclopsMC/IntegratedDynamics#1467

<a name="1.21.1-1.8.34"></a>
## [1.21.1-1.8.34](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.33...1.21.1-1.8.34) - 2025-02-15 10:20:34 +0100


### Added
* Add tr_tr translations through Crowdin (#325)

### Fixed
* Fix broken advancement icons

<a name="1.21.1-1.8.33"></a>
## [1.21.1-1.8.33](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.32...1.21.1-1.8.33) - 2025-02-03 17:02:09 +0100


### Fixed
* Fix Player Simulator not using Brush correctly, Closes #324
* Fix world fluid exporter voiding non-placable fluids, Closes #323
* Fix world offset not being configurable to zero, Closes #322

<a name="1.21.1-1.8.32"></a>
## [1.21.1-1.8.32](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.31...1.21.1-1.8.32) - 2025-01-08 17:28:01 +0100


### Fixed
* Fix chained world block exporter/exporter not able to work every tick
  This is done by making the exporter/importer sleep optimization only
  take effect if these parts were unable to move anything for at least
  three ticks.
  Closes #319
* Disallow Player Simulator from sleeping in beds, Closes CyclopsMC/IntegratedDynamics/issues/1454
* Fix player simulator not ticking fast enough with snowballs, #319

<a name="1.21.1-1.8.31"></a>
## [1.21.1-1.8.31](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.30...1.21.1-1.8.31) - 2025-01-05 15:45:41 +0100


### Fixed
* Fix broken Export Enchantable Items advancement
  Closes CyclopsMC/IntegratedDynamics#1453

<a name="1.21.1-1.8.30"></a>
## [1.21.1-1.8.30](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.29...1.21.1-1.8.30) - 2024-11-22 07:13:29 +0100


### Fixed
* Fix unable to clear part IDs, Closes CyclopsMC/IntegratedTunnels#309

<a name="1.21.1-1.8.29"></a>
## [1.21.1-1.8.29](https://github.com/CyclopsMC/IntegratedTunnels/compare/1.21.1-1.8.28...1.21.1-1.8.29) - 2024-11-19 15:30:59 +0100


### Fixed
* Fix passive import/export ignoring active aspect channel, Closes #308

<a name="1.21.1-1.8.28"></a>
## [1.21.1-1.8.28] - 2024-08-09 21:09:30 +0200


### Fixed
* Fix list aspects considering apples instead of empty, Closes #301
