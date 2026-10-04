package com.neutrinodust.useful_ores.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.neutrinodust.useful_ores.entity.NyxiumniteCubeProjectile;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionf;

public class NyxiumniteCubeProjectileRenderer<T extends NyxiumniteCubeProjectile> extends EntityRenderer<T> {
    private static final Quaternionf SCRATCH_QUAT = new Quaternionf();

    public NyxiumniteCubeProjectileRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pushPose();
        poseStack.translate(-0.2, 0.0, -0.2);
        poseStack.scale(0.4F, 0.4F, 0.4F);
        float age = entity.tickCount + partialTick;
        float spin = age * 20.0F;
        poseStack.mulPose(SCRATCH_QUAT.rotationAxis((float) Math.toRadians(spin), 0, 1, 0));
        poseStack.mulPose(SCRATCH_QUAT.rotationAxis((float) Math.toRadians(spin * 1.3F), 0, 0, 1));
        net.minecraft.client.Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                ModItems.NYXIUM_BLOCKS.get(0).get().defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
    @Override
    public net.minecraft.resources.ResourceLocation getTextureLocation(T entity) {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("useful_ores", "textures/block/nyxium_block.png");
    }

}
