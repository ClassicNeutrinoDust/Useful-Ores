package com.neutrinodust.useful_ores.network;
import net.minecraft.network.FriendlyByteBuf;
public record SpearCancelChargePacket() { public static void encode(SpearCancelChargePacket p,FriendlyByteBuf b){} public static SpearCancelChargePacket decode(FriendlyByteBuf b){return new SpearCancelChargePacket();} }
