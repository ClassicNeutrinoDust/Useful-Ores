package com.neutrinodust.useful_ores.client.spear;

import com.neutrinodust.useful_ores.Constants;















public final class SpearSuperLog {
    private SpearSuperLog() {}

    public static final boolean ENABLED = true;

    private static long lastRenderLogTick = Long.MIN_VALUE;

    


    public static void startAttack(long tick, String hitType, boolean isUsingItem,
                                    boolean onCooldown, String outcome) {
        if (!ENABLED) return;
        Constants.LOG.info(
                "[spear-super][startAttack] tick={} hit={} usingItem={} onCooldown={} outcome={}",
                tick, hitType, isUsingItem, onCooldown, outcome);
    }

    




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

    

    public static void sprintGate(long tick, boolean weBlockedTheStop) {
        if (!ENABLED) return;
        Constants.LOG.info("[spear-super][sprintGate] tick={} weKeptSprintingAllowed={}", tick, weBlockedTheStop);
    }

    
    public static void swingDuration(long tick, String entity, int durationTicks) {
        if (!ENABLED) return;
        Constants.LOG.info("[spear-super][swingDuration] tick={} entity={} duration={}t", tick, entity, durationTicks);
    }

    

    public static void useStart(String side, long tick, String player) {
        if (!ENABLED) return;
        Constants.LOG.info("[spear-super][useStart:{}] tick={} player={}", side, tick, player);
    }

    public static void useEnd(String side, long tick, String player) {
        if (!ENABLED) return;
        Constants.LOG.info("[spear-super][useEnd:{}] tick={} player={}", side, tick, player);
    }

    
    public static void attackHit(long tick, String attacker, String target) {
        if (!ENABLED) return;
        Constants.LOG.info("[spear-super][attackHit] tick={} attacker={} target={}", tick, attacker, target);
    }
}
