# Useful Ores 1.21.3 V78 — Beacon Height API Compile Fix

V77 used `Level#getMaxBuildHeight()`, which is not present in the 1.21.3 mappings used by this NeoForge project. Minecraft 1.21.3 renamed that API to `LevelHeightAccessor#getMaxY()`. NeoForge's 1.21.2 migration primer documents the rename and notes that `getMaxY()` is the inclusive top-Y value.

Changed:
- `SuperBeaconBlockEntity`: `level.getMaxBuildHeight()` -> `level.getMaxY()`.
- `SuperBeaconRenderer`: `getMaxBuildHeight()` -> `getMaxY()` in beam height and render bounds.
- Same correction applied to Fabric 1.21.3 sources.

No rendering architecture, item-model graph, culling behavior, beam geometry, or gameplay logic was otherwise changed from V77.
