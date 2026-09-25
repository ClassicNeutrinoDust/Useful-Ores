package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Accesses the actual Minecraft 1.21.4 Mojmap ItemInHandRenderer helpers. */
@Mixin(ItemInHandRenderer.class)
public interface ItemInHandRendererAccessor {
    @Invoker("applyItemArmAttackTransform")
    void usefulOres$invokeApplyItemArmAttackTransform(PoseStack pose, HumanoidArm arm, float swingProgress);

    @Invoker("applyItemArmTransform")
    void usefulOres$invokeApplyItemArmTransform(PoseStack pose, HumanoidArm arm, float equipProgress);

    @Invoker("renderItem")
    void usefulOres$invokeRenderItem(
            LivingEntity entity,
            ItemStack stack,
            ItemDisplayContext renderMode,
            boolean leftHanded,
            PoseStack pose,
            MultiBufferSource bufferSource,
            int light
    );
}
