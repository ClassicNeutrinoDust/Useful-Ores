package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SolariteCartSpeedInputPacket(int direction) implements ModPacket {
    public static final ResourceLocation ID = new ResourceLocation("useful_ores", "solarite_cart_speed_input");

    public static SolariteCartSpeedInputPacket read(FriendlyByteBuf buf) {
        return new SolariteCartSpeedInputPacket(buf.readVarInt());
    }

    @Override public ResourceLocation id() { return ID; }

    @Override public void write(FriendlyByteBuf buf) { buf.writeVarInt(direction); }
}
