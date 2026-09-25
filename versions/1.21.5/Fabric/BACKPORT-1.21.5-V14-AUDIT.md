# Useful Ores 1.21.5 V14 Port Audit

Base: validated 1.21.6 V13.

Verified changes for Minecraft 1.21.5:
- Fabric Loader 0.16.10
- Fabric API 0.119.5+1.21.5
- Fabric Loom 1.10.5
- GeckoLib 5.1.0 (Fabric 1.21.5)
- Java 21
- resource pack format 55
- all 1.21.6 model format markers changed to 1.21.5
- Fabric block render layer mapping changed from 1.21.6 `ChunkSectionLayer` to 1.21.5 `RenderType`

Preserved from V13:
- spear FPV/TPV combat and animation fixes
- Ancient City loot behavior (discarded V9 loot experiment not included)
- Solarite Furnace / Arcanite XP Jar render transforms
- off-screen/render-distance fixes for custom block-entity renderers
- GeckoLib generic block renderer bounds
- all supplied media assets
