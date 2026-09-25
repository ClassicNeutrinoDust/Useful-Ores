package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.item.NyxiumniteStaffItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.ResourceLocation;

public class NyxiumniteStaffGeoModel extends GeoModel<NyxiumniteStaffItem> {
    private static final ResourceLocation MODEL =
        ResourceLocation.fromNamespaceAndPath("useful_ores", "item/nyxiumnite_staff");
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath("useful_ores", "textures/geo/nyxiumnite_staff_atlas_128.png");
    private static final ResourceLocation ANIMATION =
        ResourceLocation.fromNamespaceAndPath("useful_ores", "item/nyxiumnite_staff");

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) { return MODEL; }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) { return TEXTURE; }

    @Override
    public ResourceLocation getAnimationResource(NyxiumniteStaffItem animatable) { return ANIMATION; }
}

