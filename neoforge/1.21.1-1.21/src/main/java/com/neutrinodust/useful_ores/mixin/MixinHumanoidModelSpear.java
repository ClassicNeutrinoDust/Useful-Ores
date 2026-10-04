package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.client.spear.VanillaSpearAnimations;
import com.neutrinodust.useful_ores.client.spear.SpearTpvDebug;
import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HumanoidModel.class)
public abstract class MixinHumanoidModelSpear {
    private boolean usefulOres$isSpearAttack(LivingEntity entity) {
        ItemStack stack = entity.getMainHandItem();
        boolean usingSpear = entity.isUsingItem() && !entity.getUseItem().isEmpty()
                && entity.getUseItem().is(SpearTags.SPEARS);
        return !usingSpear && entity.getAttackAnim(0.0F) > 0.0F
                && !stack.isEmpty() && stack.is(SpearTags.SPEARS);
    }

    @Inject(method = "setupAttackAnimation", at = @At("HEAD"), cancellable = true, require = 0)
    private void usefulOres$cancelGenericSpearSwing(LivingEntity entity, float ageInTicks, CallbackInfo ci) {
        if (usefulOres$isSpearAttack(entity)) ci.cancel();
    }

    @Inject(method = "setupAnim", at = @At("TAIL"), require = 1)
    private void usefulOres$applySpearStabPose(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                                 float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        HumanoidModel<?> model = (HumanoidModel<?>) (Object) this;
        float attack = entity.getAttackAnim(0.0F);
        boolean usingSpear = entity.isUsingItem() && !entity.getUseItem().isEmpty()
                && entity.getUseItem().is(SpearTags.SPEARS);
        if (!usingSpear && attack > 0.0F && entity.getMainHandItem().is(SpearTags.SPEARS)) {
            VanillaSpearAnimations.thirdPersonAttackHand(model, entity.getMainArm(), attack);
        }

        if (entity.isUsingItem() && !entity.getUseItem().isEmpty() && entity.getUseItem().is(SpearTags.SPEARS)) {
            ItemStack debugStack = entity.getUseItem();
            HumanoidArm debugArm = entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
            SpearTpvDebug.handInput(entity, debugArm, debugStack, entity.getTicksUsingItem(), attack, model);
            VanillaSpearAnimations.thirdPersonHandUse(model, entity, debugStack);
            SpearTpvDebug.handOutput(entity, debugArm, debugStack, entity.getTicksUsingItem(), model);
        }
    }
}
