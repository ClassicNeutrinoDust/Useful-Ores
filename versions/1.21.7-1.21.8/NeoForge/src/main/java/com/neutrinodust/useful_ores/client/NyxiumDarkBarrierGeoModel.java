package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.barrier.NyxiumDarkBarrierBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.ResourceLocation;

public class NyxiumDarkBarrierGeoModel extends GeoModel<NyxiumDarkBarrierBlockEntity> {

   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "block/nyxium_dark_barrier");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "textures/geo/nyxium_dark_barrier.png");

   @Override
   public ResourceLocation getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public ResourceLocation getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public ResourceLocation getAnimationResource(NyxiumDarkBarrierBlockEntity animatable) {
      return null;
   }
}

