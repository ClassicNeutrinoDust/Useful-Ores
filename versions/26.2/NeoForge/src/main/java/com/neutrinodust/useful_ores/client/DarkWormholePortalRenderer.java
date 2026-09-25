package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.pedestal.DarkWormholePortalBlockEntity;
import com.geckolib.renderer.GeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class DarkWormholePortalRenderer extends GeoBlockRenderer<DarkWormholePortalBlockEntity, BlockEntityRenderState> {
   public DarkWormholePortalRenderer(BlockEntityRendererProvider.Context context) {
      super(context, new DarkWormholePortalGeoModel());
   }
}

