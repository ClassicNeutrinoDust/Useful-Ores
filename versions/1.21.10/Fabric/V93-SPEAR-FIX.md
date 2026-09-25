# Useful Ores 1.21.10 Spear Fix — V93

## Root cause fixed
The 1.21.11 vanilla implementation is component-driven: `KineticWeapon.damageEntities` runs every server tick while an item is being used, instead of relying on `Item#onUseTick` as the primary dispatch mechanism. V93 mirrors that with a Fabric server-tick dispatcher.

The server-tick path also samples the attacker's actual simulation position once per server tick and converts the displacement to blocks/second. This avoids the 1.21.10 ambiguity between simulation and render-history position fields and reproduces the `getKnownSpeed()*20` input-to-movement basis used by vanilla.

## Exact 1.21.11 values extracted with javap
The supplied 1.21.11 client JAR's obfuscated item registry (`dlp$a.a(dni, FFFFFFFFF)`) constructs the spear kinetic component with:

- contact cooldown: 10 ticks
- wood delay: 15 ticks; stone 14; copper 13; iron 12; gold 10; diamond 10; netherite 8
- forward movement: 0.38
- damage multipliers: wood 0.70, stone 0.82, copper 0.82, iron 0.95, gold 0.70, diamond 1.075, netherite 1.20
- charge damage minimum relative speed: 4.6 blocks/sec
- attack range: min reach 2.0, max reach 4.5, creative min 2.0, creative max 6.5, hitbox margin 0.125, mob factor 0.5
- spear attack component is piercing and deals knockback; the vanilla code calls `LivingEntity#stabAttack` rather than ordinary `hurtServer`.

`doq.a(cgk)` in the same JAR returns movement scaled by 20 and `doq.a(dlt,int,chl,cgv)` computes relative speed as the attacker's projected speed minus the target's projected speed, clamped to zero. Damage is `floor(relative_speed * damage_multiplier)` plus the item's additional attack attribute contribution. Conditions are evaluated independently.

## Combat changes
V93:

1. Removes the spear's dependence on `Item#onUseTick` for charge processing.
2. Runs the kinetic charge dispatcher from `ServerTickEvents.END_SERVER_TICK`.
3. Samples attacker movement directly between server ticks, then projects it onto the view vector.
4. Keeps target movement subtraction and the vanilla 10-tick per-target contact cooldown.
5. Uses the exact vanilla spear attack range and hitbox margin.
6. Filters out entities that cannot be hit by projectile-style ray attacks and same-vehicle targets.
7. Keeps the port's existing material-specific kinetic values extracted from the 1.21.11 item registration bytecode.

## Sounds
The five user-supplied recordings are now bundled:

- `spear_attack_1.ogg` → dedicated jab-start sound
- `spear_attack_hit_1.ogg` → dedicated jab contact sound
- `spear_hit_1.ogg` → dedicated charge contact sound
- `spear_lunge.ogg` → dedicated lunge sound
- `spear_use.ogg` → charge/use sound

The normal jab sound is played at attack initiation, so an air jab also produces it. The contact sound is separate and only fires when an entity is actually struck.

No trident sound is referenced by the V93 spear code.

## Component parity note
Vanilla 1.21.11 registers each spear with `KINETIC_WEAPON`, `PIERCING_WEAPON`, `ATTACK_RANGE`, `MINIMUM_ATTACK_CHARGE`, `USE_EFFECTS`, `DAMAGE_TYPE`, and `SWING_ANIMATION` in addition to its attack attributes and consumable state. Minecraft 1.21.10 lacks those component APIs. V93 therefore emulates the behavior in the existing spear item class, server-tick charge dispatcher, and client animation/attack mixins instead of referencing unavailable 1.21.11 classes.
