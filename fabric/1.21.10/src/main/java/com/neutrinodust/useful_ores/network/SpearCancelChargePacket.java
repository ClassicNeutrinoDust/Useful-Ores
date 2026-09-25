package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;








public record SpearCancelChargePacket() implements CustomPacketPayload {

   public static final CustomPacketPayload.Type<SpearCancelChargePacket> TYPE =
         new CustomPacketPayload.Type<>(
               ResourceLocation.fromNamespaceAndPath("useful_ores", "spear_cancel_charge"));

   public static final StreamCodec<FriendlyByteBuf, SpearCancelChargePacket> STREAM_CODEC =
         StreamCodec.unit(new SpearCancelChargePacket());

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
