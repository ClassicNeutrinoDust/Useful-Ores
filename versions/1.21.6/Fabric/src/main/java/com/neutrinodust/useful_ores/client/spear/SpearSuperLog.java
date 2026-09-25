package com.neutrinodust.useful_ores.client.spear;

import com.neutrinodust.useful_ores.Constants;

/**
 * V96 "super logger" — a single place every spear-related code path reports
 * to, so a short repro (a few seconds of jabbing/mining/charging) produces one
 * readable timeline instead of guessing which of a dozen mixins is at fault.
 *
 * Every line is prefixed "[spear-super]" and tagged with which hook produced
 * it, so `grep spear-super logs/latest.log` gives the full story in order.
 * Set ENABLED = false to silence all of it once the bug is found.
 *
 * The render-branch hook fires every frame in vanilla (up to your FPS), which
 * would flood the log uselessly - it's throttled to once per *game tick*
 * (20/sec) instead, which is still fine-grained enough to see spam/resets but
 * won't produce megabytes of near-duplicate lines.
 */
public final class SpearSuperLog {
    private SpearSuperLog() {}

    public static final boolean ENABLED = true;

    private static long lastRenderLogTick = Long.MIN_VALUE;

    /** Every single invocation of Minecraft#startAttack while a spear is held,
     *  BEFORE any of our own gating decides what to do with it. If this fires
     *  more than once for what felt like a single click, that's the spam. */
    public static void startAttack(long tick, String hitType, boolean isUsingItem,
                                    boolean onCooldown, String outcome) {
        if (!ENABLED) return;
        Constants.LOG.info(
                "[spear-super][startAttack] tick={} hit={} usingItem={} onCooldown={} outcome={}",
                tick, hitType, isUsingItem, onCooldown, outcome);
    }

    /** Which first-person pose branch is rendering, throttled to once per game
     *  tick (not per frame). Compare this against startAttack timestamps.
     *  V97: added equipProgress and hitType - the one input to the ATTACK
     *  branch's pose that was never actually logged/verified as identical
     *  between block-bail and entity/air jabs, only assumed to be. */
    public static void renderBranch(long tick, String hand, boolean useBranch,
                                     boolean isUsingItem, int useRemaining, float swingProgress,
                                     float equipProgress, String currentHitType) {
        if (!ENABLED) return;
        if (tick == lastRenderLogTick) return;
        lastRenderLogTick = tick;
        Constants.LOG.info(
                "[spear-super][render] tick={} hand={} branch={} usingItem={} useRemaining={} swingProgress={} equipProgress={} hitType={}",
                tick, hand, useBranch ? "USE" : "ATTACK", isUsingItem, useRemaining, swingProgress, equipProgress, currentHitType);
    }

    /** Fires whenever our sprint-while-charging override actually overrides
     *  vanilla's shouldStopRunSprinting - tells you if/when it's active. */
    public static void sprintGate(long tick, boolean weBlockedTheStop) {
        if (!ENABLED) return;
        Constants.LOG.info("[spear-super][sprintGate] tick={} weKeptSprintingAllowed={}", tick, weBlockedTheStop);
    }

    /** Every time our swing-duration override is actually consulted. */
    public static void swingDuration(long tick, String entity, int durationTicks) {
        if (!ENABLED) return;
        Constants.LOG.info("[spear-super][swingDuration] tick={} entity={} duration={}t", tick, entity, durationTicks);
    }

    /** Charge (right-click) starting/ending, tagged with which side (CLIENT/SERVER)
     *  called it, so you can see if the two sides ever disagree about the state. */
    public static void useStart(String side, long tick, String player) {
        if (!ENABLED) return;
        Constants.LOG.info("[spear-super][useStart:{}] tick={} player={}", side, tick, player);
    }

    public static void useEnd(String side, long tick, String player) {
        if (!ENABLED) return;
        Constants.LOG.info("[spear-super][useEnd:{}] tick={} player={}", side, tick, player);
    }

    /** A real jab connecting with an entity (server-side, after the fact). */
    public static void attackHit(long tick, String attacker, String target) {
        if (!ENABLED) return;
        Constants.LOG.info("[spear-super][attackHit] tick={} attacker={} target={}", tick, attacker, target);
    }
}
