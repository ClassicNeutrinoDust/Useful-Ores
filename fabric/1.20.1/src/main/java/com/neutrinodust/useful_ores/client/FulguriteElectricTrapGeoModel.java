package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.block.FulguriteElectricTrapBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

public class FulguriteElectricTrapGeoModel extends GeoModel<FulguriteElectricTrapBlockEntity> {

   private static final ResourceLocation MODEL = new ResourceLocation(
      "useful_ores", "geo/block/fulgurite_electric_trap.geo.json");
   private static final ResourceLocation TEXTURE = new ResourceLocation(
      "useful_ores", "textures/geo/fulgurite_electric_trap.png");

   @Override
   public ResourceLocation getModelResource(FulguriteElectricTrapBlockEntity animatable) {
      return MODEL;
   }

   @Override
   public ResourceLocation getTextureResource(FulguriteElectricTrapBlockEntity animatable) {
      return TEXTURE;
   }

   @Override
   public ResourceLocation getAnimationResource(FulguriteElectricTrapBlockEntity animatable) {
      return null;
   }
}

