# Useful Ores 1.21.4 Backport V21 — NeoForge

Target: Minecraft 1.21.4

This build is a focused 1.21.4 API backport from the validated 1.21.5 baseline.

Key compatibility changes:
- GeckoLib 4.8.5 1.21.4 NeoForge artifact.
- Replaced the 1.21.5+ SavedDataType usage with 1.21.4 SavedData.Factory.
- Added NbtCompat helpers for 1.21.4 CompoundTag/ListTag access and codec serialization.
- Replaced 1.21.5+ InsideBlockEffectApplier entityInside signatures with the 1.21.4 form.
- Ported Super Beacon to 1.21.4 BeaconBlockEntity beam-section/data-component APIs.
- Ported 1.21.4 tool/armor property APIs and MobEffects holder names.
- Preserves previously validated spear behavior, block-entity culling fixes, and Sperrylite vial stack/dirt-to-mud behavior.

Build:
    gradle clean build

Runtime validation still required after compilation, especially GeckoLib blocks/items, Super Beacon, SavedData persistence, and Sperrylite vial behavior.
