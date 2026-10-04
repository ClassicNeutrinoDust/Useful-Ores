package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HumanoidArmorLayer.class)
public interface HumanoidArmorLayerInvoker {
    @Invoker("renderModel")
    void usefulOres$invokeRenderModel(PoseStack poseStack, MultiBufferSource bufferSource, int light,
                                      HumanoidModel<?> model, int overlay, ResourceLocation texture);
}
