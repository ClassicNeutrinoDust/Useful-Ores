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

public class UsefulOresModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ModNetworking.initClient();
        com.neutrinodust.useful_ores.client.spear.SpearChargeClientSync.register();

        EntityModelLayerRegistry.registerModelLayer(ClientModelLayers.SOLARITE_MINECART, SolariteMinecartModelData::createBodyLayer);

        // The targeted Minecraft versions default custom terrain blocks to SOLID unless their block is explicitly mapped.
        // Vanilla fire and rails are CUTOUT terrain, and these custom implementations use the same
        // alpha-tested models/textures. Keeping them on SOLID produces the characteristic smeared,
        // enlarged-looking transparent pixels seen on the source-version client.
        registerCutoutTerrain();

        // The XP jar is a genuinely translucent glass block.
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

    private static void registerCutoutTerrain() {
        // All custom colored fire blocks mirror vanilla FireBlock, whose sprites are alpha-cut fire planes.
        for (var fire : ModOreFireBlocks.FIRE_BLOCKS.values()) {
            BlockRenderLayerMap.INSTANCE.putBlock(fire.get(), RenderType.cutout());
        }

        // Vanilla campfires use the cutout terrain pipeline for their log/fire geometry.
        // The custom CampfireBlock subclasses need the same explicit mapping in the targeted client versions.
        for (var campfire : ModColoredCampfireBlocks.CAMPFIRE_BLOCKS.values()) {
            BlockRenderLayerMap.INSTANCE.putBlock(campfire.get(), RenderType.cutout());
        }

        // All custom rails mirror vanilla RailBlock/BaseRailBlock and use transparent 16x16 rail sprites.
        BlockRenderLayerMap.INSTANCE.putBlock(ModTitaniumRailBlocks.TITANIUM_RAIL.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModTitaniumRailBlocks.TITANIUM_CONTROLLER_RAIL.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModEnderiumRailBlocks.ENDERIUM_RAIL.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModSolariteRailBlocks.SOLARITE_RAIL.get(), RenderType.cutout());
    }
}

