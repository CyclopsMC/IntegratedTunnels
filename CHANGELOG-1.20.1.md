# Changelog for Minecraft 1.20.1
All notable changes to this project will be documented in this file.

<a name="1.20.1-1.9.1"></a>
## [1.20.1-1.9.1](/compare/1.20.1-1.9.0...1.20.1-1.9.1) - 2025-11-11 14:34:23


### Fixed
* Disallow taming from Player Simulator, Closes CyclopsMC/IntegratedDynamics#1562
* Fix broken Player Simulator item-based aspects
* Fix Player Simulator locking villager trades, Closes #1561
* Fix slot option not working for entities in World Item Exporter/Importer
  Closes CyclopsMC/IntegratedDynamics#1559

<a name="1.20.1-1.9.0"></a>
## [1.20.1-1.9.0](/compare/1.20.1-1.8.42...1.20.1-1.9.0) - 2025-10-07 07:37:24 +0200


### Fixed
* Add missing PASSIVE_IO property
* Fix passive fluid interaction consuming too much

Internal changes (required for Integrated Mekanism):
* Make PositionedOperatorIngredientIndex better extensible

<a name="1.20.1-1.8.42"></a>
## [1.20.1-1.8.42](/compare/1.20.1-1.8.41...1.20.1-1.8.42) - 2025-07-30 16:49:26 +0200


### Fixed
* Fix Player Simulator dupe with Inventory Pets, Closes #339

<a name="1.20.1-1.8.41"></a>
## [1.20.1-1.8.41](/compare/1.20.1-1.8.40...1.20.1-1.8.41) - 2025-06-16 18:44:48 +0200


### Fixed
* Fix Player Simulator duping placed items, regression due to #339 fix

<a name="1.20.1-1.8.40"></a>
## [1.20.1-1.8.40](/compare/1.20.1-1.8.39...1.20.1-1.8.40) - 2025-06-16 17:35:47 +0200


### Fixed
* Fix player simulator not consuming PASSed items, Closes #339

<a name="1.20.1-1.8.39"></a>
## [1.20.1-1.8.39](/compare/1.20.1-1.8.38...1.20.1-1.8.39) - 2025-05-31 21:47:07 +0200


### Fixed
* Fix invalid imports

<a name="1.20.1-1.8.38"></a>
## [1.20.1-1.8.38](/compare/1.20.1-1.8.37...1.20.1-1.8.38) - 2025-05-31 21:43:06 +0200


### Fixed
* Fix invalid imports

<a name="1.20.1-1.8.37"></a>
## [1.20.1-1.8.37](/compare/1.20.1-1.8.36...1.20.1-1.8.37) - 2025-05-31 21:04:59 +0200


### Fixed
* Fix aspect icons in Network Reader not loading

<a name="1.20.1-1.8.36"></a>
## [1.20.1-1.8.36](/compare/1.20.1-1.8.35...1.20.1-1.8.36) - 2025-03-29 14:25:49 +0100


### Fixed
* Fix fluid lists not always matching correctly, Closes #333
* Fix typos in manual

<a name="1.20.1-1.8.35"></a>
## [1.20.1-1.8.35](/compare/1.20.1-1.8.34...1.20.1-1.8.35) - 2025-02-22 17:11:21 +0100


### Fixed
* Fix world block exporter not passing item to use context
  This could lead to issues with certain modded blocks.
  Closes CyclopsMC/IntegratedDynamics#1467

<a name="1.20.1-1.8.34"></a>
## [1.20.1-1.8.34](/compare/1.20.1-1.8.33...1.20.1-1.8.34) - 2025-02-03 17:01:23 +0100


### Fixed
* Fix Player Simulator not using Brush correctly, Closes #324
* Fix world fluid exporter voiding non-placable fluids, Closes #323
* Fix world offset not being configurable to zero, Closes #322

<a name="1.20.1-1.8.33"></a>
## [1.20.1-1.8.33](/compare/1.20.1-1.8.32...1.20.1-1.8.33) - 2025-01-08 17:25:28 +0100


### Fixed
* Fix chained world block exporter/exporter not able to work every tick
  This is done by making the exporter/importer sleep optimization only
  take effect if these parts were unable to move anything for at least
  three ticks.
  Closes #319
* Disallow Player Simulator from sleeping in beds, Closes CyclopsMC/IntegratedDynamics/issues/1454
* Fix player simulator not ticking fast enough with snowballs, #319

<a name="1.20.1-1.8.32"></a>
## [1.20.1-1.8.32](/compare/1.20.1-1.8.31...1.20.1-1.8.32) - 2024-11-19 15:27:28 +0100


### Fixed
* Fix passive import/export ignoring active aspect channel, Closes #308
* Fix list aspects considering apples instead of empty, Closes #301

<a name="1.20.1-1.8.31"></a>
## [1.20.1-1.8.31](/compare/1.20.1-1.8.30...1.20.1-1.8.31) - 2024-08-09 19:18:50 +0200


### Fixed
* Fix list aspects considering apples instead of empty, Closes #301

<a name="1.20.1-1.8.30"></a>
## [1.20.1-1.8.30](/compare/1.20.1-1.8.29...1.20.1-1.8.30) - 2024-08-06 17:25:18 +0200


### Fixed
* Fix Place Logs tutorial not mentioning tags

Closes #299
Closes #298

<a name="1.20.1-1.8.29"></a>
## [1.20.1-1.8.29](/compare/1.20.1-1.8.28...1.20.1-1.8.29) - 2024-07-21 11:47:30 +0200


### Fixed
* Fix rotation not being set in Player Simulator
  This caused issues with items that consider player rotation, such as buckets.
  Closes CyclopsMC/IntegratedDynamics#1357

<a name="1.20.1-1.8.28"></a>
## [1.20.1-1.8.28](/compare/1.20.1-1.8.27...1.20.1-1.8.28) - 2024-05-04 09:18:34 +0200


### Fixed
* Fix list-based aspects only moving one ingredient at a time
  Closes CyclopsMC/IntegratedDynamics#1349
  Related to #286

<a name="1.20.1-1.8.27"></a>
## [1.20.1-1.8.27](/compare/1.20.1-1.8.26...1.20.1-1.8.27) - 2024-04-28 10:13:17 +0200


### Changed
* Optimize list-based ingredient movement
  This makes importers and exporters configured with
  a list aspect make better use of internal indexes
  to significantly reduce server load.
  Closes #286


### Fixed
* Fix predicate-based importing ignoring round-robin, Closes #288
* Fix variable-based offsets not working in interfaces, Closes #289

<a name="1.20.1-1.8.26"></a>
## [1.20.1-1.8.26](/compare/1.20.1-1.8.25...1.20.1-1.8.26) - 2024-04-14 14:13:52 +0200


### Fixed
* Fix rare crash during chunk loading of filtering interfaces, Closes #287

<a name="1.20.1-1.8.25"></a>
## [1.20.1-1.8.25](/compare/1.20.1-1.8.24...1.20.1-1.8.25) - 2024-02-06 19:14:43 +0100


### Fixed
* Fix network reader crash with Integrated Dynamics 1.21.0

<a name="1.20.1-1.8.24"></a>
## [1.20.1-1.8.24](/compare/1.20.1-1.8.23...1.20.1-1.8.24) - 2023-10-05 08:43:06 +0200


### Fixed
* Fix interface offset changes not being applied immediately, Closes #277

<a name="1.20.1-1.8.23"></a>
## [1.20.1-1.8.23](/compare/1.20.1-1.8.22...1.20.1-1.8.23) - 2023-09-15 17:28:23 +0200


### Fixed
* Fix crash when interacting with fluids without sounds, Closes #275

<a name="1.20.1-1.8.22"></a>
## [1.20.1-1.8.22](/compare/1.20.1-1.8.21...1.20.1-1.8.22) - 2023-08-27 11:46:35 +0200


### Fixed
* Fix predicate-based transfer not considering slots, Closes #271
* Fix subnetworks causing increasing numbers of ghost items in terminals, Closes CyclopsMC/IntegratedTerminals#109

<a name="1.20.1-1.8.21"></a>
## [1.20.1-1.8.21](/compare/1.20.1-1.8.20...1.20.1-1.8.21) - 2023-07-08 14:44:31 +0200


### Fixed
* Fix crash when removing invalid filtering interface, Closes #270

<a name="1.20.1-1.8.20"></a>
## [1.20.1-1.8.20] - 2023-07-02 08:11:29 +0200


Initial 1.20.1 release
