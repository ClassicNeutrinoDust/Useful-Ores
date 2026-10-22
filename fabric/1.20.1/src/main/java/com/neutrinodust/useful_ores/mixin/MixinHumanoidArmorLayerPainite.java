package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.neutrinodust.useful_ores.painite.PainitePower;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayerPainite {
    @Unique private LivingEntity usefulOres$currentEntity;
    @Unique private ItemStack usefulOres$currentStack;
    @Unique private EquipmentSlot usefulOres$currentSlot;
    @Unique private PoseStack usefulOres$poseStack;
    @Unique private MultiBufferSource usefulOres$bufferSource;
    @Unique private int usefulOres$light;
    @Unique private HumanoidModel<?> usefulOres$model;

    @Unique
    private static int animationFrame() {
        return (int)((System.currentTimeMillis() / 85L) % 12L);
    }

    @Unique
    private static ResourceLocation furyBaseAsset(EquipmentSlot slot) {
        String layer = slot == EquipmentSlot.LEGS ? "layer_2" : "layer_1";
        return new ResourceLocation("useful_ores",
                "textures/models/armor/painite_low_health_" + layer + ".png");
    }

    @Unique
    private static ResourceLocation furyCoreAsset(int frame) {
        return new ResourceLocation("useful_ores",
                String.format("textures/models/armor/painite_fury_core_%02d_layer_1.png", frame));
    }

    @Unique
    private boolean usefulOres$furyOnCurrentPiece() {
        return usefulOres$currentEntity != null
                && usefulOres$currentStack != null
                && usefulOres$currentSlot != null
                && PainitePower.isFuryActive(usefulOres$currentEntity)
                && PainitePower.isPainiteArmorItem(usefulOres$currentStack);
    }

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), require = 1)
    private void usefulOres$captureArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity,
                                               EquipmentSlot slot, int light, HumanoidModel<?> model, CallbackInfo ci) {
        this.usefulOres$currentEntity = entity;
        this.usefulOres$currentStack = entity.getItemBySlot(slot);
        this.usefulOres$currentSlot = slot;
        this.usefulOres$poseStack = poseStack;
        this.usefulOres$bufferSource = bufferSource;
        this.usefulOres$light = light;
        this.usefulOres$model = model;
    }

    @Inject(method = "renderArmorPiece", at = @At("RETURN"), require = 1)
    private void usefulOres$clearArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity,
                                             EquipmentSlot slot, int light, HumanoidModel<?> model, CallbackInfo ci) {
        this.usefulOres$currentEntity = null;
        this.usefulOres$currentStack = null;
        this.usefulOres$currentSlot = null;
        this.usefulOres$poseStack = null;
        this.usefulOres$bufferSource = null;
        this.usefulOres$model = null;
    }

    @Inject(method = "getArmorLocation", at = @At("HEAD"), cancellable = true, require = 1)
    private void usefulOres$furyBaseTexture(net.minecraft.world.item.ArmorItem armorItem, boolean inner,
                                            String suffix, CallbackInfoReturnable<ResourceLocation> cir) {
        if (usefulOres$furyOnCurrentPiece()) {
            cir.setReturnValue(furyBaseAsset(usefulOres$currentSlot));
        }
    }

    @Inject(method = "renderModel", at = @At("RETURN"), require = 1)
    private void usefulOres$furyCore(CallbackInfo ci) {
        if (!usefulOres$furyOnCurrentPiece() || usefulOres$currentSlot == EquipmentSlot.LEGS
                || usefulOres$bufferSource == null || usefulOres$model == null || usefulOres$poseStack == null) {
            return;
        }
        VertexConsumer consumer = usefulOres$bufferSource.getBuffer(
                RenderType.armorCutoutNoCull(furyCoreAsset(animationFrame())));
        usefulOres$model.renderToBuffer(usefulOres$poseStack, consumer, usefulOres$light,
                OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
