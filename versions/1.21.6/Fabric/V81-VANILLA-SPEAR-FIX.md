# V82 — Vanilla 1.21.11 spear render-flow correction for Minecraft 1.21.10

- Fixed the missing first-person spear base anchor before `firstPersonUse`: X = +/-0.56, Y = -0.52, Z = -0.72, matching Mojang 1.21.11.
- Kept the exact vanilla time-held expression: `useDuration - (remainingTicks - partialTick + 1)`.
- Corrected the local `inOutBack` easing to Mojang `Ease.inOutBack`.
- Third-person attack/use remain separated exactly like `ItemInHandLayer`: STAB item transform first, then use-pose animation.
- Third-person attack item forward movement is 0.38, matching vanilla spear `KineticWeapon.forwardMovement()`.
- No Accurate Spears visual/runtime classes are used.
- 1.21.10 compatibility is implemented only where 1.21.11 state/component APIs are absent.

Reference: Mojang 1.21.11-equivalent source for `ItemInHandRenderer` and `SpearAnimations`.
