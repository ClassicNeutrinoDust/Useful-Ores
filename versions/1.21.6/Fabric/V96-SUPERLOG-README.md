# V96 super logger — how to use it

Bumped `mod_version` to `V96-superlog` so the mod list line in `latest.log`
will actually prove which build you're running (this is why the last log
still said V94 - the version string just hadn't been bumped since then).

New file: `client/spear/SpearSuperLog.java`. Every spear code path now reports
to it with a `[spear-super]` prefix, tagged by hook name:

- `[spear-super][startAttack]` — every single time `Minecraft#startAttack`
  runs for a spear, BEFORE any of our gating. Shows `hit` type
  (BLOCK/ENTITY/MISS), whether the item was mid-charge, cooldown state, and
  outcome (`BAIL_BLOCK`, `BLOCKED_BY_COOLDOWN`, or `JAB_FIRED`). If this fires
  more than once for what felt like one click, that's the spam, in the act.
- `[spear-super][render]` — which first-person pose branch is active, once
  per game tick (not per frame, so it won't flood). Line this up against
  `startAttack` timestamps to see if the pose flips independently of clicks.
- `[spear-super][sprintGate]` — fires whenever the "keep sprinting while
  charging" override actually engages.
- `[spear-super][swingDuration]` — fires whenever the spear's tuned swing
  duration override is consulted, with the value it returned.
- `[spear-super][useStart:CLIENT/SERVER]` / `[useEnd:CLIENT/SERVER]` — charge
  start/stop, tagged by side, so a client/server disagreement about whether
  you're still charging becomes visible as mismatched timestamps.
- `[spear-super][attackHit]` — a jab actually landing on an entity (server).
- `[spear-debug]` — unchanged, still SpearChargeCombat's existing per-5-tick
  charge diagnostics from V95.

## How to reproduce for me

1. Build, launch, confirm `logs/latest.log` shows `useful_ores 2.1.7+mc1.21.10-V96-superlog`
   in the mod list — that's your proof this build is actually running.
2. Do exactly three short, separated actions, a few seconds apart so they're
   easy to tell apart by timestamp:
   - Plain left-click jab at a mob or into the air, 3-4 times, **no right-click
     at all**.
   - Hold left-click on a block for ~2 seconds (mining).
   - If relevant to what you're testing: right-click to charge, then try to
     left-click while still charging.
3. Send me the fresh `logs/latest.log` (or just the `grep spear-super` /
   `grep spear-debug` lines from it).

Set `SpearSuperLog.ENABLED = false` once we've found it — this is deliberately
noisy and not meant to ship.
