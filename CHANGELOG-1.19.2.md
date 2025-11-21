# Changelog for Minecraft 1.19.2
All notable changes to this project will be documented in this file.

<a name="1.19.2-1.8.43"></a>
## [1.19.2-1.8.43](/compare/1.19.2-1.8.42...1.19.2-1.8.43) - 2025-11-21 15:23:40


### Changed
* Optimize predicate-based transfer using slotless index

By default, predicate-based aspects used the slot-based index to be able
to handle cases such as #271. Since slot-based transfer can be a lot
slower than slotless transfer, this is not great.
Since the use case of #271 is quite exotic, the slot-based behaviour
has been hidden behind an aspect property, while the predicate-based
aspects will now always use the more performant slotless index by
default.

Closes CyclopsMC/ColossalChests#192

<a name="1.19.2-1.8.42"></a>
## [1.19.2-1.8.42](/compare/1.19.2-1.8.41...1.19.2-1.8.42) - 2025-11-11 14:43:00 +0100


### Fixed
* Fix broken item entity import/export

<a name="1.19.2-1.8.41"></a>
## [1.19.2-1.8.41](/compare/1.19.2-1.8.40...1.19.2-1.8.41) - 2025-11-11 14:32:50 +0100


### Fixed
* Disallow taming from Player Simulator, Closes CyclopsMC/IntegratedDynamics#1562
* Fix broken Player Simulator item-based aspects
* Fix Player Simulator locking villager trades, Closes #1561
* Fix slot option not working for entities in World Item Exporter/Importer
  Closes CyclopsMC/IntegratedDynamics#1559

<a name="1.19.2-1.8.40"></a>
## [1.19.2-1.8.40](/compare/1.19.2-1.8.39...1.19.2-1.8.40) - 2025-07-30 16:48:31 +0200


### Fixed
* Fix Player Simulator dupe with Inventory Pets, Closes #339

<a name="1.19.2-1.8.39"></a>
## [1.19.2-1.8.39](/compare/1.19.2-1.8.38...1.19.2-1.8.39) - 2025-06-16 18:44:14 +0200


### Fixed
* Fix Player Simulator duping placed items, regression due to #339 fix

<a name="1.19.2-1.8.38"></a>
## [1.19.2-1.8.38](/compare/1.19.2-1.8.37...1.19.2-1.8.38) - 2025-06-16 17:34:56 +0200


### Fixed
* Fix player simulator not consuming PASSed items, Closes #339

<a name="1.19.2-1.8.37"></a>
## [1.19.2-1.8.37](/compare/1.19.2-1.8.36...1.19.2-1.8.37) - 2025-05-31 21:42:32 +0200


### Fixed
* Fix invalid imports

<a name="1.19.2-1.8.36"></a>
## [1.19.2-1.8.36](/compare/1.19.2-1.8.35...1.19.2-1.8.36) - 2025-05-31 21:03:32 +0200


### Fixed
* Fix aspect icons in Network Reader not loading

<a name="1.19.2-1.8.35"></a>
## [1.19.2-1.8.35](/compare/1.19.2-1.8.34...1.19.2-1.8.35) - 2025-03-29 14:25:23 +0100


### Fixed
* Fix fluid lists not always matching correctly, Closes #333
* Fix typos in manual

<a name="1.19.2-1.8.34"></a>
## [1.19.2-1.8.34](/compare/1.19.2-1.8.33...1.19.2-1.8.34) - 2025-02-22 17:10:41 +0100


### Fixed
* Fix world block exporter not passing item to use context
  This could lead to issues with certain modded blocks.
  Closes CyclopsMC/IntegratedDynamics#1467

<a name="1.19.2-1.8.33"></a>
## [1.19.2-1.8.33](/compare/1.19.2-1.8.32...1.19.2-1.8.33) - 2025-02-03 17:00:24 +0100


### Fixed
* Fix world fluid exporter voiding non-placable fluids, Closes #323
* Fix world offset not being configurable to zero, Closes #322

<a name="1.19.2-1.8.32"></a>
## [1.19.2-1.8.32](/compare/1.19.2-1.8.31...1.19.2-1.8.32) - 2025-01-08 17:23:19 +0100


### Fixed
* Fix chained world block exporter/exporter not able to work every tick
  This is done by making the exporter/importer sleep optimization only
  take effect if these parts were unable to move anything for at least
  three ticks.
  Closes #319
* Disallow Player Simulator from sleeping in beds, Closes CyclopsMC/IntegratedDynamics/issues/1454
* Fix player simulator not ticking fast enough with snowballs, #319

<a name="1.19.2-1.8.31"></a>
## [1.19.2-1.8.31](/compare/1.19.2-1.8.30...1.19.2-1.8.31) - 2024-11-19 15:18:33 +0100


### Fixed
* Fix passive import/export ignoring active aspect channel, Closes #308

<a name="1.19.2-1.8.30"></a>
## [1.19.2-1.8.30](/compare/1.19.2-1.8.29...1.19.2-1.8.30) - 2024-08-09 19:20:24 +0200


### Fixed
* Fix list aspects considering apples instead of empty, Closes #301

<a name="1.19.2-1.8.29"></a>
## [1.19.2-1.8.29](/compare/1.19.2-1.8.28...1.19.2-1.8.29) - 2024-08-06 17:24:33 +0200


### Fixed
* Fix Place Logs tutorial not mentioning tags

Closes #299
Closes #298

<a name="1.19.2-1.8.28"></a>
## [1.19.2-1.8.28](/compare/1.19.2-1.8.27...1.19.2-1.8.28) - 2024-07-21 11:37:47 +0200


### Fixed
* Fix rotation not being set in Player Simulator
  This caused issues with items that consider player rotation, such as buckets.
  Closes CyclopsMC/IntegratedDynamics#1357

<a name="1.19.2-1.8.27"></a>
## [1.19.2-1.8.27](/compare/1.19.2-1.8.26...1.19.2-1.8.27) - 2024-05-04 09:17:26 +0200


### Fixed
* Fix list-based aspects only moving one ingredient at a time
  Closes CyclopsMC/IntegratedDynamics#1349
  Related to #286

<a name="1.19.2-1.8.26"></a>
## [1.19.2-1.8.26](/compare/1.19.2-1.8.25...1.19.2-1.8.26) - 2024-04-28 10:09:20 +0200


### Changed
* Optimize list-based ingredient movement
  This makes importers and exporters configured with
  a list aspect make better use of internal indexes
  to significantly reduce server load.
  Closes #286

### Fixed
* Fix predicate-based importing ignoring round-robin, Closes #288
* Fix variable-based offsets not working in interfaces, Closes #289

<a name="1.19.2-1.8.25"></a>
## [1.19.2-1.8.25](/compare/1.19.2-1.8.24...1.19.2-1.8.25) - 2024-04-14 14:11:08 +0200


### Fixed
* Fix rare crash during chunk loading of filtering interfaces, Closes #287

<a name="1.19.2-1.8.24"></a>
## [1.19.2-1.8.24](/compare/1.19.2-1.8.23...1.19.2-1.8.24) - 2024-02-06 19:13:45 +0100


### Fixed
* Fix network reader crash with Integrated Dynamics 1.21.0

<a name="1.19.2-1.8.23"></a>
## [1.19.2-1.8.23](/compare/1.19.2-1.8.22...1.19.2-1.8.23) - 2023-10-05 08:41:39 +0200


### Fixed
* Fix interface offset changes not being applied immediately, Closes #277

<a name="1.19.2-1.8.22"></a>
## [1.19.2-1.8.22](/compare/1.19.2-1.8.21...1.19.2-1.8.22) - 2023-09-15 17:27:09 +0200


### Fixed
* Fix crash when interacting with fluids without sounds, Closes #275

<a name="1.19.2-1.8.21"></a>
## [1.19.2-1.8.21](/compare/1.19.2-1.8.20...1.19.2-1.8.21) - 2023-08-27 11:39:13 +0200


### Fixed
* Fix predicate-based transfer not considering slots, Closes #271
* Fix subnetworks causing increasing numbers of ghost items in terminals, Closes CyclopsMC/IntegratedTerminals#109

<a name="1.19.2-1.8.20"></a>
## [1.19.2-1.8.20](/compare/1.19.2-1.8.19...1.19.2-1.8.20) - 2023-07-08 14:42:38 +0200


### Fixed
* Fix crash when removing invalid filtering interface, Closes #270

<a name="1.19.2-1.8.19"></a>
## [1.19.2-1.8.19](/compare/1.19.2-1.8.18...1.19.2-1.8.19) - 2023-05-21 10:12:07 +0200


### Fixed
* Add missing offset controls for interfaces, Closes #268

<a name="1.19.2-1.8.18"></a>
## [1.19.2-1.8.18](/compare/1.19.2-1.8.17...1.19.2-1.8.18) - 2022-12-11 13:52:08 +0100


### Fixed
* Fix localization and serialization of Network Count Of Item/Fluid operators, Closes CyclopsMC/IntegratedDynamics#1235

<a name="1.19.2-1.8.17"></a>
## [1.19.2-1.8.17](/compare/1.19.2-1.8.16...1.19.2-1.8.17) - 2022-10-26 10:38:32 +0200


### Fixed
* Fix item cooldowns not ticking in player simulator, Closes #259

<a name="1.19.2-1.8.16"></a>
## [1.19.2-1.8.16](/compare/1.19.2-1.8.15...1.19.2-1.8.16) - 2022-09-17 12:20:54 +0200


### Fixed
* Fix sword and stone advancements triggering too early, Closes #258

<a name="1.19.2-1.8.15"></a>
## [1.19.2-1.8.15](/compare/1.19.2-1.8.14...1.19.2-1.8.15) - 2022-08-29 17:23:39 +0200


### Fixed
* Fix filtering interfaces breaking when modifying chained predicates
  Closes CyclopsMC/IntegratedDynamics#1207

<a name="1.19.2-1.8.14"></a>
## [1.19.2-1.8.14] - 2022-08-11 19:48:18 +0200


Update to MC 1.19.2
