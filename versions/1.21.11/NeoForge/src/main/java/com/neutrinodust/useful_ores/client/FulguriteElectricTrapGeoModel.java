package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.block.FulguriteElectricTrapBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public class FulguriteElectricTrapGeoModel extends GeoModel<FulguriteElectricTrapBlockEntity> {

   private static final Identifier MODEL = Identifier.fromNamespaceAndPath(
      "useful_ores", "block/fulgurite_electric_trap");
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
      "useful_ores", "textures/geo/fulgurite_electric_trap.png");

   @Override
   public Identifier getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public Identifier getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public Identifier getAnimationResource(FulguriteElectricTrapBlockEntity animatable) {
      return null;
   }
}

