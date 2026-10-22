package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.painite.PainitePower;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayerPainite {
    @Unique private LivingEntity usefulOres$entity;
    @Unique private EquipmentSlot usefulOres$slot;
    @Unique private boolean usefulOres$coreDrawn;

    @Unique
    private static int usefulOres$animationFrame() {
        return (int) ((System.currentTimeMillis() / 85L) % 12L);
    }

    @Unique
    private static ResourceLocation usefulOres$furyCoreAsset(int frame) {
        return ResourceLocation.fromNamespaceAndPath("useful_ores", String.format("textures/models/armor/painite_fury_core_%02d_layer_1.png", frame));
    }

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), require = 1)
    private void usefulOres$begin(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity,
                                  EquipmentSlot slot, int packedLight, HumanoidModel<?> model, CallbackInfo ci) {
        this.usefulOres$entity = entity;
        this.usefulOres$slot = slot;
        this.usefulOres$coreDrawn = false;
    }

    @Inject(method = "renderTrim", at = @At("HEAD"), require = 0)
    private void usefulOres$coreBeforeTrim(@org.spongepowered.asm.mixin.injection.Coerce Object material, PoseStack poseStack,
                                           MultiBufferSource bufferSource, int packedLight,
                                           @org.spongepowered.asm.mixin.injection.Coerce Object trim, HumanoidModel<?> model,
                                           boolean innerModel, CallbackInfo ci) {
        usefulOres$drawCore(poseStack, bufferSource, packedLight, model);
    }

    @Inject(method = "renderArmorPiece", at = @At("RETURN"), require = 1)
    private void usefulOres$end(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity,
                                EquipmentSlot slot, int packedLight, HumanoidModel<?> model, CallbackInfo ci) {
        usefulOres$drawCore(poseStack, bufferSource, packedLight, model);
        this.usefulOres$entity = null;
        this.usefulOres$slot = null;
    }

    @Unique
    private void usefulOres$drawCore(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, HumanoidModel<?> model) {
        LivingEntity entity = this.usefulOres$entity;
        EquipmentSlot slot = this.usefulOres$slot;
        if (this.usefulOres$coreDrawn || entity == null || slot == null || slot == EquipmentSlot.LEGS) return;
        ItemStack stack = entity.getItemBySlot(slot);
        if (!PainitePower.isFuryActive(entity) || !PainitePower.isPainiteArmorItem(stack)) return;
        this.usefulOres$coreDrawn = true;

        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        model.renderToBuffer(poseStack,
                bufferSource.getBuffer(RenderType.armorCutoutNoCull(usefulOres$furyCoreAsset(usefulOres$animationFrame()))),
                packedLight, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
