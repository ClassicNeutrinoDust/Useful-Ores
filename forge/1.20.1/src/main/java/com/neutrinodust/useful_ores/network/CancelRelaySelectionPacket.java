package com.neutrinodust.useful_ores.network;
import net.minecraft.network.FriendlyByteBuf;
public record CancelRelaySelectionPacket() { public static void encode(CancelRelaySelectionPacket p,FriendlyByteBuf b){} public static CancelRelaySelectionPacket decode(FriendlyByteBuf b){return new CancelRelaySelectionPacket();} }
