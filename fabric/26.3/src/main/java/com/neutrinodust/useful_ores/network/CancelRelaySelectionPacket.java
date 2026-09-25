package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CancelRelaySelectionPacket() implements CustomPacketPayload {

   public static final CustomPacketPayload.Type<CancelRelaySelectionPacket> TYPE =
         new CustomPacketPayload.Type<>(
               Identifier.fromNamespaceAndPath("useful_ores", "cancel_relay_selection"));

   public static final StreamCodec<FriendlyByteBuf, CancelRelaySelectionPacket> STREAM_CODEC =
         StreamCodec.unit(new CancelRelaySelectionPacket());

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}

