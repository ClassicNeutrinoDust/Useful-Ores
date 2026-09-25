# Useful Ores

Minecraft mod by **NeutrinoDust**, maintained as separate Gradle projects for each Minecraft version and mod loader.

## Repository layout

```text
versions/
  <minecraft-version>/
    Fabric/
      build.gradle.kts
      settings.gradle.kts
      gradle.properties (where present)
      src/
      libs/ (where required)
      tools/ (where present)
    NeoForge/
      build.gradle.kts
      settings.gradle.kts
      gradle.properties (where present)
      src/
      libs/ (where required)
      tools/ (where present)
    notes/ (version-level porting/status notes, where present)
```

Each version/loader directory is intentionally an independent Gradle project. Version-specific APIs and mappings are **not merged across projects**. This makes it possible to compare ports and test a target version without overwriting another version's source.

## Included projects

| Minecraft version | Loader | Files | Approx. size |
|---|---|---:|---:|
| `1.21.10` | `Fabric` | 4,926 | 5.8 MiB |
| `1.21.10` | `NeoForge` | 4,756 | 6.4 MiB |
| `1.21.11` | `NeoForge` | 4,731 | 5.6 MiB |
| `1.21.3` | `NeoForge` | 4,366 | 5.7 MiB |
| `1.21.4` | `NeoForge` | 4,765 | 5.7 MiB |
| `1.21.5` | `Fabric` | 4,932 | 5.8 MiB |
| `1.21.6` | `Fabric` | 4,932 | 5.8 MiB |
| `1.21.7-1.21.8` | `NeoForge` | 4,757 | 5.7 MiB |
| `26.1.2` | `Fabric` | 4,726 | 6.2 MiB |
| `26.1.2` | `NeoForge` | 4,726 | 6.2 MiB |
| `26.2` | `Fabric` | 4,724 | 6.2 MiB |
| `26.2` | `NeoForge` | 4,726 | 6.2 MiB |
| `26.3` | `Fabric` | 4,725 | 6.2 MiB |
| `26.3` | `NeoForge` | 4,726 | 5.6 MiB |

**Projects:** 14

## Notes

- The imported source tree contains the Gradle build files, Java/Kotlin source where present, resources/assets/data, local dependency JARs under `libs/`, project tools, and existing porting/status notes.
- No generated `build/` or `.gradle/` directories from the supplied archive are included.
- Existing backup/history files supplied in the archive (for example `*.bak` files) are retained rather than silently deleted.
- No license file was added because none was supplied in the source archive.

## Current version map

- `1.21.10`: `Fabric`, `NeoForge`
- `1.21.11`: `NeoForge`
- `1.21.3`: `NeoForge`
- `1.21.4`: `NeoForge`
- `1.21.5`: `Fabric`
- `1.21.6`: `Fabric`
- `1.21.7-1.21.8`: `NeoForge`
- `26.1.2`: `Fabric`, `NeoForge`
- `26.2`: `Fabric`, `NeoForge`
- `26.3`: `Fabric`, `NeoForge`
