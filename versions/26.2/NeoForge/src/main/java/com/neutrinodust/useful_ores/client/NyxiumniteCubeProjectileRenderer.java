package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.entity.NyxiumniteCubeProjectile;
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

public class NyxiumniteCubeProjectileRenderer<T extends NyxiumniteCubeProjectile>
      extends EntityRenderer<T, NyxiumniteCubeProjectileRenderer.CubeRenderState> {

   private static final BlockDisplayContext DISPLAY_CONTEXT = BlockDisplayContext.create();

   private static final Quaternionf SCRATCH_QUAT = new Quaternionf();

   private final BlockModelResolver blockModelResolver;

   public NyxiumniteCubeProjectileRenderer(EntityRendererProvider.Context ctx) {
      super(ctx);
      this.blockModelResolver = ctx.getBlockModelResolver();
   }

   @Override
   public CubeRenderState createRenderState() {
      return new CubeRenderState();
   }

   @Override
   public void extractRenderState(T entity, CubeRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      this.blockModelResolver.update(state.blockModel, ModItems.NYXIUM_BLOCKS.get(0).get().defaultBlockState(), DISPLAY_CONTEXT);
   }

   @Override
   public void submit(CubeRenderState state, PoseStack poseStack,
                       SubmitNodeCollector collector, CameraRenderState cameraState) {
      super.submit(state, poseStack, collector, cameraState);

      poseStack.pushPose();
      poseStack.translate(-0.2, 0.0, -0.2);
      poseStack.scale(0.4F, 0.4F, 0.4F);

      float spin = state.ageInTicks * 20.0F;
      poseStack.mulPose(SCRATCH_QUAT.rotationAxis((float) Math.toRadians(spin), 0, 1, 0));
      poseStack.mulPose(SCRATCH_QUAT.rotationAxis((float) Math.toRadians(spin * 1.3F), 0, 0, 1));

      state.blockModel.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);

      poseStack.popPose();
   }

   public static class CubeRenderState extends EntityRenderState {
      public final BlockModelRenderState blockModel = new BlockModelRenderState();
   }
}

