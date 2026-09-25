package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.item.MeteorStaffItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class MeteorStaffGeoModel extends GeoModel<MeteorStaffItem> {

   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "geo/item/meteor_staff.geo.json");
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "textures/geo/meteor_staff_atlas.png");
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(
      "useful_ores", "animations/item/meteor_staff.animation.json");

   @Override
   public ResourceLocation getModelResource(MeteorStaffItem animatable, GeoRenderer<MeteorStaffItem> renderer) {
      return MODEL;
   }

   @Override
   public ResourceLocation getTextureResource(MeteorStaffItem animatable, GeoRenderer<MeteorStaffItem> renderer) {
      return TEXTURE;
   }

   @Override
   public ResourceLocation getAnimationResource(MeteorStaffItem animatable) {
      return ANIMATION;
   }
}

