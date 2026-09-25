# Useful Ores 2.1.7 — Minecraft 1.21.10 Spear V91

## Deep bytecode findings

The supplied Minecraft 1.21.11 class jar was inspected with `javap` and compared with the 1.21.10 mappings.

The decisive velocity detail is:

- 1.21.11 `KineticWeapon` calls `Entity.getKnownSpeed().scale(20.0)`.
- In 1.21.10, the simulation-history coordinates are `xo`, `yo`, `zo` (`lastX`, `lastY`, `lastZ`).
- `xOld`, `yOld`, `zOld` are render-history coordinates (`lastRenderX/Y/Z`) and must not be used for the spear charge.
- The previous port used the wrong coordinate set, so its speed calculation could fail even while the player visibly moved.

The 1.21.11 `KineticWeapon.damageEntities` bytecode was also checked directly. Its sequence is:

1. elapsed use ticks are reduced by the kinetic delay;
2. attacker look vector is read;
3. attacker `getKnownSpeed()*20` is projected onto that look vector;
4. all entity contacts along the attack range are processed;
5. target known speed is projected onto the same look vector;
6. relative speed is `max(0, attackerSpeed - targetSpeed)`;
7. the three kinetic conditions are tested;
8. damage is the current attack-damage attribute plus `floor(relativeSpeed * damageMultiplier)`;
9. `LivingEntity.stabAttack(...)` is invoked for the contact.

## V91 changes

### Charge attack

- Replaced `getDeltaMovement()` with exact simulation-history speed derived from `getX()-xo`, `getY()-yo`, `getZ()-zo`.
- Uses the current held spear's `ATTACK_DAMAGE` attribute value, preserving the spear's item attribute modifier.
- Preserves the 10-tick target contact cooldown.
- Tests all living entity contacts along the unobstructed spear ray rather than selecting only the nearest entity.
- Uses 2.0 minimum reach and 4.5 maximum reach for normal players.
- Uses 6.5 maximum reach for creative players, matching the inspected 1.21.11 spear range data.
- Keeps the 0.125 hitbox margin.
- Computes target movement with the same known-speed rule, including root-vehicle speed for non-player passengers.
- Uses the spear-specific hit sound for kinetic contact.
- Charge state is reset at the end of item use.

### Right-click/use sound

- Removed every trident sound reference from spear code.
- Right-click uses `useful_ores:item.spear.use` only from the server, preventing integrated-server double playback.

### Jab sound

- Removed jab audio from the `Player.attack()` path as the only source.
- Entity jab contacts use the dedicated `item.spear.hit` event.
- Air/block jab attempts are detected from the `Minecraft.startAttack()` client entry point and play `item.spear.attack` even when no entity is hit.

### Jab animation

- Removed the old approximate inverse of the generic sword swing.
- Added a `setupAttackAnimation` cancellation for spears so the regular sword/axe attack layer never runs first.
- Applies the dedicated spear stab pose directly.
- The item-layer stab transform now stops explicitly at the final attack frame to prevent a residual end-frame transform.

## Dedicated sound resources

Because the supplied Minecraft class jars do not contain Mojang's binary OGG assets, V91 does not pretend the bundled audio is Mojang's original recording. It registers the same dedicated spear event structure (`item.spear.use`, `item.spear.attack`, `item.spear.hit`) and includes short, non-trident spear-like OGG assets under the Useful Ores namespace.

## Validation

- `TRIDENT` references in spear implementation: 0.
- `SpearChargeCombat` references to `getDeltaMovement`: 0.
- Charge speed source: `xo/yo/zo` simulation history.
- `sounds.json`: valid JSON.
- All three spear OGG files exist and decode as Vorbis.
- Mixin config includes the new client `Minecraft` sound hook.
- The generic humanoid spear attack is cancelled before the dedicated stab pose.

## Build note

The project was not compiled in this environment because there is no Gradle executable or Gradle wrapper bundled with the supplied V89 project. The source/resources were statically audited and packaged for the user's normal `gradle build` environment.
