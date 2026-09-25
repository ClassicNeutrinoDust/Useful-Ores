package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.item.MeteorStaffItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MeteorStaffGeoModel extends GeoModel<MeteorStaffItem> {

   private static final Identifier MODEL = Identifier.fromNamespaceAndPath(
      "useful_ores", "item/meteor_staff");
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
      "useful_ores", "textures/geo/meteor_staff_atlas.png");
   private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(
      "useful_ores", "item/meteor_staff");

   @Override
   public Identifier getModelResource(GeoRenderState renderState) {
      return MODEL;
   }

   @Override
   public Identifier getTextureResource(GeoRenderState renderState) {
      return TEXTURE;
   }

   @Override
   public Identifier getAnimationResource(MeteorStaffItem animatable) {
      return ANIMATION;
   }
}

