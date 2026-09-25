package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.item.MeteorStaffItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MeteorStaffGeoModel extends GeoModel<MeteorStaffItem> {

   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "item/meteor_staff");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "textures/geo/meteor_staff_atlas.png");
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "item/meteor_staff");

   @Override
   public ResourceLocation getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public ResourceLocation getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public ResourceLocation getAnimationResource(MeteorStaffItem animatable) {
      return ANIMATION;
   }
}

