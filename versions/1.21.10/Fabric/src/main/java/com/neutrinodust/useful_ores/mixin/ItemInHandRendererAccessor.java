package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemInHandRenderer.class)
public interface ItemInHandRendererAccessor {
    @Invoker("method_3217")
    void usefulOres$invokeApplyItemArmAttackTransform(PoseStack pose, HumanoidArm arm, float swingProgress);

    @Invoker("method_3224")
    void usefulOres$invokeApplyItemArmTransform(PoseStack pose, HumanoidArm arm, float equipProgress);

    @Invoker("method_3233")
    void usefulOres$invokeRenderItem(
            LivingEntity entity,
            ItemStack stack,
            ItemDisplayContext renderMode,
            PoseStack pose,
            SubmitNodeCollector collector,
            int light
    );
}
