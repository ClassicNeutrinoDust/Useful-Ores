package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.particle.ModParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;


@EventBusSubscriber(modid = "useful_ores", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PhosgenePowderClientRegistration {
    private PhosgenePowderClientRegistration() {}

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.PHOSGENE_MIST.get(), PhosgeneColoredParticle.MistProvider::new);
        event.registerSpriteSet(ModParticleTypes.PHOSGENE_BUBBLE.get(), PhosgeneColoredParticle.BubbleProvider::new);
    }
}
