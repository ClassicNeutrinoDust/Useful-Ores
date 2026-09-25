package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.pedestal.DarkWormholePortalBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.ResourceLocation;

public class DarkWormholePortalGeoModel extends GeoModel<DarkWormholePortalBlockEntity> {

   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "block/dark_wormhole_portal");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "textures/geo/dark_wormhole_portal.png");

   @Override
   public ResourceLocation getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public ResourceLocation getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public ResourceLocation getAnimationResource(DarkWormholePortalBlockEntity animatable) {
      return null;
   }
}

