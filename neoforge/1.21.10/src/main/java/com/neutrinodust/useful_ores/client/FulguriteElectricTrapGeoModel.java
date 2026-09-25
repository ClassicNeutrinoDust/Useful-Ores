package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.block.FulguriteElectricTrapBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.ResourceLocation;

public class FulguriteElectricTrapGeoModel extends GeoModel<FulguriteElectricTrapBlockEntity> {

   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "block/fulgurite_electric_trap");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "textures/geo/fulgurite_electric_trap.png");

   @Override
   public ResourceLocation getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public ResourceLocation getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public ResourceLocation getAnimationResource(FulguriteElectricTrapBlockEntity animatable) {
      return null;
   }
}

