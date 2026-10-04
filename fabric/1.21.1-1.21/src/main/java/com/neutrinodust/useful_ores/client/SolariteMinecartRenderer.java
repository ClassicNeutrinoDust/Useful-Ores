package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.AbstractMinecart;







public class SolariteMinecartRenderer extends MinecartRenderer {
    private static final ResourceLocation SOLARITE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("useful_ores", "textures/entity/solarite_battery_minecart.png");

    public SolariteMinecartRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, ClientModelLayers.SOLARITE_MINECART);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractMinecart minecart) {
        return minecart instanceof SolariteBatteryMinecartEntity ? SOLARITE_TEXTURE : super.getTextureLocation(minecart);
    }
}
