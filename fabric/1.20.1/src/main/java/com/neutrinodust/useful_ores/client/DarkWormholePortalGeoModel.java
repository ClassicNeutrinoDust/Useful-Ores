package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.pedestal.DarkWormholePortalBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

public class DarkWormholePortalGeoModel extends GeoModel<DarkWormholePortalBlockEntity> {

   private static final ResourceLocation MODEL = new ResourceLocation(
      "useful_ores", "geo/block/dark_wormhole_portal.geo.json");
   private static final ResourceLocation TEXTURE = new ResourceLocation(
      "useful_ores", "textures/geo/dark_wormhole_portal.png");

   @Override
   public ResourceLocation getModelResource(DarkWormholePortalBlockEntity animatable) {
      return MODEL;
   }

   @Override
   public ResourceLocation getTextureResource(DarkWormholePortalBlockEntity animatable) {
      return TEXTURE;
   }

   @Override
   public ResourceLocation getAnimationResource(DarkWormholePortalBlockEntity animatable) {
      return null;
   }
}

