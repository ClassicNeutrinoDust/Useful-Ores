# Useful Ores V92 — spear charge/tag correction

## Root cause found
The mod used `#minecraft:spears` as the gate for spear logic, but the port did not ship a `data/minecraft/tags/item/spears.json` containing the mod's spear items. Minecraft 1.21.11's own tag lists its vanilla spear items. Consequently the V91 charge loop could exit at its first tag check for every Useful Ores spear.

## Reference checked
The supplied 1.21.11 merged JAR contains:
- `data/minecraft/tags/item/spears.json`
- `data/minecraft/damage_type/spear.json`
- the spear implementation bytecode used for the kinetic-weapon path.

The supplied 1.21.10 JAR has no vanilla spear tag because spears are not a 1.21.10 native weapon.

## V92 correction
Added `data/minecraft/tags/item/spears.json` with `replace:false` and all 17 Useful Ores spear IDs. This extends rather than replaces the vanilla tag when present.

The existing V91 deep fixes remain:
- charge speed uses simulation tick position delta (`x-xo`, `y-yo`, `z-zo`) scaled by 20, matching the 1.21.11 `getKnownSpeed().scale(20)` contract;
- charge speed is projected onto the look vector;
- target speed is subtracted from attacker speed;
- charge damage uses the attack-damage attribute plus `floor(relativeSpeed * multiplier)`;
- contact cooldown is 10 ticks;
- spear hit sound uses a dedicated Useful Ores spear event rather than legacy weapon/trident sounds;
- air jab sound is injected at `Minecraft.startAttack`, so it does not depend on an entity hit;
- the generic humanoid attack swing is cancelled while the dedicated spear stab pose is applied, avoiding end-frame transform compensation.

## Audio limitation
The supplied Minecraft 1.21.11 merged JAR is a class/data JAR and contains no `.ogg` sound payloads. The dedicated Useful Ores sound events therefore use bundled mod audio, not copied Mojang audio. The event names match the 1.21.11 spear sound contract.

## Build note
No Gradle executable or wrapper is present in the supplied project/environment, so V92 was not compiled here. ZIP integrity and JSON syntax were checked.
