package com.neutrinodust.useful_ores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent the instant a spear jab fires client-side (any hit type - entity or air),
 * telling the server to end its charge state immediately. AttackEntityCallback
 * already does this for entity hits; this covers the air-swing case that hook
 * never sees, so the server's isUsingItem flag can no longer lag behind the
 * client's local cancel and desync the render pose.
 */
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
