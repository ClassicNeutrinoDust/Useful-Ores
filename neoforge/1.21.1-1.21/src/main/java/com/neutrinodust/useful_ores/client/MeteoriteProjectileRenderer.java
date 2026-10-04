package com.neutrinodust.useful_ores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.entity.MeteoriteProjectile;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionf;

public class MeteoriteProjectileRenderer<T extends MeteoriteProjectile> extends EntityRenderer<T> {
    private static final Quaternionf SCRATCH_QUAT = new Quaternionf();

    public MeteoriteProjectileRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pushPose();
        poseStack.translate(-0.25, 0.0, -0.25);
        poseStack.scale(0.6F, 0.6F, 0.6F);
        float age = entity.tickCount + partialTick;
        float spin = age * 12.0F;
        poseStack.mulPose(SCRATCH_QUAT.rotationAxis((float) Math.toRadians(spin), 0, 1, 0));
        poseStack.mulPose(SCRATCH_QUAT.rotationAxis((float) Math.toRadians(spin * 0.7F), 1, 0, 0));
        net.minecraft.client.Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                ModItems.METEORITE_BLOCK.get().defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
    @Override
    public net.minecraft.resources.ResourceLocation getTextureLocation(T entity) {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("useful_ores", "textures/block/meteorite_block.png");
    }

}
