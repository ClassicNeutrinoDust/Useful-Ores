package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.client.spear.SpearRenderContext;
import com.neutrinodust.useful_ores.client.spear.SpearTpvDebug;
import com.neutrinodust.useful_ores.client.spear.VanillaSpearAnimations;
import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;





@Mixin(ItemRenderer.class)
public abstract class MixinItemRendererSpear {
    private static final String RENDER_METHOD =
            "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Z"
                    + "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II"
                    + "Lnet/minecraft/client/resources/model/BakedModel;)V";

    @Inject(method = RENDER_METHOD, at = @At("HEAD"), require = 1, remap = true)
    private void usefulOres$applySpearPose(ItemStack itemStack, ItemDisplayContext displayContext,
                                            boolean leftHanded, PoseStack poseStack, MultiBufferSource bufferSource,
                                            int light, int overlay, BakedModel model, CallbackInfo ci) {
        SpearRenderContext.Context context = SpearRenderContext.current();
        if (context == null || itemStack.isEmpty() || !itemStack.is(SpearTags.SPEARS)) return;

        HumanoidArm arm = context.arm();
        var entity = context.entity();
        float attack = entity.getAttackAnim(0.0F);
        float ticksUsing = entity.getTicksUsingItem();
        boolean matched = context.displayContext() == displayContext && context.stack().getItem() == itemStack.getItem();
        SpearTpvDebug.itemInvocation(entity, itemStack, displayContext, leftHanded, matched, attack, ticksUsing);
        SpearTpvDebug.poseMatrix("ITEM_BEFORE", poseStack.last().pose());
        if (!matched) return;
        if (attack > 0.0F && entity.getMainArm() == arm && entity.getMainHandItem().is(SpearTags.SPEARS)) {
            VanillaSpearAnimations.thirdPersonAttackItem(poseStack, attack, itemStack);
        }

        if (ticksUsing != 0.0F) {
            VanillaSpearAnimations.thirdPersonUseItem(poseStack, ticksUsing, attack, arm, itemStack);
            SpearTpvDebug.poseMatrix("ITEM_AFTER_USE", poseStack.last().pose());
        }
    }
}
