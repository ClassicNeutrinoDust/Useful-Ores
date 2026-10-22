package com.neutrinodust.useful_ores.network;
import net.minecraft.network.FriendlyByteBuf;
public record SpearJabPacket() { public static void encode(SpearJabPacket p,FriendlyByteBuf b){} public static SpearJabPacket decode(FriendlyByteBuf b){return new SpearJabPacket();} }
