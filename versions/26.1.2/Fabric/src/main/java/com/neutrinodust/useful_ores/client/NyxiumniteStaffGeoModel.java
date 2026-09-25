package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.item.NyxiumniteStaffItem;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public class NyxiumniteStaffGeoModel extends GeoModel<NyxiumniteStaffItem> {
    private static final Identifier MODEL =
        Identifier.fromNamespaceAndPath("useful_ores", "item/nyxiumnite_staff");
    private static final Identifier TEXTURE =
        Identifier.fromNamespaceAndPath("useful_ores", "textures/geo/nyxiumnite_staff_atlas_128.png");
    private static final Identifier ANIMATION =
        Identifier.fromNamespaceAndPath("useful_ores", "item/nyxiumnite_staff");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) { return MODEL; }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) { return TEXTURE; }

    @Override
    public Identifier getAnimationResource(NyxiumniteStaffItem animatable) { return ANIMATION; }
}

