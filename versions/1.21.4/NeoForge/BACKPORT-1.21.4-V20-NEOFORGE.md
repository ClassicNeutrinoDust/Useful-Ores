# Useful Ores 1.21.4 Backport V20 — NeoForge

Port target: Minecraft 1.21.4.

Key port work in this revision:
- Base source rebased from the validated 1.21.5 backport.
- GeckoLib migrated from 5.x GeoRenderState/DataTicket APIs to the 4.8.5 API line used by Minecraft 1.21.4.
- Fabric target uses Fabric API 0.114.3+1.21.4.
- NeoForge target uses NeoForge 21.4.140.
- Preserves the previously validated spear rendering/charge behavior, custom render culling fixes, and Sperrylite vial stack/mud-conversion fixes.
- 1.21.4 tooltip signatures are restored to the list-based Item API.

Build command:
```text
gradle clean build
```

This package has been statically audited for JSON validity, ZIP integrity, stale 1.21.5 runtime API markers, and balanced Java braces. A Windows Gradle/Minecraft dependency build still needs to be run on the target environment.
