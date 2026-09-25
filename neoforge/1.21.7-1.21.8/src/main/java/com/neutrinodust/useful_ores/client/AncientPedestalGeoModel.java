package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.pedestal.AncientPedestalBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.ResourceLocation;

public class AncientPedestalGeoModel extends GeoModel<AncientPedestalBlockEntity> {

   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "block/ancient_pedestal");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "textures/geo/ancient_pedestal.png");

   @Override
   public ResourceLocation getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public ResourceLocation getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public ResourceLocation getAnimationResource(AncientPedestalBlockEntity animatable) {
      return null;
   }
}

