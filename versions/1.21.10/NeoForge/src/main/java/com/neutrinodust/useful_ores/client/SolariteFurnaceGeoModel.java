package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.solar.SolariteFurnaceBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.constant.dataticket.DataTicket;

public class SolariteFurnaceGeoModel extends GeoModel<SolariteFurnaceBlockEntity> {

    public static final DataTicket<Boolean> LIT =
            DataTicket.create("useful_ores_furnace_lit", Boolean.class);

    public static final DataTicket<Direction> FACING =
            DataTicket.create("useful_ores_furnace_facing", Direction.class);

    private static final ResourceLocation MODEL_OFF = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "block/solarite_furnace_off");
    private static final ResourceLocation MODEL_ON = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "block/solarite_furnace_on");
    private static final ResourceLocation TEXTURE_OFF = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "textures/geo/solarite_furnace_off.png");
    private static final ResourceLocation TEXTURE_ON = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "textures/geo/solarite_furnace_on.png");

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return isLit(renderState) ? MODEL_ON : MODEL_OFF;
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return isLit(renderState) ? TEXTURE_ON : TEXTURE_OFF;
    }

    private boolean isLit(GeoRenderState renderState) {
        Boolean lit = renderState.getGeckolibData(LIT);
        return lit != null && lit;
    }

    @Override
    public ResourceLocation getAnimationResource(SolariteFurnaceBlockEntity animatable) {
        return null;
    }
}

