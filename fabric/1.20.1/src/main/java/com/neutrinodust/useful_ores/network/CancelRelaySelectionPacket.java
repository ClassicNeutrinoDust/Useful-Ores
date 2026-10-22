package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record CancelRelaySelectionPacket() implements ModPacket {
    public static final ResourceLocation ID = new ResourceLocation("useful_ores", "cancel_relay_selection");

    public static CancelRelaySelectionPacket read(FriendlyByteBuf buf) {
        return new CancelRelaySelectionPacket();
    }

    @Override public ResourceLocation id() { return ID; }

    @Override public void write(FriendlyByteBuf buf) { }
}
