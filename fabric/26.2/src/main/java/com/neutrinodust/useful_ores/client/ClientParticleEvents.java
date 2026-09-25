package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.particle.ModParticleTypes;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;

public class ClientParticleEvents {

    public static void register() {
        ParticleProviderRegistry registry = ParticleProviderRegistry.getInstance();
        registry.register(ModParticleTypes.COLORED_FLAME, ColoredFlameParticle.Provider::new);
        registry.register(ModParticleTypes.WIFI_RING, WifiRingParticle.Provider::new);
    }

    private ClientParticleEvents() {}
}

