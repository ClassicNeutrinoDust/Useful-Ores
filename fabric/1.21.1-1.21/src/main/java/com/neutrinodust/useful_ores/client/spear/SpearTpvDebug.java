package com.neutrinodust.useful_ores.client.spear;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;







public final class SpearTpvDebug {
    private static final boolean ENABLED = false;
    private static final Logger LOGGER = LoggerFactory.getLogger("Useful Ores/TPV-Spear-Debug");
    private static final AtomicLong SEQUENCE = new AtomicLong();

    private SpearTpvDebug() {}

    public static long next(String event) {
        long id = SEQUENCE.incrementAndGet();
        if (ENABLED) LOGGER.info("TPVDBG seq={} event={}", id, event);
        return id;
    }

    public static void context(String phase, LivingEntity entity, ItemStack stack,
                               ItemDisplayContext displayContext, boolean leftHanded) {
        if (!ENABLED) return;
        LOGGER.info(
                "TPVDBG context phase={} entity={} stack={} display={} left={} using={} useHand={} ticksUsing={} attackAnim={} mainArm={} mainHand={}",
                phase,
                System.identityHashCode(entity),
                stack.getItem(),
                displayContext,
                leftHanded,
                entity.isUsingItem(),
                entity.getUsedItemHand(),
                entity.getTicksUsingItem(),
                entity.getAttackAnim(0.0F),
                entity.getMainArm(),
                entity.getMainHandItem().getItem()
        );
    }

    public static void itemInvocation(LivingEntity entity, ItemStack stack,
                                      ItemDisplayContext displayContext, boolean leftHanded,
                                      boolean matched, float attack, float ticksUsing) {
        if (!ENABLED) return;
        LOGGER.info(
                "TPVDBG itemInvocation entity={} stack={} display={} left={} matched={} attack={} ticksUsing={} using={} useItem={} useHand={}",
                System.identityHashCode(entity),
                stack.getItem(),
                displayContext,
                leftHanded,
                matched,
                attack,
                ticksUsing,
                entity.isUsingItem(),
                entity.getUseItem().getItem(),
                entity.getUsedItemHand()
        );
    }

    public static void handInput(LivingEntity entity, HumanoidArm arm, ItemStack stack,
                                 float ticksUsing, float attack, HumanoidModel<?> model) {
        if (!ENABLED) return;
        var target = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        LOGGER.info(
                "TPVDBG handInput entity={} arm={} stack={} ticksUsing={} attack={} head[x={},y={},z={}] bodyY={} armBefore[x={},y={},z={}]",
                System.identityHashCode(entity), arm, stack.getItem(), ticksUsing, attack,
                model.head.xRot, model.head.yRot, model.head.zRot, model.body.yRot,
                target.xRot, target.yRot, target.zRot
        );
    }

    public static void handOutput(LivingEntity entity, HumanoidArm arm, ItemStack stack,
                                  float ticksUsing, HumanoidModel<?> model) {
        if (!ENABLED) return;
        var target = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        LOGGER.info(
                "TPVDBG handOutput entity={} arm={} stack={} ticksUsing={} armAfter[x={},y={},z={}]",
                System.identityHashCode(entity), arm, stack.getItem(), ticksUsing,
                target.xRot, target.yRot, target.zRot
        );
    }

    public static void useItem(LivingEntity entity, HumanoidArm arm, ItemStack stack,
                               float timeHeld, float attackTime,
                               float raise, float raiseBack, float sway,
                               float lower, float slow, float fast, float intensity,
                               float raiseMod, float feedback,
                               float xDegrees, float yDegrees,
                               double tx, double ty, double tz) {
        if (!ENABLED) return;
        LOGGER.info(
                "TPVDBG useItem entity={} arm={} stack={} timeHeld={} attackTime={} q[raise={},back={},sway={},lower={},slow={},fast={},intensity={}] transform[raiseMod={},feedback={},xDeg={},yDeg={},translate={}, {}, {}]",
                System.identityHashCode(entity), arm, stack.getItem(), timeHeld, attackTime,
                raise, raiseBack, sway, lower, slow, fast, intensity,
                raiseMod, feedback, xDegrees, yDegrees, tx, ty, tz
        );
    }

    public static void poseMatrix(String phase, Object matrix) {
        if (!ENABLED) return;
        LOGGER.info("TPVDBG poseMatrix phase={} matrix={}", phase, matrix);
    }

    public static void attackItem(LivingEntity entity, HumanoidArm arm, ItemStack stack,
                                  float attackTime, float attackCurve, float retractCurve,
                                  float xDegrees, float translateY) {
        if (!ENABLED) return;
        LOGGER.info(
                "TPVDBG attackItem entity={} arm={} stack={} attackTime={} attackCurve={} retractCurve={} xDeg={} translateY={}",
                System.identityHashCode(entity), arm, stack.getItem(), attackTime,
                attackCurve, retractCurve, xDegrees, translateY
        );
    }
}
