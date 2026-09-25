package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SolariteCartSpeedInputPacket(int direction) implements CustomPacketPayload {

   public static final CustomPacketPayload.Type<SolariteCartSpeedInputPacket> TYPE =
         new CustomPacketPayload.Type<>(
               Identifier.fromNamespaceAndPath("useful_ores", "solarite_cart_speed_input"));

   public static final StreamCodec<FriendlyByteBuf, SolariteCartSpeedInputPacket> STREAM_CODEC =
         StreamCodec.composite(
               ByteBufCodecs.VAR_INT, SolariteCartSpeedInputPacket::direction,
               SolariteCartSpeedInputPacket::new);

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}

