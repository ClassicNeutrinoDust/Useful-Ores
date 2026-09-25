# Useful Ores V95b — real fixes from your log + decompiling Accurate Spears

Your log was the first hard evidence anyone (including my earlier passes) has had.
I also pulled a real decompiler (CFR) and read Accurate Spears' actual class files
instead of guessing what it does. Two things below are now fixed with confidence;
one is instrumented rather than guessed a third time.

## 1. CONFIRMED root cause of "charge deals no damage": your log proves it

```
[spear-debug] attacker=memoproton2 target=Enderman elapsed=15t attackerSpeed=0.000 targetSpeed=0.000 relSpeed=0.000 ...
```

`attackerSpeed=0.000` every tick, even while you were actively charging at the
Enderman. The bug: `knownSpeed()` computed movement as `getX() - xo` (position minus
last-tick position). That works for mobs, but a real player's position is set
directly from movement packets *before* the entity's own tick() runs each server
tick - by the time our code read `xo`, it had usually already been overwritten to
match the new position, so the delta was structurally ~0 regardless of how fast you
were actually moving. This wasn't a tuning problem, it was measuring nothing.

**Fix:** `knownSpeed()` now keeps its own per-entity position history (sampled once
per game tick, independent of how the position was updated) and computes real
displacement from that. This should make `attackerSpeed`/`relSpeed` reflect your
actual movement now - re-run with a mob and check the log; you should see non-zero
values while sprinting at it.

## 2. CONFIRMED root cause of "spam attacks like mining"

`MixinMinecraftSpearSounds` hooks `Minecraft#startAttack`. I checked Accurate
Spears' own equivalent hook (`SpearAttackMixin`, on `MultiPlayerGameMode#attack`) -
it only ever fires when you're actually attacking an entity. Ours had no such check:
`startAttack()` is the same method vanilla calls once per client tick for as long as
you hold the mouse button down, including to continue mining a block. So holding
left-click on a block was firing our "jab" logic (sound, cooldown, stopUsingItem())
once per tick the whole time you mined - the spam you saw.

**Fix:** bail out immediately if `Minecraft.hitResult` is a `BLOCK` hit, so mining is
left completely alone and only an actual entity-hit or an air swing counts as a jab.

## 3. Animation still dropping on mob/air jabs — instrumented, not re-guessed

Two fix attempts (server/client use-state desync, then the mining-spam gate) didn't
resolve this per your report, and I don't want to ship a third blind guess. I looked
at whether Accurate Spears' combat model could just replace ours here, but it's a
genuinely different feature - its "lunge" is an enchantment-gated dash ability, not
the run-and-jab kinetic charge this project is going for, so its combat code isn't a
drop-in swap. Its jab-animation handling, though, confirmed something useful: it
never touches its dedicated pose code unless it's certain an actual jab/hold is
happening - it doesn't infer state from timing quirks. So I added logging at the
exact fork in our own renderer that decides which pose to use
(`MixinVanillaSpearFirstPerson`, first-person view - the one in your screenshots):

```
[spear-anim-debug] branch changed to USE | isUsingItem=true useRemaining=71988 usedHand=MAIN_HAND hand=MAIN_HAND swingProgress=0.0
```

It only logs when the branch actually flips (not every frame), so it won't spam.
**Please reproduce the drop (plain left-click jab at air or a mob, no right-click
involved) and send me the log again.** That tells us definitively:
- If it logs `branch changed to USE` right when you jab (with no right-click), then
  `isUsingItem()` is somehow true when it shouldn't be at all - that's a real bug
  upstream of this renderer and I can chase exactly why with that confirmation.
- If it stays on `ATTACK` the whole time and the pose is still wrong, the bug is
  purely in `VanillaSpearAnimations.firstPersonAttack`'s math, not in which branch
  fires - a much smaller, more mechanical fix.

## Not touched

`VanillaSpearAnimations`'s pose math and the per-tier tuning table are still
unchanged - same reasoning as before, I don't want to stack a new guess on an
unconfirmed one. The `attackerSpeed` fix in part 1 might on its own change how the
charge *feels* enough that some of what looked like an animation bug was actually
the charge state never resolving; worth re-testing before diagnosing further.

## Build note

Still no Fabric/Minecraft toolchain reachable here, so still not locally compiled.
I did pull a real decompiler (CFR) this round and decompiled Accurate Spears' actual
`.class` files to check its logic firsthand rather than trust old notes, but I have
no way to compile or run *this* project's code in this sandbox - your build is still
the first real test of anything in this file.
