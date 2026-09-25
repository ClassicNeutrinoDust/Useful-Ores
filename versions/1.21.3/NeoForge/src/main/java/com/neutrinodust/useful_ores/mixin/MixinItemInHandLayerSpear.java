package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.client.spear.VanillaSpearAnimations;
import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Applies the 1.21.4 spear item-layer pose immediately before Minecraft renders the item.
 * 1.21.3 still passes the stack/model directly to ItemRenderer, so no ItemStackRenderState
 * bridge is necessary.
 */
@Mixin(ItemInHandLayer.class)
public abstract class MixinItemInHandLayerSpear {
    @Inject(
            method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
            ),
            require = 1
    )
    private void usefulOres$applySpearPose(
            LivingEntityRenderState state,
            BakedModel model,
            ItemStack stack,
            ItemDisplayContext renderMode,
            HumanoidArm arm,
            PoseStack pose,
            MultiBufferSource bufferSource,
            int light,
            CallbackInfo ci
    ) {
        if (!(state instanceof HumanoidRenderState humanoid)
                || stack.isEmpty()
                || !stack.is(SpearTags.SPEARS)) {
            return;
        }

        if (humanoid.attackTime > 0.0F && humanoid.mainArm == arm) {
            VanillaSpearAnimations.thirdPersonAttackItem(state, pose, humanoid.attackTime, stack);
        }

        float timeHeld = humanoid.ticksUsingItem;
        if (timeHeld != 0.0F) {
            VanillaSpearAnimations.thirdPersonUseItem(
                    state, pose, timeHeld, humanoid.attackTime, arm, stack);
        }
    }
}
