package com.neutrinodust.useful_ores.block;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;

public final class WirelessRedstoneRelayLifecycleEvents {

    private WirelessRedstoneRelayLifecycleEvents() {}

    public static void register() {
        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> {
            if (blockEntity instanceof WirelessRedstoneRelayBlockEntity relay) {
                relay.onLoad();
            }
        });
        ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, level) -> {
            if (blockEntity instanceof WirelessRedstoneRelayBlockEntity relay) {
                relay.onChunkUnloaded();
            }
        });
    }
}

