package com.neutrinodust.useful_ores.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

@Mod.EventBusSubscriber(modid = "useful_ores", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class WirelessRelayKeyRegistration {
    private WirelessRelayKeyRegistration() {}

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(WirelessRelayKeyHandler.CANCEL_RELAY_KEY);
    }
}
