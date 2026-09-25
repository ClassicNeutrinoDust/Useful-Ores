package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.barrier.NyxiumDarkBarrierBlockEntity;
import com.geckolib.renderer.GeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class NyxiumDarkBarrierRenderer extends GeoBlockRenderer<NyxiumDarkBarrierBlockEntity, BlockEntityRenderState> {
   public NyxiumDarkBarrierRenderer(BlockEntityRendererProvider.Context context) {
      super(context, new NyxiumDarkBarrierGeoModel());
   }
}

