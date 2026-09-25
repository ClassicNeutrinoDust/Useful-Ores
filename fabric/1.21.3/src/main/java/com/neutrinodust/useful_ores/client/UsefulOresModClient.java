package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.bioluminescence.BioluminescentOverlayRenderer;
import com.neutrinodust.useful_ores.entity.SolariteCartXpHudRenderer;
import com.neutrinodust.useful_ores.network.ModNetworking;
import com.neutrinodust.useful_ores.init.ModEnderiumRailBlocks;
import com.neutrinodust.useful_ores.init.ModOreFireBlocks;
import com.neutrinodust.useful_ores.init.ModSolariteRailBlocks;
import com.neutrinodust.useful_ores.init.ModTitaniumRailBlocks;
import com.neutrinodust.useful_ores.init.ModColoredCampfireBlocks;
import com.neutrinodust.useful_ores.phosgene.PhosgenePowderOverlayRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import com.neutrinodust.useful_ores.init.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class UsefulOresModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ModNetworking.initClient();
        LegacyItemProperties.register();
        LegacyModelLoading.register();
        registerContextualItemRenderers();
        com.neutrinodust.useful_ores.client.spear.SpearChargeClientSync.register();

        EntityModelLayerRegistry.registerModelLayer(ClientModelLayers.SOLARITE_MINECART, SolariteMinecartModelData::createBodyLayer);

        
        
        
        
        registerCutoutTerrain();

        
        BlockRenderLayerMap.INSTANCE.putBlock((net.minecraft.world.level.block.Block) ModItems.ARCANITE_XP_JAR_BLOCK.get(), RenderType.translucent());

        ClientBlockEntityRenderers.register();
        ClientMenuScreens.register();
        ClientParticleEvents.register();
        PhosgenePowderClientEvents.register();

        SolariteCartInputHandler.register();
        WirelessRelayKeyHandler.register();

        WorldRenderEvents.AFTER_TRANSLUCENT.register(BioluminescentOverlayRenderer::onRenderAfterTranslucent);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(PhosgenePowderOverlayRenderer::onCollectSubmits);

        SolariteCartXpHudRenderer.register();
    }

    private static void registerContextualItemRenderers() {
        var renderer = ContextualDynamicItemRenderer.INSTANCE;
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.METEOR_STAFF.get(), renderer);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.NYXIUMNITE_STAFF.get(), renderer);
    }

    private static void registerCutoutTerrain() {
        
        for (var fire : ModOreFireBlocks.FIRE_BLOCKS.values()) {
            BlockRenderLayerMap.INSTANCE.putBlock(fire.get(), RenderType.cutout());
        }

        
        
        for (var campfire : ModColoredCampfireBlocks.CAMPFIRE_BLOCKS.values()) {
            BlockRenderLayerMap.INSTANCE.putBlock(campfire.get(), RenderType.cutout());
        }

        
        BlockRenderLayerMap.INSTANCE.putBlock(ModTitaniumRailBlocks.TITANIUM_RAIL.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModTitaniumRailBlocks.TITANIUM_CONTROLLER_RAIL.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModEnderiumRailBlocks.ENDERIUM_RAIL.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModSolariteRailBlocks.SOLARITE_RAIL.get(), RenderType.cutout());
    }
}

