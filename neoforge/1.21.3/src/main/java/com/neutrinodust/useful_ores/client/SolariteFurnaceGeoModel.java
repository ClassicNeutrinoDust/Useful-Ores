package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.solar.SolariteFurnaceBlockEntity;
import com.neutrinodust.useful_ores.solar.SolariteFurnaceBlock;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class SolariteFurnaceGeoModel extends GeoModel<SolariteFurnaceBlockEntity> {

    private static final ResourceLocation MODEL_OFF = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "geo/block/solarite_furnace_off.geo.json");
    private static final ResourceLocation MODEL_ON = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "geo/block/solarite_furnace_on.geo.json");
    private static final ResourceLocation TEXTURE_OFF = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "textures/geo/solarite_furnace_off.png");
    private static final ResourceLocation TEXTURE_ON = ResourceLocation.fromNamespaceAndPath(
            "useful_ores", "textures/geo/solarite_furnace_on.png");

    @Override
    public ResourceLocation getModelResource(SolariteFurnaceBlockEntity animatable, GeoRenderer<SolariteFurnaceBlockEntity> renderer) {
        return isLit(animatable) ? MODEL_ON : MODEL_OFF;
    }

    @Override
    public ResourceLocation getTextureResource(SolariteFurnaceBlockEntity animatable, GeoRenderer<SolariteFurnaceBlockEntity> renderer) {
        return isLit(animatable) ? TEXTURE_ON : TEXTURE_OFF;
    }

    private boolean isLit(SolariteFurnaceBlockEntity furnace) {
        net.minecraft.world.level.block.state.BlockState state = furnace.getBlockState();
        return state.hasProperty(SolariteFurnaceBlock.LIT) && state.getValue(SolariteFurnaceBlock.LIT);
    }

    @Override
    public ResourceLocation getAnimationResource(SolariteFurnaceBlockEntity animatable) {
        return null;
    }
}

