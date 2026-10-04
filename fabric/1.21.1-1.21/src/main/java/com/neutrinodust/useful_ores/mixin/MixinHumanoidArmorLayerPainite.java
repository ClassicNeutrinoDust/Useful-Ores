package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.painite.PainitePower;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayerPainite {
    @Unique private LivingEntity usefulOres$currentEntity;
    @Unique private ItemStack usefulOres$currentStack;
    @Unique private EquipmentSlot usefulOres$currentSlot;

    @Unique
    private static int animationFrame() {
        return (int)((System.currentTimeMillis() / 85L) % 12L);
    }

    





    @Unique
    private static ResourceLocation furyBaseAsset(EquipmentSlot slot) {
        String layer = slot == EquipmentSlot.LEGS ? "layer_2" : "layer_1";
        return ResourceLocation.fromNamespaceAndPath("useful_ores",
                "textures/models/armor/painite_low_health_" + layer + ".png");
    }

    @Unique
    private static ResourceLocation furyCoreAsset(int frame) {
        return ResourceLocation.fromNamespaceAndPath("useful_ores",
                String.format("textures/models/armor/painite_fury_core_%02d_layer_1.png", frame));
    }

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), require = 1)
    private void usefulOres$captureArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity,
                                               EquipmentSlot slot, int light, HumanoidModel<?> model, CallbackInfo ci) {
        this.usefulOres$currentEntity = entity;
        this.usefulOres$currentStack = entity.getItemBySlot(slot);
        this.usefulOres$currentSlot = slot;
    }

    @Inject(method = "renderArmorPiece", at = @At("RETURN"), require = 1)
    private void usefulOres$clearArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity,
                                             EquipmentSlot slot, int light, HumanoidModel<?> model, CallbackInfo ci) {
        this.usefulOres$currentEntity = null;
        this.usefulOres$currentStack = null;
        this.usefulOres$currentSlot = null;
    }

    @Redirect(
        method = "renderArmorPiece",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/HumanoidModel;ILnet/minecraft/resources/ResourceLocation;)V"
        ),
        require = 0
    )
    private void usefulOres$renderPainiteFury(HumanoidArmorLayer<?, ?, ?> layer, PoseStack poseStack,
                                              MultiBufferSource bufferSource, int light, HumanoidModel<?> model,
                                              int overlay, ResourceLocation asset) {
        if (usefulOres$currentEntity != null
                && usefulOres$currentStack != null
                && usefulOres$currentSlot != null
                && PainitePower.isFuryActive(usefulOres$currentEntity)
                && PainitePower.isPainiteArmorItem(usefulOres$currentStack)) {
            HumanoidArmorLayerInvoker invoker = (HumanoidArmorLayerInvoker) (Object) layer;
            
            invoker.usefulOres$invokeRenderModel(poseStack, bufferSource, light, model, overlay,
                    furyBaseAsset(usefulOres$currentSlot));
            
            if (usefulOres$currentSlot != EquipmentSlot.LEGS) {
                invoker.usefulOres$invokeRenderModel(poseStack, bufferSource, light, model, overlay,
                        furyCoreAsset(animationFrame()));
            }
            return;
        }
        ((HumanoidArmorLayerInvoker) (Object) layer).usefulOres$invokeRenderModel(poseStack, bufferSource, light, model, overlay, asset);
    }
}
