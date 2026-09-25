package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.pedestal.AncientPedestalBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import net.minecraft.resources.ResourceLocation;

public class AncientPedestalGeoModel extends GeoModel<AncientPedestalBlockEntity> {

   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "geo/block/ancient_pedestal.geo.json");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "textures/geo/ancient_pedestal.png");

   @Override
   public ResourceLocation getModelResource(AncientPedestalBlockEntity animatable, GeoRenderer<AncientPedestalBlockEntity> renderer) {
      return MODEL;
   }

   @Override
   public ResourceLocation getTextureResource(AncientPedestalBlockEntity animatable, GeoRenderer<AncientPedestalBlockEntity> renderer) {
      return TEXTURE;
   }

   @Override
   public ResourceLocation getAnimationResource(AncientPedestalBlockEntity animatable) {
      return null;
   }
}

