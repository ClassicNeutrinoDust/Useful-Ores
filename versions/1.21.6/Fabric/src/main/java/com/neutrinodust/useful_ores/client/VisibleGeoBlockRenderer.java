package com.neutrinodust.useful_ores.client;

import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * GeckoLib block renderer for models whose geometry can extend beyond the
 * owning block's normal frustum-culling bounds.
 *
 * The type bounds deliberately mirror GeckoLib 5's GeoBlockRenderer contract:
 * the block entity must itself be a GeoAnimatable. This prevents accidental
 * registration of a vanilla/non-Geo block entity with the Geo renderer.
 */
public class VisibleGeoBlockRenderer<T extends BlockEntity & GeoAnimatable> extends GeoBlockRenderer<T> {
    private static final int VIEW_DISTANCE = 256;

    public VisibleGeoBlockRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return VIEW_DISTANCE;
    }
}
