package com.neutrinodust.useful_ores.client;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import com.neutrinodust.useful_ores.block.FulguriteElectricTrapBlockEntity;
import com.neutrinodust.useful_ores.pedestal.DarkWormholePortalBlockEntity;
import com.neutrinodust.useful_ores.init.ModEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.EntityRenderersEvent;

@Mod.EventBusSubscriber(modid = "useful_ores", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientBlockEntityRenderers {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ClientModelLayers.SOLARITE_MINECART, SolariteMinecartModelData::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerBlockEntityRenderer(ModBlockEntities.SUPER_BEACON.get(), ctx -> new SuperBeaconRenderer(ctx));

        event.registerEntityRenderer(ModEntities.METEORITE_PROJECTILE.get(), MeteoriteProjectileRenderer::new);

        event.registerEntityRenderer(ModEntities.METEOR_BOMB.get(), ThrownItemRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.SOLARITE_FURNACE.get(), ctx -> new SolariteFurnaceRenderer<>(ctx));

        event.registerEntityRenderer(ModEntities.SOLARITE_BATTERY_MINECART.get(), SolariteMinecartRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.FULGURITE_ELECTRIC_TRAP.get(), ctx -> new GeoBlockRenderer<FulguriteElectricTrapBlockEntity>(new FulguriteElectricTrapGeoModel()));

        event.registerBlockEntityRenderer(ModBlockEntities.NYXIUM_DARK_BARRIER.get(), ctx -> new VisibleGeoBlockRenderer<>(new NyxiumDarkBarrierGeoModel(), new net.minecraft.world.phys.AABB(-2.0D, 0.0D, -2.0D, 3.0D, 4.5D, 3.0D)));

        event.registerBlockEntityRenderer(ModBlockEntities.WIRELESS_REDSTONE_RELAY.get(), WirelessRedstoneRelayGlowRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.REDSTONE_CLOCK.get(), RedstoneClockGlowRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.ARCANITE_XP_JAR.get(), ArcaniteXpJarRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.ANCIENT_PEDESTAL.get(), ctx -> new VisibleGeoBlockRenderer<>(new AncientPedestalGeoModel()));

        event.registerBlockEntityRenderer(ModBlockEntities.DARK_WORMHOLE_PORTAL.get(), ctx -> new GeoBlockRenderer<DarkWormholePortalBlockEntity>(new DarkWormholePortalGeoModel()));

        event.registerEntityRenderer(ModEntities.NYXIUMNITE_CUBE_PROJECTILE.get(), NyxiumniteCubeProjectileRenderer::new);

        event.registerEntityRenderer(ModEntities.SCHEELITE_ARROW.get(), TippableArrowRenderer::new);
        event.registerEntityRenderer(ModEntities.ARCANITE_ARROW.get(), TippableArrowRenderer::new);
        event.registerEntityRenderer(ModEntities.NYXIUM_ARROW.get(), TippableArrowRenderer::new);
    }

}

