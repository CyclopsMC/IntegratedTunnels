# Changelog for Minecraft 26.1.2
All notable changes to this project will be documented in this file.

<a name="26.1.2-1.11.0"></a>
## [26.1.2-1.11.0](https://github.com/CyclopsMC/IntegratedTunnels/compare/26.1.2-1.10.0...26.1.2-1.11.0) - 2026-09-05 14:01:46


### Added
* Add match block option to world block importer aspects (#380), Closes #347

### Changed
* Improve overall performance:
  * Avoid constructing block place contexts for unused replaceability checks
  * Improve ingredient predicate hashing and equality performance
  * Improve interface performance by skipping redundant target capability checks

### Fixed
* Fix server crash when a filtered interface ticks while detached from its network (#385), Closes CyclopsMC/IntegratedDynamics#1711
* Fix interfaces not respecting the target side configuration (#382), Closes #381
* Fix grammar in new matchblock aspect property description (#383)

<a name="26.1.2-1.10.0"></a>
## [26.1.2-1.10.0](https://github.com/CyclopsMC/IntegratedTunnels/compare/26.1.2-1.9.6...26.1.2-1.10.0) - 2026-08-24 19:53:22 +0200


### Added
* Add translations through Crowdin (#375)
* Add option to ignore aspect filter during passive interaction (#377), Closes #310
* Add 'Craft' checkbox to export items aspect (#373), Closes #344
* Add export/import item slot option for world item exporters/importers (#372), Closes CyclopsMC/IntegratedDynamics#1560

### Changed
* Auto-save interface settings when closing the gui (#378), Closes #161

### Fixed
* Fix interface channel text in settings being invisible, Closes #379
* Fix memory leak in per-level FakePlayer cache
* Prevent Player Simulator crashes from fake-player dimension transitions (#366), Closes #365

<a name="26.1.2-1.9.6"></a>
## [26.1.2-1.9.6](https://github.com/CyclopsMC/IntegratedTunnels/compare/26.1.2-1.9.5...26.1.2-1.9.6) - 2026-05-22 11:21:27 +0200


### Changed
* Migrate from simulation to transaction logic in IngredientStorageHelpers

<a name="26.1.2-1.9.5"></a>
## [26.1.2-1.9.5](https://github.com/CyclopsMC/IntegratedTunnels/compare/26.1.2-1.9.4...26.1.2-1.9.5) - 2026-04-25 15:53:07 +0200


### Added
* Add translations through Crowdin (#362)

### Fixed
* Fix crash due to null heldItemTransformedTo during exports, Closes #363

<a name="26.1.2-1.9.4"></a>
## [26.1.2-1.9.4] - 2026-04-21 20:01:27 +0200


Initial 26.1.2 release
