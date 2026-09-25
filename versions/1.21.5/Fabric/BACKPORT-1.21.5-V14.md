# Useful Ores 1.21.5 Backport V14

Base: validated Useful Ores 1.21.6 V13 source.

Targets:
- Minecraft 1.21.5
- Fabric Loader 0.16.10
- Fabric API 0.119.5+1.21.5
- Fabric Loom 1.10.5
- GeckoLib 5.1.0
- Java 21

The 1.21.8/1.21.7 validated spear/render behavior is preserved. The Ancient City loot changes from the discarded V9 experiment are not included.

The Fabric client render-layer registration was explicitly rolled back from 1.21.6 `ChunkSectionLayer` to the 1.21.5 `RenderType` API.
