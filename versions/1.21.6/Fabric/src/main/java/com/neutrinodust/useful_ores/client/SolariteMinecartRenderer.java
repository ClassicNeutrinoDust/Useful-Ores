package com.neutrinodust.useful_ores.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;

public class SolariteMinecartRenderer extends MinecartRenderer {

    public SolariteMinecartRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, ClientModelLayers.SOLARITE_MINECART);
    }
}

