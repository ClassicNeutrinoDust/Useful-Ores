package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.particle.ModParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

@Mod.EventBusSubscriber(modid = "useful_ores", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientParticleEvents {

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.COLORED_FLAME.get(), ColoredFlameParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.WIFI_RING.get(), WifiRingParticle.Provider::new);
    }
}

