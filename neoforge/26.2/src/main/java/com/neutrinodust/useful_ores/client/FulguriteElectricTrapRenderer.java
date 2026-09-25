package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.block.FulguriteElectricTrapBlockEntity;
import com.geckolib.renderer.GeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class FulguriteElectricTrapRenderer extends GeoBlockRenderer<FulguriteElectricTrapBlockEntity, BlockEntityRenderState> {
   public FulguriteElectricTrapRenderer(BlockEntityRendererProvider.Context context) {
      super(context, new FulguriteElectricTrapGeoModel());
   }
}

