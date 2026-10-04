package com.neutrinodust.useful_ores.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;


@EventBusSubscriber(modid = "useful_ores", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class WirelessRelayKeyRegistration {
    private WirelessRelayKeyRegistration() {}

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(WirelessRelayKeyHandler.CANCEL_RELAY_KEY);
    }
}
