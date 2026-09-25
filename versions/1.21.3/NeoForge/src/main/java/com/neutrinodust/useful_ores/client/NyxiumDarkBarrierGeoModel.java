package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.barrier.NyxiumDarkBarrierBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import net.minecraft.resources.ResourceLocation;

public class NyxiumDarkBarrierGeoModel extends GeoModel<NyxiumDarkBarrierBlockEntity> {

   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "geo/block/nyxium_dark_barrier.geo.json");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "textures/geo/nyxium_dark_barrier.png");

   @Override
   public ResourceLocation getModelResource(NyxiumDarkBarrierBlockEntity animatable, GeoRenderer<NyxiumDarkBarrierBlockEntity> renderer) {
      return MODEL;
   }

   @Override
   public ResourceLocation getTextureResource(NyxiumDarkBarrierBlockEntity animatable, GeoRenderer<NyxiumDarkBarrierBlockEntity> renderer) {
      return TEXTURE;
   }

   @Override
   public ResourceLocation getAnimationResource(NyxiumDarkBarrierBlockEntity animatable) {
      return null;
   }
}

