package com.neutrinodust.useful_ores.network;
import net.minecraft.network.FriendlyByteBuf;
public record SolariteCartSpeedInputPacket(int direction) { public static void encode(SolariteCartSpeedInputPacket p,FriendlyByteBuf b){b.writeVarInt(p.direction());} public static SolariteCartSpeedInputPacket decode(FriendlyByteBuf b){return new SolariteCartSpeedInputPacket(b.readVarInt());} }
