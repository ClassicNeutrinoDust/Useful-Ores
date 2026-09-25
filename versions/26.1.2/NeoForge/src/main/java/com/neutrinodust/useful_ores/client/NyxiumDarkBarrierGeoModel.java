package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.barrier.NyxiumDarkBarrierBlockEntity;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public class NyxiumDarkBarrierGeoModel extends GeoModel<NyxiumDarkBarrierBlockEntity> {

   private static final Identifier MODEL = Identifier.fromNamespaceAndPath(
      "useful_ores", "block/nyxium_dark_barrier");
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
      "useful_ores", "textures/geo/nyxium_dark_barrier.png");

   @Override
   public Identifier getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public Identifier getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public Identifier getAnimationResource(NyxiumDarkBarrierBlockEntity animatable) {
      return null;
   }
}

