package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.pedestal.DarkWormholePortalBlockEntity;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public class DarkWormholePortalGeoModel extends GeoModel<DarkWormholePortalBlockEntity> {

   private static final Identifier MODEL = Identifier.fromNamespaceAndPath(
      "useful_ores", "block/dark_wormhole_portal");
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
      "useful_ores", "textures/geo/dark_wormhole_portal.png");

   @Override
   public Identifier getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public Identifier getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public Identifier getAnimationResource(DarkWormholePortalBlockEntity animatable) {
      return null;
   }
}

