package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SpearJabPacket() implements CustomPacketPayload {
    public static final Type<SpearJabPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("useful_ores", "spear_jab"));
    public static final StreamCodec<FriendlyByteBuf, SpearJabPacket> STREAM_CODEC =
            StreamCodec.unit(new SpearJabPacket());
    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
