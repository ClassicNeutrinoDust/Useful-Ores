package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.item.NyxiumniteStaffItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import net.minecraft.resources.ResourceLocation;

public class NyxiumniteStaffGeoModel extends GeoModel<NyxiumniteStaffItem> {
    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath("useful_ores", "geo/item/nyxiumnite_staff.geo.json");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath("useful_ores", "textures/geo/nyxiumnite_staff_atlas_128.png");
    private static final ResourceLocation ANIMATION =
        ResourceLocation.fromNamespaceAndPath("useful_ores", "animations/item/nyxiumnite_staff.animation.json");

    @Override
    public ResourceLocation getModelResource(NyxiumniteStaffItem animatable, GeoRenderer<NyxiumniteStaffItem> renderer) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(NyxiumniteStaffItem animatable, GeoRenderer<NyxiumniteStaffItem> renderer) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(NyxiumniteStaffItem animatable) { return ANIMATION; }
}

