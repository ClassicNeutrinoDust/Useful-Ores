package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.painite.PainitePower;
import com.neutrinodust.useful_ores.painite.PainiteRenderStateAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayerPainite {
    @Unique private HumanoidRenderState usefulOres$currentRenderState;

    private static ResourceKey<EquipmentAsset> furyAsset(int frame) {
        String id = frame == 0 ? "painite_low_health" : String.format("painite_low_health_%02d", frame);
        return ResourceKey.create(EquipmentAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath("useful_ores", id));
    }

    private static int animationFrame() {
        return (int)((System.currentTimeMillis() / 85L) % 12L);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void usefulOres$captureArmorRenderState(
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
            HumanoidRenderState state, float yRot, float xRot, CallbackInfo ci) {
        this.usefulOres$currentRenderState = state;
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void usefulOres$clearArmorRenderState(
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
            HumanoidRenderState state, float yRot, float xRot, CallbackInfo ci) {
        this.usefulOres$currentRenderState = null;
    }

    @Redirect(
        method = "renderArmorPiece",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
        )
    )
    private void usefulOres$renderPainiteFury(
        EquipmentLayerRenderer renderer,
        EquipmentClientInfo.LayerType layerType,
        ResourceKey<EquipmentAsset> asset,
        Model model,
        ItemStack stack,
        PoseStack pose,
        MultiBufferSource bufferSource,
        int light
    ) {
        if (this.usefulOres$currentRenderState != null
                && this.usefulOres$currentRenderState instanceof PainiteRenderStateAccess access
                && access.usefulOres$isPainiteFury()
                && PainitePower.isPainiteArmorItem(stack)) {
            asset = furyAsset(animationFrame());
        }
        renderer.renderLayers(layerType, asset, model, stack, pose, bufferSource, light);
    }
}
