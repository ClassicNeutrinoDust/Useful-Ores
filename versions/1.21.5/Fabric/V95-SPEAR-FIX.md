# Useful Ores V95 — spear desync fix + charge diagnostics

I read through V91–V94 and the actual source (not just the notes), and traced two concrete, fixable things rather than re-guessing at vanilla numbers again.

## 1. Animation drops on air/mob jabs but is correct on blocks — real cause found

`MixinMinecraftSpearSounds` cancels the charging state (`isUsingItem`) the instant you
left-click, but it only does this on the **client** (`Minecraft#startAttack` is a
client-only method, and it only touches `minecraft.player`, the `LocalPlayer`).

The **server**'s copy of you never gets told the charge ended. So if you ever
right-clicked (even briefly, even earlier), the server keeps thinking you're still
using the spear. Nothing forces those two states back in sync — except by coincidence.
Hitting a block happens not to disturb it, but attacking an entity/swinging near one
causes extra entity-tracking sync traffic involving your own player, and when that
carries the server's stale "still using item" flag back down, it stomps your client's
local cancel — which is exactly the "pose drops to the charging position" symptom,
and it's not just visual: the server-side charge tracker (`SpearChargeCombat`) also
never got told to stop, so its own state could be stale too.

**Fix (`combat/SpearUseStateSync.java`, wired up in `UsefulOresMod`):**
- Registers `AttackEntityCallback` to call `player.stopUsingItem()` **on the server**
  the moment a spear-holder attacks an entity, mirroring the existing client-side cancel.
- Also cancels the server-side use state on `PlayerBlockBreakEvents.BEFORE`, so starting
  to mine cancels a charge server-side too (matching vanilla's "any new action interrupts
  item use" rule), instead of relying on it happening to not matter.

This closes the desync in both directions. It should fix the animation issue for
mob-attacks. Air jabs (no entity involved) aren't touched by `AttackEntityCallback`,
so if the drop still happens on a *pure* air swing after this, that's a second,
narrower bug (most likely the same desync but triggered by some other sync packet,
e.g. health/velocity) — tell me and I'll add a matching server-side hook for the
swing-with-no-target path specifically.

## 2. "Running towards a mob doesn't deal damage" — instrumented, not guessed

I did **not** re-tune `minRelativeSpeedForChargeDamage` (4.6 blocks/sec) or the other
V91–V94 constants again — every previous version already did that blind, based on
notes from bytecode inspection I can't re-verify without a real 1.21.11 JAR and a
running game, which I don't have here. Guessing a 5th set of numbers isn't going to
be more trustworthy than the 4th.

Instead, `SpearChargeCombat.tick()` now has a `DEBUG_LOG` switch (on by default in
this build) that prints, every 5 ticks while you're charging and a target is in range:

```
[spear-debug] attacker=You target=Zombie elapsed=12t attackerSpeed=3.821 targetSpeed=0.000 relSpeed=3.821 dismount=false knockback=false damage=false (need relSpeed>=4.6 within 300t)
```

or, if nothing is even in range:

```
[spear-debug] attacker=You elapsed=8t: no candidate targets in range (maxReach=4.5)
```

**What to do:** charge into a mob like you did before, then check the server/client log
(`logs/latest.log`) for `[spear-debug]` lines and send them to me. That tells us
immediately which case you're in:
- No lines at all → the charge tick isn't even reaching the target loop (likely still
  the desync in part 1, or `elapsed < 0` never clearing — the delay window not passing).
- "no candidate targets" → it's a reach/hitbox problem in `findTargets`, not a damage
  threshold problem.
- Lines with `damage=false` and `relSpeed` consistently under ~4.6 → you need to be
  sprinting, not just walking, into the target; 4.6 blocks/sec is close to sprint speed,
  and this is the number to lower if you want charge damage to be easier to trigger even
  at a jog. I can tune it precisely once I see a real `relSpeed` value from your game
  instead of guessing again.
- `damage=true` but nothing happens → the problem is downstream, in the
  `target.hurtServer(...)` call itself (e.g. invulnerability frames from the immediately
  preceding jab attack's own cooldown), which is a different, easy fix.

Set `DEBUG_LOG = false` in `SpearChargeCombat.java` once you're done diagnosing; it logs
on the server console/log, not in chat, so it won't spam your screen.

## What I did not touch

The exact animation pose math (`VanillaSpearAnimations`) and the per-tier tuning table
(`SpearAnimationProfile`) are unchanged in this pass — they may still be off, but without
a way to run the game here, changing numbers again would be exactly the same guesswork
that produced V91 through V94. The debug log is meant to replace that guesswork with
real numbers from your own game.

## Build note

Same as every prior version: there's no Fabric/Minecraft Gradle toolchain reachable from
this sandbox (only a small allow-list of package registries, not Fabric's Maven), so this
was not compiled here. Please run your normal local `gradle build` — the two new/changed
files are small and use only APIs already used elsewhere in this project (`AttackEntityCallback`,
`PlayerBlockBreakEvents.BEFORE`, `Constants.LOG`), so they should compile cleanly, but you're
the first real compile+runtime test this has had.
