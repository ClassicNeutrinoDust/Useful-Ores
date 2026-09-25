package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.client.spear.VanillaSpearAnimations;
import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Replaces the ordinary sword/axe swing layer with the vanilla spear stab pose. */
@Mixin(HumanoidModel.class)
public abstract class MixinHumanoidModelSpear {
    private static ItemStack usefulOres$handStack(HumanoidRenderState state, HumanoidArm arm) {
        return arm == HumanoidArm.RIGHT ? state.rightHandItem : state.leftHandItem;
    }

    private static boolean usefulOres$isSpearAttack(HumanoidRenderState state) {
        HumanoidArm arm = state.attackArm;
        ItemStack stack = usefulOres$handStack(state, arm);
        return state.attackTime > 0.0F
                && arm == state.mainArm
                && !stack.isEmpty()
                && stack.is(SpearTags.SPEARS);
    }

    @Inject(method = "setupAttackAnimation", at = @At("HEAD"), cancellable = true, require = 0)
    private void usefulOres$cancelGenericSpearSwing(
            HumanoidRenderState state,
            float ageInTicks,
            CallbackInfo ci
    ) {
        if (usefulOres$isSpearAttack(state)) {
            ci.cancel();
        }
    }

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void usefulOres$applySpearStabPose(HumanoidRenderState state, CallbackInfo ci) {
        HumanoidModel<?> model = (HumanoidModel<?>) (Object) this;
        if (usefulOres$isSpearAttack(state)) {
            VanillaSpearAnimations.thirdPersonAttackHand(model, state);
        }

        InteractionHand hand = state.useItemHand;
        ItemStack active = hand == InteractionHand.MAIN_HAND
                ? state.rightHandItem
                : state.leftHandItem;
        if (state.isUsingItem && !active.isEmpty() && active.is(SpearTags.SPEARS)) {
            VanillaSpearAnimations.thirdPersonHandUse(model, state, active);
        }
    }
}
