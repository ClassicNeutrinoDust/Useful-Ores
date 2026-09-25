# Useful Ores V94 — spear backport fix

## Build errors fixed

1. `SpearItem.releaseUsing(...)` now returns `boolean`, matching Minecraft 1.21.10's `Item` API, and returns `super.releaseUsing(...)` after ending spear state.
2. `UsefulOresMod` now imports `ModSpearSounds`, fixing the missing symbol during compilation.

## Charge-path correction

The 1.21.11 client JAR bytecode (`doq`) shows the kinetic weapon tick is processed from `LivingEntity` every tick. Its speed helper uses the simulation position delta and multiplies it by 20:

`(currentX - previousX, currentY - previousY, currentZ - previousZ) * 20`

The V94 emulation now calls that same calculation directly for the attacker instead of maintaining an independent movement sampler that started with a zero-speed tick.

The target speed is calculated using the same helper. Relative speed is projected onto the attacker's view vector and clamped at zero, then the damage conditions and effective ATTACK_DAMAGE value are applied.

The projectile-like line test remains the 1.21.10-compatible replacement for the missing 1.21.11 AttackRange component.

## Sound behavior

The five supplied spear OGGs are used:

- `attack_1.ogg` — jab initiation
- `attack_hit_1.ogg` — jab entity contact
- `use_1.ogg` — spear use start
- `lunge.ogg` — release/lunge
- `hit_1.ogg` — charge contact

No trident sound references remain.

The client jab sound is registered through the client mixin, while the contact sound is handled separately after entity attack processing.

## Data

`data/minecraft/tags/item/spears.json` contains the Useful Ores spear IDs with `replace: false`.

## Validation

All JSON resources parse successfully. The project source contains no `trident` references. The five supplied OGG resources are present in the archive.

A full Gradle compilation could not be executed in this environment because Gradle/the project wrapper is unavailable here. The V94 changes specifically address the three compiler errors reported from the user's local Gradle 1.21.10 environment.
