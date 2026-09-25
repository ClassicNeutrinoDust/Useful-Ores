package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.particle.ModParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = "useful_ores", value = Dist.CLIENT)
public class ClientParticleEvents {

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.COLORED_FLAME.get(), ColoredFlameParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.WIFI_RING.get(), WifiRingParticle.Provider::new);
    }
}

