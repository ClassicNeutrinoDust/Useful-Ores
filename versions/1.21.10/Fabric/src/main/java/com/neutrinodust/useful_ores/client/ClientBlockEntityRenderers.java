package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.neutrinodust.useful_ores.init.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class ClientBlockEntityRenderers {

    public static void register() {

        BlockEntityRendererRegistry.register(ModBlockEntities.SUPER_BEACON, ctx -> new BeaconRenderer<>());

        EntityRendererRegistry.register(ModEntities.METEORITE_PROJECTILE, MeteoriteProjectileRenderer::new);

        EntityRendererRegistry.register(ModEntities.METEOR_BOMB, ThrownItemRenderer::new);

        BlockEntityRendererRegistry.register(ModBlockEntities.SOLARITE_FURNACE, ctx -> new SolariteFurnaceRenderer<>(ctx));

        EntityRendererRegistry.register(ModEntities.SOLARITE_BATTERY_MINECART, SolariteMinecartRenderer::new);

        BlockEntityRendererRegistry.register(ModBlockEntities.FULGURITE_ELECTRIC_TRAP, (BlockEntityRendererProvider) ctx -> new GeoBlockRenderer(new FulguriteElectricTrapGeoModel()));

        BlockEntityRendererRegistry.register(ModBlockEntities.WIRELESS_REDSTONE_RELAY, WirelessRedstoneRelayGlowRenderer::new);

        BlockEntityRendererRegistry.register(ModBlockEntities.REDSTONE_CLOCK, RedstoneClockGlowRenderer::new);

        BlockEntityRendererRegistry.register(ModBlockEntities.NYXIUM_DARK_BARRIER, (BlockEntityRendererProvider) ctx -> new GeoBlockRenderer(new NyxiumDarkBarrierGeoModel()));

        BlockEntityRendererRegistry.register(ModBlockEntities.ARCANITE_XP_JAR, ArcaniteXpJarRenderer::new);

        BlockEntityRendererRegistry.register(ModBlockEntities.ANCIENT_PEDESTAL, (BlockEntityRendererProvider) ctx -> new GeoBlockRenderer(new AncientPedestalGeoModel()));
        BlockEntityRendererRegistry.register(ModBlockEntities.DARK_WORMHOLE_PORTAL, (BlockEntityRendererProvider) ctx -> new GeoBlockRenderer(new DarkWormholePortalGeoModel()));

        EntityRendererRegistry.register(ModEntities.NYXIUMNITE_CUBE_PROJECTILE, NyxiumniteCubeProjectileRenderer::new);
    }

    private ClientBlockEntityRenderers() {}
}

