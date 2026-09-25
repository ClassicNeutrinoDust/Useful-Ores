package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.entity.MeteoriteProjectile;
import com.neutrinodust.useful_ores.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionf;

public class MeteoriteProjectileRenderer<T extends MeteoriteProjectile>
      extends EntityRenderer<T, MeteoriteProjectileRenderer.MeteoriteRenderState> {

   private static final BlockDisplayContext DISPLAY_CONTEXT = BlockDisplayContext.create();

   private static final Quaternionf SCRATCH_QUAT = new Quaternionf();

   private final BlockModelResolver blockModelResolver;

   public MeteoriteProjectileRenderer(EntityRendererProvider.Context ctx) {
      super(ctx);
      this.blockModelResolver = ctx.getBlockModelResolver();
   }

   @Override
   public MeteoriteRenderState createRenderState() {
      return new MeteoriteRenderState();
   }

   @Override
   public void extractRenderState(T entity, MeteoriteRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      this.blockModelResolver.update(state.blockModel, ModItems.METEORITE_BLOCK.get().defaultBlockState(), DISPLAY_CONTEXT);
   }

   @Override
   public void submit(MeteoriteRenderState state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState) {
      super.submit(state, poseStack, collector, cameraState);

      poseStack.pushPose();
      poseStack.translate(-0.25, 0.0, -0.25);
      poseStack.scale(0.6F, 0.6F, 0.6F);

      float spin = state.ageInTicks * 12.0F;
      poseStack.mulPose(new org.joml.Matrix4f().rotation(SCRATCH_QUAT.rotationAxis((float) Math.toRadians(spin), 0, 1, 0)));
      poseStack.mulPose(new org.joml.Matrix4f().rotation(SCRATCH_QUAT.rotationAxis((float) Math.toRadians(spin * 0.7F), 1, 0, 0)));

      state.blockModel.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);

      poseStack.popPose();
   }

   public static class MeteoriteRenderState extends EntityRenderState {
      public final BlockModelRenderState blockModel = new BlockModelRenderState();
   }
}

