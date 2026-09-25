# V99 — Spear architecture replacement

V99 separates the spear into three paths: vanilla block mining, dedicated quick jab, and an authoritative kinetic charge state.

## Root causes

- The V98 jab hook added sound/cooldown behavior but still let `Minecraft.startAttack()` reach generic `Player#attack()`. The actual 1.21.11 spear uses a dedicated PiercingWeapon attack dispatch instead.
- The first-person attack renderer was still fed live equip progress. The uploaded log shows entity/air jab frames cycling through equip progress, while the user's known-good block case settles at zero.
- The charge server loop required `isUsingItem()`. The log shows server `activeUses=1`, then `activeUses=0` while the client remains `usingItem=true`; that is a direct state-desynchronization failure.

## V99 changes

- Block clicks are untouched; vanilla mining remains the reference pose.
- Spear entity/air left-clicks are intercepted before generic `Player#attack()`.
- Client starts `swing()` and sends `SpearJabPacket`. Server re-raycasts and applies authoritative damage/knockback/hit sound.
- Charge state stores hand, copied spear stack, start game time and per-target contacts. It no longer depends on server `isUsingItem()` or client use-duration bookkeeping.
- Charge elapsed time is `gameTime - startedAt`.
- The attack renderer applies the existing `VanillaSpearAnimations.firstPersonAttack` math with equip progress forced to zero, matching the known-good block/mining anchor.
- Legacy `MixinPlayerSpearSounds` is removed from the active mixin config.

## 1.21.11 reference

The actual 1.21.11 spear uses dedicated KineticWeapon and PiercingWeapon components plus a dedicated SPEAR first-person use animation, rather than treating the spear as a normal sword.
