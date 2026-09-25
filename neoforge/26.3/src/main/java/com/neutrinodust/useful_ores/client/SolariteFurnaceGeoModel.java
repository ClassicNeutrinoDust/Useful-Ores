package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.solar.SolariteFurnaceBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.constant.dataticket.DataTicket;

public class SolariteFurnaceGeoModel extends GeoModel<SolariteFurnaceBlockEntity> {

    public static final DataTicket<Boolean> LIT =
            DataTicket.create("useful_ores_furnace_lit", Boolean.class);

    public static final DataTicket<Direction> FACING =
            DataTicket.create("useful_ores_furnace_facing", Direction.class);

    private static final Identifier MODEL_OFF = Identifier.fromNamespaceAndPath(
            "useful_ores", "block/solarite_furnace_off");
    private static final Identifier MODEL_ON = Identifier.fromNamespaceAndPath(
            "useful_ores", "block/solarite_furnace_on");
    private static final Identifier TEXTURE_OFF = Identifier.fromNamespaceAndPath(
            "useful_ores", "textures/geo/solarite_furnace_off.png");
    private static final Identifier TEXTURE_ON = Identifier.fromNamespaceAndPath(
            "useful_ores", "textures/geo/solarite_furnace_on.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return isLit(renderState) ? MODEL_ON : MODEL_OFF;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return isLit(renderState) ? TEXTURE_ON : TEXTURE_OFF;
    }

    private boolean isLit(GeoRenderState renderState) {
        Boolean lit = renderState.getGeckolibData(LIT);
        return lit != null && lit;
    }

    @Override
    public Identifier getAnimationResource(SolariteFurnaceBlockEntity animatable) {
        return null;
    }
}

