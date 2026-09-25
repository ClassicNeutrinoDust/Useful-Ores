# Useful Ores
<img width="897" height="285" alt="image" src="https://github.com/user-attachments/assets/aa773b0a-19de-43cf-b489-ef22c0f18c99" />

Useful Ores expands the normal ores-and-gear formula with custom resources, equipment, redstone systems, machines, magic, alchemy, world-generation features, and utility mechanics.

**Current release line:** `2.1.7`  
**Author:** NeutrinoDust  
**Environment:** Client and server  
**Loaders:** Fabric, NeoForge  
**Required dependency:** GeckoLib  

## What it adds

- **Custom ore and material families:** Multiple custom resource families with tools, weapons, armor, blocks, and recipes.
- **Wireless redstone:** Wireless redstone relay/linking systems for remote redstone connections.
- **Logic and redstone devices:** Logic gates, redstone clock components, switches, and related utility blocks.
- **Magic and special projectiles:** Magic staffs, projectile entities, meteor/bomb mechanics, and dark-wormhole content.
- **Advanced utility machines:** Solarite furnaces, solar batteries, XP jars, filters, locks, and other automation-oriented blocks.
- **Rails and minecarts:** Specialized rail systems plus Solarite-powered/battery minecart content.
- **Brewing and vials:** Sperrylite catalytic vials, splash/lingering variants, fuel, and brewing integrations.
- **Specialized gear:** Chisels, arrows, keys/locks, Farseeker tools, and material-specific equipment effects.
- **World and environmental mechanics:** Custom ore generation, lighting/flame effects, bioluminescence, blast resistance, and world structures.
- **In-game compendium:** A built-in compendium/book system for mod content and discoveries.

## Ores and world generation

The current public project description documents the following ore/material generation table:

| Ore / material | Dimension | Y-level / location | Spawn rate |
| --- | --- | --- | --- |
| Arcanite | Overworld | -64 to -32 | 4 veins/chunk |
| Chromite | Overworld | -32 to 0 | 4 veins/chunk |
| Enderium | End | 0 to 120 | 4 veins/chunk |
| Extruding Crystal | Overworld | -64 to 16 | 1 vein/chunk, 50% chance |
| Fulgurite | Overworld | -32 to 0 | 4 veins/chunk |
| Ilmenite | Overworld | -64 to 16 | 7 veins/chunk |
| Nyxium | Overworld | -64 to 16 / -64 to -48 variants | 7 + 2 + 1 veins/chunk |
| Osmium | Overworld | -32 to 0 | 4 veins/chunk |
| Phosgene | Overworld | -32 to 64 | 10 veins/chunk |
| Scheelite | Overworld | 0 to 64 | 8 veins/chunk |
| Solarite | Nether | 8 to 119 | 1 vein/chunk |
| Sperrylite | Overworld | 0 to 64 | 6 veins/chunk |
| Zephyrite | Overworld | -16 to 112 | 14 veins/chunk |
| Argentite | Overworld | 0 to 64 | 8 veins/chunk |
| Lonsdaleite | Overworld | Meteorite | 1 vein/meteorite |
| Voidshard | End | 0 to 20 | 1 vein every 5 chunks; 20% chance |
| Painite | Nether | 8 to 119 | 7 veins/chunk |

## Notable items and systems

### Combat and utility

- Arcanite, Nyxium, and Scheelite arrows with distinct effects.
- Scheelite Chisel for converting eligible blocks between block, stair, and slab forms.
- Meteor Staff with explosive/fire effects and terrain transformation.
- Nyxiumnite Staff linked to Ancient Pedestals and wormhole-style mob teleportation.

### Machines and storage

- Solarite Battery and Solarite Furnace.
- Solarite Battery Minecart.
- Titanium Lock and Titanium Key.
- Arcanite XP Jar.
- Argentite Filter for item sorting.
- Wireless Relay with multiple operating modes.
- Redstone Clock with configurable tick frequencies.
- Logic gates: AND, NAND, OR, NOR, XNOR, and NOT.

### Alchemy and special materials

- Sperrylite Catalytic Vial, Splash Vial, and Lingering Vial.
- Infused Coal variants for many ore families.
- Subspace and Attributed Subspace materials.
- Meteorite and Nyxiumnite-related crafting components.
- Farseeker and Extruding Crystal materials.
- Phosgene Potion Powder and Lonsdaleite Layer Glue utility.

### World and environmental mechanics

- Colored ore fire and campfire variants.
- Bioluminescence and special lighting effects.
- Blast-resistant and fire-resistant material mechanics.
- Meteorite structures and Ancient Pedestal content.

## Supported versions in this source repository

| Loader | Minecraft versions |
| --- | --- |
| Fabric | `1.21.3`, `1.21.4`, `1.21.5`, `1.21.6`, `1.21.7-1.21.8`, `1.21.10`, `1.21.11`, `26.1.2`, `26.2`, `26.3` |
| NeoForge | `1.21.3`, `1.21.4`, `1.21.5`, `1.21.6`, `1.21.7-1.21.8`, `1.21.10`, `1.21.11`, `26.1.2`, `26.2`, `26.3` |

Each version is an independent Gradle project. Loader-specific source is kept separate so version-specific APIs, mappings, mixins, and dependencies can evolve independently.

## Repository layout

Every supported loader/version is kept as an independent Gradle project.

```text
Useful-Ores/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
│
├── fabric/
│   ├── 1.21.3/
│   ├── 1.21.4/
│   ├── 1.21.5/
│   ├── 1.21.6/
│   ├── 1.21.7-1.21.8/
│   ├── 1.21.10/
│   ├── 1.21.11/
│   ├── 26.1.2/
│   ├── 26.2/
│   └── 26.3/
│
└── neoforge/
    ├── 1.21.3/
    ├── 1.21.4/
    ├── 1.21.5/
    ├── 1.21.6/
    ├── 1.21.7-1.21.8/
    ├── 1.21.10/
    ├── 1.21.11/
    ├── 26.1.2/
    ├── 26.2/
    └── 26.3/
```

Each loader/version directory is self-contained and contains its own Gradle project files, `src/`, and `libs/` when local libraries are required.

## Root Gradle dispatcher

The root Gradle project does not merge the Fabric and NeoForge projects. Instead, it dispatches the command to the selected version/loader project and runs that project's own Gradle build.

### List all available builds

```bash
gradle listBuilds
```

### Build a specific target

Generic form:

```bash
gradle build -Ploader=fabric -PmcVersion=26.3
```

```bash
gradle build -Ploader=neoforge -PmcVersion=26.3
```

The dispatcher prints the exact Minecraft version, loader, project directory, and task being executed.

### Convenience tasks

Examples:

```bash
gradle buildFabric263
gradle buildNeoForge263

gradle buildFabric2612
gradle buildNeoForge2612

gradle buildFabric2111
gradle buildNeoForge2111

gradle buildFabric1213
gradle buildNeoForge1213
```

The same pattern is available for every loader/version listed above.

## Development

To work on a particular target, enter that project's directory and use its own Gradle project directly.

Example:

```bash
cd fabric/26.3
gradle build
```

or:

```bash
cd neoforge/26.3
gradle build
```

This keeps loader-specific dependencies and mappings isolated.

## Dependency note

Useful Ores requires GeckoLib. Install the GeckoLib build matching your Minecraft version and loader. Modrinth and CurseForge list the project for both Fabric and NeoForge and document GeckoLib as a required dependency. 

## Downloads

- [Modrinth](https://modrinth.com/mod/useful-ores)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/useful-ores-2)
- [GitHub source](https://github.com/ClassicNeutrinoDust/Useful-Ores)

## Release line

The public 2.1.7 files include builds for Minecraft 1.21.3 through 1.21.8, 1.21.10–1.21.11, 26.1.2, 26.2, and 26.3, across Fabric and NeoForge. The 26.3 release is available for both loaders. 
