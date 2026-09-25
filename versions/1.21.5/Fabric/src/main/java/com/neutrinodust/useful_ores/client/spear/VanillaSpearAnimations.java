package com.neutrinodust.useful_ores.client.spear;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

/** Vanilla SpearAnimations math, with the missing component/state data supplied by SpearAnimationProfile. */
public final class VanillaSpearAnimations {
    private static final float DEG = (float)(Math.PI / 180.0D);
    private VanillaSpearAnimations() {}

    private static float progress(float time, float start, float end) {
        return Mth.clamp(Mth.inverseLerp(time, start, end), 0.0F, 1.0F);
    }
    private static float inOutSine(float x) { return -(float)(Math.cos(Math.PI*x)-1.0D)/2.0F; }
    private static float outBack(float x) { float c1=1.70158F,c3=c1+1.0F,y=x-1.0F; return 1.0F+c3*y*y*y+c1*y*y; }
    private static float inBack(float x) { return 1.0F-outBack(1.0F-x); }
    private static float inOutBack(float x) {
        float c2 = 2.5949094F;
        if (x < 0.5F) {
            return 4.0F * x * x * (7.189819F * x - c2) / 2.0F;
        }
        float dt = 2.0F * x - 2.0F;
        return (dt * dt * (3.5949094F * dt + c2) + 2.0F) / 2.0F;
    }
    private static float inOutElastic(float x) {
        if (x==0.0F||x==1.0F) return x;
        double c5=2.0D*Math.PI/4.5D;
        return x<0.5F ? (float)(-(Math.pow(2.0D,20.0D*x-10.0D)*Math.sin((20.0D*x-11.125D)*c5))/2.0D)
                       : (float)(Math.pow(2.0D,-20.0D*x+10.0D)*Math.sin((20.0D*x-11.125D)*c5)/2.0D+1.0D);
    }
    private static float outCirc(float x) { return (float)Math.sqrt(Math.max(0.0D,1.0D-(x-1.0F)*(x-1.0F))); }
    private static float inCirc(float x) { return -(float)(Math.sqrt(Math.max(0.0D,1.0D-x*x))-1.0D); }
    private static float outCubic(float x) { float y=1.0F-x; return 1.0F-y*y*y; }
    private static float inOutExpo(float x) {
        if (x==0.0F||x==1.0F) return x;
        return x<0.5F ? (float)Math.pow(2.0D,20.0D*x-10.0D)/2.0F
                       : (float)(2.0D-Math.pow(2.0D,-20.0D*x+10.0D))/2.0F;
    }

    private record UseParams(float raiseProgress,float raiseProgressStart,float raiseProgressMiddle,float raiseProgressEnd,
                             float swayProgress,float lowerProgress,float raiseBackProgress,float swayIntensity,
                             float swayScaleSlow,float swayScaleFast) {}

    private static UseParams params(ItemStack stack, float time) {
        SpearAnimationProfile p=SpearAnimationProfile.forStack(stack);
        int finishRaise=Math.round(p.chargeDelaySeconds()*20.0F);
        int finishSway=finishRaise+Math.round(p.maxDurationForDismountSeconds()*20.0F);
        int startSway=finishSway-20;
        int finishLower=finishRaise+Math.round(p.maxDurationForChargeKnockbackSeconds()*20.0F);
        int startLower=finishLower-40;
        int finishBack=finishRaise+Math.round(p.maxDurationForChargeDamageSeconds()*20.0F);
        float raise=progress(time,0.0F,finishRaise);
        float rs=progress(raise,0.0F,0.5F), rm=progress(raise,0.5F,0.8F), re=progress(raise,0.8F,1.0F);
        float sway=progress(time,startSway, startLower);
        float lower=outCubic(inOutElastic(progress(time-20.0F,startLower,finishLower)));
        float back=progress(time,finishBack-5.0F,finishBack);
        float intensity=2.0F*outCirc(sway)-2.0F*inCirc(back);
        float slow=(float)Math.sin(time*19.0F*DEG)*intensity;
        float fast=(float)Math.sin(time*30.0F*DEG)*intensity;
        return new UseParams(raise,rs,rm,re,sway,lower,back,intensity,slow,fast);
    }

    private static float hitFeedbackAmount(float ticks) {
        return 0.4F*(outQuart(progress(ticks,1.0F,3.0F))-inOutSine(progress(ticks,3.0F,10.0F)));
    }
    private static float outQuart(float x) { float y=1.0F-x; return 1.0F-y*y*y*y; }

    public static void firstPersonUse(float ticksSinceFeedback, PoseStack pose, float timeHeld, HumanoidArm arm, ItemStack stack) {
        UseParams q=params(stack,timeHeld); int invert=arm==HumanoidArm.RIGHT?1:-1;
        pose.translate((float)invert*(q.raiseProgress()*0.15F+q.raiseProgressEnd()*-0.05F+q.swayProgress()*-0.1F+q.swayScaleSlow()*0.005F),
                q.raiseProgress()*-0.075F+q.raiseProgressMiddle()*0.075F+q.swayScaleFast()*0.01F,
                q.raiseProgressStart()*0.05F+q.raiseProgressEnd()*-0.05F+q.swayScaleSlow()*0.005F);
        pose.rotateAround(Axis.XP.rotationDegrees(-65.0F*inOutBack(q.raiseProgress())-35.0F*q.lowerProgress()+100.1F*q.raiseBackProgress()-0.5F*q.swayScaleFast()),0.0F,0.1F,0.0F);
        pose.rotateAround(Axis.YN.rotationDegrees((float)invert*(-90.0F*progress(q.raiseProgress(),0.5F,0.55F)+90.0F*q.swayProgress()+2.0F*q.swayScaleSlow())),(float)invert*0.15F,0.0F,0.0F);
        pose.translate(0.0F,-hitFeedbackAmount(ticksSinceFeedback),0.0F);
    }

    public static void firstPersonAttack(float attack, PoseStack pose, int invert, HumanoidArm arm) {
        float starting= inOutSine(progress(attack,0.0F,0.05F));
        float middle=outBack(progress(attack,0.05F,0.2F));
        float ending=inOutExpo(progress(attack,0.4F,1.0F));
        pose.translate((float)invert*0.1F*(starting-middle),-0.075F*(starting-ending),0.65F*(starting-middle));
        pose.mulPose(Axis.XP.rotationDegrees(-70.0F*(starting-ending)));
        pose.translate(0.0F,0.0F,-0.25F*(ending-middle));
    }


    public static void thirdPersonHandUse(HumanoidModel<?> model, HumanoidRenderState state, ItemStack item) {
        boolean right=state.useItemHand==InteractionHand.MAIN_HAND ? state.mainArm==HumanoidArm.RIGHT : state.mainArm!=HumanoidArm.RIGHT;
        var arm=right?model.rightArm:model.leftArm;
        var head=model.head;
        int invert=right?1:-1;
        arm.yRot=-0.1F*invert+head.yRot;
        arm.xRot=-((float)Math.PI/2F)+head.xRot+0.8F;
        if(state.isFallFlying || state.swimAmount>0.0F) arm.xRot-=0.9599311F;
        arm.yRot=((float)Math.PI/180F)*Math.clamp(arm.yRot/DEG,-60.0F,60.0F);
        arm.xRot=((float)Math.PI/180F)*Math.clamp(arm.xRot/DEG,-120.0F,30.0F);
        if(state.ticksUsingItem>0.0F && (!state.isUsingItem || state.useItemHand==(right?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND))){
            UseParams q=params(item,state.ticksUsingItem);
            arm.yRot += -invert*q.swayScaleFast()*DEG*q.swayIntensity();
            arm.zRot += -invert*q.swayScaleSlow()*DEG*q.swayIntensity()*0.5F;
            arm.xRot += (-40.0F*q.raiseProgressStart()+30.0F*q.raiseProgressMiddle()-20.0F*q.raiseProgressEnd()+20.0F*q.lowerProgress()+10.0F*q.raiseBackProgress()+0.6F*q.swayScaleSlow()*q.swayIntensity())*DEG;
        }
    }

    public static void thirdPersonUseItem(ArmedEntityRenderState state, PoseStack pose, float timeHeld, float attackTime, HumanoidArm arm, ItemStack stack) {
        if(timeHeld==0.0F) return;
        UseParams q=params(stack,timeHeld); int invert=arm==HumanoidArm.RIGHT?1:-1;
        float attack=inQuad(progress(attackTime,0.05F,0.2F));
        float retract=inOutExpo(progress(attackTime,0.4F,1.0F));
        float raiseMod=1.0F-outBack(1.0F-q.raiseProgress());
        float feedback=hitFeedbackAmount(10.0F); // no kinetic-hit render-state is required by this compatibility path; neutral
        pose.translate(0.0D,-feedback*0.4D,-(raiseMod-q.raiseBackProgress())*0.0D+feedback);
        pose.rotateAround(Axis.XN.rotationDegrees(70.0F*(q.raiseProgress()-q.raiseBackProgress())-40.0F*(attack-retract)),0.0F,-0.03125F,0.125F);
        pose.rotateAround(Axis.YP.rotationDegrees((float)(invert*90)*(q.raiseProgress()-q.swayProgress()+3.0F*retract+attack)),0.0F,0.0F,0.125F);
    }

    private static float inQuad(float x){ return x*x; }

    public static void thirdPersonAttackHand(HumanoidModel<?> model, HumanoidRenderState state) {
        model.rightArm.yRot-=model.body.yRot; model.leftArm.yRot-=model.body.yRot; model.leftArm.xRot-=model.body.yRot;
        float prepare=inOutSine(progress(state.attackTime,0.0F,0.05F));
        float attack=inQuad(progress(state.attackTime,0.05F,0.2F));
        float retract=inOutExpo(progress(state.attackTime,0.4F,1.0F));
        var arm=state.attackArm==HumanoidArm.RIGHT?model.rightArm:model.leftArm;
        arm.xRot += (90.0F*prepare-120.0F*attack+30.0F*retract)*DEG;
    }

    public static void thirdPersonAttackItem(ArmedEntityRenderState state, PoseStack pose, float attackTime, ItemStack stack) {
        if(attackTime<=0.0F || attackTime>=1.0F) return;
        float attack=inQuad(progress(attackTime,0.05F,0.2F));
        float retract=inOutExpo(progress(attackTime,0.4F,1.0F));
        pose.rotateAround(Axis.XN.rotationDegrees(70.0F*(attack-retract)),0.0F,-0.125F,0.125F);
        float forward=SpearAnimationProfile.forStack(stack).forwardMovement();
        pose.translate(0.0F,forward*(attack-retract),0.0F);
    }

}
