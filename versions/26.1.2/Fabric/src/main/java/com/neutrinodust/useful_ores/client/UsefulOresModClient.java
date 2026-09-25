package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.bioluminescence.BioluminescentOverlayRenderer;
import com.neutrinodust.useful_ores.entity.SolariteCartXpHudRenderer;
import com.neutrinodust.useful_ores.network.ModNetworking;
import com.neutrinodust.useful_ores.phosgene.PhosgenePowderOverlayRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

public class UsefulOresModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ModNetworking.initClient();

        ModelLayerRegistry.registerModelLayer(ClientModelLayers.SOLARITE_MINECART, SolariteMinecartModelData::createBodyLayer);

        ClientBlockEntityRenderers.register();
        ClientMenuScreens.register();
        ClientParticleEvents.register();
        PhosgenePowderClientEvents.register();

        SolariteCartInputHandler.register();
        WirelessRelayKeyHandler.register();

        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(BioluminescentOverlayRenderer::onRenderAfterTranslucent);
        LevelRenderEvents.COLLECT_SUBMITS.register(PhosgenePowderOverlayRenderer::onCollectSubmits);

        SolariteCartXpHudRenderer.register();
    }
}

