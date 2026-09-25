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
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import com.neutrinodust.useful_ores.init.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class UsefulOresModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ModNetworking.initClient();
        com.neutrinodust.useful_ores.client.spear.SpearChargeClientSync.register();

        EntityModelLayerRegistry.registerModelLayer(ClientModelLayers.SOLARITE_MINECART, SolariteMinecartModelData::createBodyLayer);

        
        
        
        
        registerCutoutTerrain();

        
        BlockRenderLayerMap.putBlock((net.minecraft.world.level.block.Block) ModItems.ARCANITE_XP_JAR_BLOCK.get(), ChunkSectionLayer.TRANSLUCENT);

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

    private static void registerCutoutTerrain() {
        
        for (var fire : ModOreFireBlocks.FIRE_BLOCKS.values()) {
            BlockRenderLayerMap.putBlock(fire.get(), ChunkSectionLayer.CUTOUT);
        }

        
        
        for (var campfire : ModColoredCampfireBlocks.CAMPFIRE_BLOCKS.values()) {
            BlockRenderLayerMap.putBlock(campfire.get(), ChunkSectionLayer.CUTOUT);
        }

        
        BlockRenderLayerMap.putBlock(ModTitaniumRailBlocks.TITANIUM_RAIL.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModTitaniumRailBlocks.TITANIUM_CONTROLLER_RAIL.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModEnderiumRailBlocks.ENDERIUM_RAIL.get(), ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModSolariteRailBlocks.SOLARITE_RAIL.get(), ChunkSectionLayer.CUTOUT);
    }
}

