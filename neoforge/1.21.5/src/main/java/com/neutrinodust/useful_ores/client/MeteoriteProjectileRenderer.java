package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.entity.MeteoriteProjectile;
import com.neutrinodust.useful_ores.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionf;

public class MeteoriteProjectileRenderer<T extends MeteoriteProjectile> extends EntityRenderer<T, MeteoriteProjectileRenderer.MeteoriteRenderState> {
    private static final Quaternionf SCRATCH_QUAT = new Quaternionf();

    public MeteoriteProjectileRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public MeteoriteRenderState createRenderState() {
        return new MeteoriteRenderState();
    }

    @Override
    public void extractRenderState(T entity, MeteoriteRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
    }

    @Override
    public void render(MeteoriteRenderState state, PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource bufferSource, int packedLight) {
        super.render(state, poseStack, bufferSource, packedLight);
        poseStack.pushPose();
        poseStack.translate(-0.25, 0.0, -0.25);
        poseStack.scale(0.6F, 0.6F, 0.6F);
        float spin = state.ageInTicks * 12.0F;
        poseStack.mulPose(SCRATCH_QUAT.rotationAxis((float) Math.toRadians(spin), 0, 1, 0));
        poseStack.mulPose(SCRATCH_QUAT.rotationAxis((float) Math.toRadians(spin * 0.7F), 1, 0, 0));
        net.minecraft.client.Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                ModItems.METEORITE_BLOCK.get().defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    public static class MeteoriteRenderState extends EntityRenderState {
    }
}
