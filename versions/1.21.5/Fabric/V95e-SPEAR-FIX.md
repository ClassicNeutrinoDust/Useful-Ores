# V95d Spear Fix

Two fixes, both confirmed against the client log the user attached (`latest.log`)
and direct code inspection - no guessing beyond what the code/log actually show.

## 1. First-person jab pose drops low on mob/air attacks

`mixin/MixinVanillaSpearFirstPerson.java`, ATTACK branch, had an unconditional

    pose.translate(0.0F, 0.09F, 0.0F);

applied whenever `swingProgress > 0.0F`. Every other transform in this class
(and in `VanillaSpearAnimations`) is sourced from vanilla SpearAnimations math
or `SpearAnimationProfile` - this one wasn't, and has no counterpart in the
third-person path (`MixinItemInHandLayerSpear`) or the USE branch. Removed it.

Why it only showed up on mob/air attacks: swingProgress resets to 0 and
replays from scratch on each discrete swing against a mob/air target, so the
arm fully returns to idle between hits and the offset is visible every time.
Continuous mining swings never let the arm rest between hits, so the same
offset was being masked there instead of being absent.

## 2. Charge (right-click) attack stops dealing damage after ~2 hits

`combat/SpearChargeCombat.java` gated charge damage/knockback/dismount on
`elapsed`, measured from the moment right-click was first pressed
(`ticksUsed = useDuration - remainingUseDuration`). Because the spear's
`Consumable` duration is effectively unlimited (72000 ticks), holding
right-click through multiple encounters let `elapsed` climb past all three
windows (dismount ~5s, knockback ~10s, damage ~11-15s depending on material)
while the charge animation kept playing as if still active. The charge was
functionally dead but gave no indication, so the first couple of hits (still
inside the window) would land and everything after would silently whiff.

Fix: once `elapsed` exceeds the longest of the three windows, the charge is
now force-ended (`attacker.stopUsingItem()` + removed from `ACTIVE_USES`)
instead of being left in a dead-but-animating state. This makes a stale
charge require a fresh right-click press, matching what the animation already
visually implies is happening.

## V95e follow-up (the real root cause)

The V95d jab-offset removal and the charge-window fix above were both
legitimate but didn't fully resolve either report, because three existing
mixins had a much more fundamental bug: they called `cir.setReturnValue(...)`
without ever calling `cir.cancel()`. In SpongePowered Mixin, `setReturnValue`
only supplies the value to return *if* the callback is cancelled - without
`cancel()`, the original vanilla method keeps executing normally afterward
and the value is discarded. All three were silent no-ops:

- `MixinMinecraftSpearSounds` (per-jab cooldown gate on `Minecraft#startAttack`)
  - never actually blocked anything, so jabs against mobs/air kept re-firing
    every client tick, restarting `attackAnim` from 0 continuously instead of
    completing one swing - almost certainly the real source of both the
    "spam like mining" report and the jab pose looking wrong.
- `MixinLivingEntitySpearSwingDuration` (`getCurrentSwingDuration` override)
  - never applied, so spears used vanilla's default swing timing instead of
    the material-tuned `SpearAnimationProfile` duration.
- `MixinLocalPlayerSpearUseEffects` (`shouldStopRunSprinting` override)
  - never applied, so vanilla kept force-stopping sprint the instant a charge
    started. Since `minRelativeSpeedForChargeDamage` sits right around sprint
    speed, this alone explains "rarely, sometimes deals damage" - it only
    worked on the odd tick where momentum carried you fast enough anyway
    despite not actually sprinting.

All three now call `cir.cancel()` alongside `setReturnValue(...)`. Investigated
by checking Accurate Spears' equivalent mixins for comparison; that mod turned
out to implement a completely different mechanic (an enchantment-triggered
lunge on ordinary melee attacks, not a hold-to-charge kinetic weapon), so
there was nothing to port from it for the charge attack - but the comparison
is what led to re-reading these three mixins closely enough to spot the
missing `cancel()` calls.

## Not changed

- The "spam-attacks-like-mining" behavior on holding left-click: this is
  vanilla `Minecraft.startAttack()` input handling (re-fires every tick the
  key is held, for blocks/mobs/air alike). None of the spear mixins touch
  attack input, so there's nothing spear-specific to fix here unless a
  deliberate per-click cooldown gate is wanted on `SpearItem` - that's a
  design change, not a bug fix, and wasn't applied.
