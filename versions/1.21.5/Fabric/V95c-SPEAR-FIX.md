# Useful Ores V95c — target-detection bug found from your second log

## Good news first: the speed fix from V95b worked

Your new log has real, sensible numbers now instead of the flat `0.000` from before:

```
attackerSpeed=14.628 ... relSpeed=14.628 ... damage=true
attackerSpeed=5.465 ... relSpeed=5.465 ... damage=true
```

That confirms the `knownSpeed()` rewrite is correctly measuring your movement now.
The remaining problem isn't speed - it's target detection.

## The real bug: entities were barely ever found at all

Counted across your log: **97 ticks logged "no candidate targets in range" vs. only
8 ticks that found a target at all**, while you were actively charging with good
speed. Two compounding problems in `findTargets()`:

1. **A dead early-return that undid its own fix note.** The code had:
   ```java
   double reach = Math.min(maxReach, visibleReach);
   if (reach < MIN_REACH) {
       return List.of();   // <- skips entity search ENTIRELY
   }
   ...
   // NOTE: MIN_REACH must NOT also exclude entities that are already close...
   ```
   A comment from an earlier pass already correctly identified that this shouldn't
   happen, but the actual `return List.of()` two lines above was never removed - the
   fix was written down but not applied. In practice: any time the straight-line
   distance to the nearest block in front of you (the ground, a slope, terrain) was
   under 2 blocks - which is normal when running at ground level toward a mob and
   looking roughly at it - the game skipped looking for entities *at all* that tick,
   including the moment you were closing into hit range. This is very plausibly why
   it "never worked on normal running": ground-based charges are exactly the
   situation that triggers this early-out, while flying well above the terrain in
   creative mostly avoids it (explaining why it only worked "sometimes" there).

2. **A too-tight hitbox margin (0.125 blocks).** The hit test required your exact
   camera ray to pass within a needle's width of the target's hitbox - basically
   pixel-perfect aim, which explains the "only at a specific angle" feel even when
   detection wasn't skipped outright.

## Fixes

- Removed the early-return; the block-visibility distance now only trims how far
  the search line reaches (so you still can't hit something through a wall) instead
  of disabling entity detection altogether.
- Widened the hitbox margin from 0.125 to 0.75 blocks.
- Added a fallback: if a charging player is already touching/inside the target
  (the common case when normal ground movement physically stops you right at
  contact), it now checks plain proximity instead of requiring the camera ray to
  cleanly clip through the box.

## What to test next

Re-run both the creative-flying case and, importantly, a normal survival ground
charge (which your last log didn't actually contain any data for - it was all
`maxReach=6.5`, i.e. creative, the whole session). The `[spear-debug]` log will now
tell us directly whether ground charges are finding targets at all.

## Jab animation

No new evidence came in this round (the log only had two USE/ATTACK branch flips,
both lining up cleanly with right-click press/release, which looks correct - not a
bug on its own). Still waiting on a log captured from a plain left-click jab with no
right-click involved, per the last message, to chase that one further.
