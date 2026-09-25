package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.pedestal.AncientPedestalBlockEntity;
import com.geckolib.renderer.GeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class AncientPedestalRenderer extends GeoBlockRenderer<AncientPedestalBlockEntity, BlockEntityRenderState> {
   public AncientPedestalRenderer(BlockEntityRendererProvider.Context context) {
      super(context, new AncientPedestalGeoModel());
   }
}

