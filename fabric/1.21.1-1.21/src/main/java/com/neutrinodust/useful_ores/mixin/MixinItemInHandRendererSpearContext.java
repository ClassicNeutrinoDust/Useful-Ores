package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.client.spear.SpearRenderContext;
import com.neutrinodust.useful_ores.client.spear.SpearTpvDebug;
import com.neutrinodust.useful_ores.client.spear.VanillaSpearAnimations;
import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;






@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRendererSpearContext {
    private static boolean usefulOres$isThirdPerson(ItemDisplayContext context) {
        return context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;"
                    + "Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), require = 1, remap = true)
    private void usefulOres$capture(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext,
                                    boolean leftHanded, PoseStack poseStack, MultiBufferSource bufferSource,
                                    int seed, CallbackInfo ci) {
        if (usefulOres$isThirdPerson(displayContext)) {
            SpearRenderContext.push(entity, stack, displayContext, leftHanded);
            if (!stack.isEmpty() && stack.is(SpearTags.SPEARS)) {
                SpearTpvDebug.context("PUSH", entity, stack, displayContext, leftHanded);
            }
        }
    }

    

    @Inject(
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;"
                    + "Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/entity/LivingEntity;"
                            + "Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Z"
                            + "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;"
                            + "Lnet/minecraft/world/level/Level;III)V"),
            require = 1, remap = true)
    private void usefulOres$applySpearPose(LivingEntity entity, ItemStack stack,
                                           ItemDisplayContext displayContext, boolean leftHanded,
                                           PoseStack poseStack, MultiBufferSource bufferSource,
                                           int seed, CallbackInfo ci) {
        if (!usefulOres$isThirdPerson(displayContext) || stack.isEmpty() || !stack.is(SpearTags.SPEARS)) return;
        HumanoidArm arm = displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
        boolean usingSpear = entity.isUsingItem() && entity.getUseItem().is(SpearTags.SPEARS);
        float attack = usingSpear ? 0.0F : entity.getAttackAnim(0.0F);
        float ticksUsing = entity.getTicksUsingItem();
        if (attack > 0.0F && entity.getMainArm() == arm && entity.getMainHandItem().is(SpearTags.SPEARS)) {
            VanillaSpearAnimations.thirdPersonAttackItem(poseStack, attack, stack);
        }
        if (ticksUsing != 0.0F) {
            VanillaSpearAnimations.thirdPersonUseItem(poseStack, ticksUsing, attack, arm, stack);
        }
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;"
                    + "Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN"), require = 1, remap = true)
    private void usefulOres$clear(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext,
                                  boolean leftHanded, PoseStack poseStack, MultiBufferSource bufferSource,
                                  int seed, CallbackInfo ci) {
        if (usefulOres$isThirdPerson(displayContext)) {
            if (!stack.isEmpty() && stack.is(SpearTags.SPEARS)) {
                SpearTpvDebug.context("POP", entity, stack, displayContext, leftHanded);
            }
            SpearRenderContext.pop();
        }
    }
}
