package com.neutrinodust.useful_ores.client;

import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;









public class VisibleGeoBlockRenderer<T extends BlockEntity & GeoAnimatable> extends GeoBlockRenderer<T> {
    private static final int VIEW_DISTANCE = 256;

    public VisibleGeoBlockRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    public boolean shouldRenderOffScreen(T entity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return VIEW_DISTANCE;
    }
}
