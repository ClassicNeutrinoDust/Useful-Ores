package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.particle.ModParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

@Mod.EventBusSubscriber(modid = "useful_ores", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PhosgenePowderClientRegistration {
    private PhosgenePowderClientRegistration() {}

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.PHOSGENE_MIST.get(), PhosgeneColoredParticle.MistProvider::new);
        event.registerSpriteSet(ModParticleTypes.PHOSGENE_BUBBLE.get(), PhosgeneColoredParticle.BubbleProvider::new);
    }
}
