package com.neutrinodust.useful_ores.network;

import com.neutrinodust.useful_ores.minecart.SolariteMinecartBehavior.MinecartStep;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;





public record SolariteCartStepsPacket(int entityId, List<MinecartStep> steps, boolean reset) implements CustomPacketPayload {

   public static final CustomPacketPayload.Type<SolariteCartStepsPacket> TYPE =
         new CustomPacketPayload.Type<>(
               ResourceLocation.fromNamespaceAndPath("useful_ores", "solarite_cart_steps"));

   public static final StreamCodec<FriendlyByteBuf, SolariteCartStepsPacket> STREAM_CODEC =
         StreamCodec.of(
               (buf, pkt) -> {
                  buf.writeVarInt(pkt.entityId);
                  buf.writeBoolean(pkt.reset);
                  buf.writeVarInt(pkt.steps.size());
                  for (MinecartStep s : pkt.steps) s.write(buf);
               },
               buf -> {
                  int id = buf.readVarInt();
                  boolean reset = buf.readBoolean();
                  int n = buf.readVarInt();
                  List<MinecartStep> steps = new ArrayList<>(n);
                  for (int i = 0; i < n; i++) steps.add(MinecartStep.read(buf));
                  return new SolariteCartStepsPacket(id, steps, reset);
               });

   @Override
   public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
