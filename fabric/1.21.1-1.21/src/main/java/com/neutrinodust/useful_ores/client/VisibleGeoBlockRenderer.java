package com.neutrinodust.useful_ores.client;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;






public class VisibleGeoBlockRenderer<T extends BlockEntity & GeoAnimatable> extends GeoBlockRenderer<T> {
    private static final int VIEW_DISTANCE = 256;
    private final AABB localRenderBounds;

    public VisibleGeoBlockRenderer(GeoModel<T> model) {
        this(model, null);
    }

    public VisibleGeoBlockRenderer(GeoModel<T> model, AABB localRenderBounds) {
        super(model);
        this.localRenderBounds = localRenderBounds;
    }

    @Override
    public boolean shouldRenderOffScreen(T entity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return VIEW_DISTANCE;
    }

    






    public AABB getRenderBoundingBox(T entity) {
        if (localRenderBounds == null) {
            return new AABB(entity.getBlockPos());
        }
        return localRenderBounds.move(
                entity.getBlockPos().getX(),
                entity.getBlockPos().getY(),
                entity.getBlockPos().getZ());
    }
}
