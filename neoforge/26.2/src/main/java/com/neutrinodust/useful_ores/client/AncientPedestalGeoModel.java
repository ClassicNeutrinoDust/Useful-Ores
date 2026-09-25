package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.pedestal.AncientPedestalBlockEntity;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public class AncientPedestalGeoModel extends GeoModel<AncientPedestalBlockEntity> {

   private static final Identifier MODEL = Identifier.fromNamespaceAndPath(
      "useful_ores", "block/ancient_pedestal");
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
      "useful_ores", "textures/geo/ancient_pedestal.png");

   @Override
   public Identifier getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public Identifier getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public Identifier getAnimationResource(AncientPedestalBlockEntity animatable) {
      return null;
   }
}

