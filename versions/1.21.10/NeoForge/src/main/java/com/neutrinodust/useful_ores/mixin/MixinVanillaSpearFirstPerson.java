package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.Constants;
import com.neutrinodust.useful_ores.client.spear.SpearSuperLog;
import com.neutrinodust.useful_ores.client.spear.VanillaSpearAnimations;
import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Reconstructs the vanilla 1.21.11 dedicated spear branch inside 1.21.10. */
@Mixin(ItemInHandRenderer.class)
public abstract class MixinVanillaSpearFirstPerson {
    // V99: the attack branch deliberately uses the known-good zero-equip anchor from
    // the block/mining pose; equipProgress is no longer allowed to move jab frames.
    // Prior V95 diagnostics were retained in project history.
    /* V95 diagnostic note:
    // instead of guessing a third cause blindly, this logs which branch actually
    // fires. Once you reproduce the bad pose, check the client log for
    // "[spear-anim-debug]" lines - "branch=USE" during a plain left-click jab (no
    // right-click involved) means isUsingItem() is somehow true when it shouldn't
    // be, which is the real next thing to chase down; "branch=ATTACK" with a
    // swingProgress that looks wrong points the problem at VanillaSpearAnimations
    // math instead. Only logs on a branch *change* so it won't spam every frame.
    */
    private static boolean usefulOres$lastBranchWasUse = false;
    private static long usefulOres$lastLogTick = -1;

    @Inject(method="renderArmWithItem", at=@At("HEAD"), cancellable=true)
    private void usefulOres$renderSpear(AbstractClientPlayer player,float tickProgress,float pitch,InteractionHand hand,float swingProgress,ItemStack item,float equipProgress,PoseStack pose,SubmitNodeCollector collector,int light,CallbackInfo ci){
        if(item.isEmpty() || !item.is(SpearTags.SPEARS)) return;
        HumanoidArm arm=hand==InteractionHand.MAIN_HAND?player.getMainArm():player.getMainArm().getOpposite();
        int sign=arm==HumanoidArm.RIGHT?1:-1;
        ItemInHandRendererAccessor r=(ItemInHandRendererAccessor)(Object)this;
        ItemDisplayContext ctx=arm==HumanoidArm.RIGHT?ItemDisplayContext.FIRST_PERSON_RIGHT_HAND:ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        boolean useBranch = player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand()==hand;

        String currentHit = net.minecraft.client.Minecraft.getInstance().hitResult == null
                ? "NULL" : net.minecraft.client.Minecraft.getInstance().hitResult.getType().name();
        SpearSuperLog.renderBranch(
                player.level().getGameTime(), hand.name(), useBranch,
                player.isUsingItem(), player.getUseItemRemainingTicks(), swingProgress,
                equipProgress, currentHit
        );

        if (useBranch != usefulOres$lastBranchWasUse) {
            usefulOres$lastBranchWasUse = useBranch;
            long t = player.level().getGameTime();
            if (t != usefulOres$lastLogTick) {
                usefulOres$lastLogTick = t;
                Constants.LOG.info(
                        "[spear-anim-debug] branch changed to {} | isUsingItem={} useRemaining={} usedHand={} hand={} swingProgress={}",
                        useBranch ? "USE" : "ATTACK",
                        player.isUsingItem(), player.getUseItemRemainingTicks(), player.getUsedItemHand(), hand, swingProgress
                );
            }
        }

        if(useBranch){
            // Exact 1.21.11 SPEAR branch: the vanilla spear case supplies the
            // ordinary first-person item anchor, then applies SpearAnimations.
            // It deliberately does not use the normal equip-height transform.
            pose.translate((float)sign * 0.56F, -0.52F, -0.72F);
            float timeHeld=(float)item.getUseDuration(player)-((float)player.getUseItemRemainingTicks()-tickProgress+1.0F);
            VanillaSpearAnimations.firstPersonUse(10.0F, pose, timeHeld, arm, item);
        }else{
            r.usefulOres$invokeApplyItemArmTransform(pose,arm,0.0F);
            if(swingProgress>0.0F) {
                // V95d: removed an unexplained +0.09 vertical fudge offset that lived here.
                // It has no source in VanillaSpearAnimations/SpearAnimationProfile (unlike every
                // other transform in this class) and only became visible on discrete mob/air
                // jabs, where swingProgress resets to 0 between hits and the arm fully returns
                // to idle - continuous mining swings never let the arm rest, which is why it
                // looked "correct" there. Removing it re-aligns the jab pose with the idle/equip
                // pose in both cases.
                VanillaSpearAnimations.firstPersonAttack(swingProgress,pose,sign,arm);
            }
        }
        r.usefulOres$invokeRenderItem(player,item,ctx,pose,collector,light);
        ci.cancel();
    }
}

