package com.neutrinodust.useful_ores.network;

import com.neutrinodust.useful_ores.block.WirelessRedstoneLinking;
import com.neutrinodust.useful_ores.bioluminescence.ClientGlowingBlocks;
import com.neutrinodust.useful_ores.lock.ClientLockedChests;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {

   private ModNetworking() {}

   public static void init(IEventBus modBus) {
      modBus.addListener(ModNetworking::onRegisterPayloads);
   }

   private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1");

      registrar.playToServer(
            CancelRelaySelectionPacket.TYPE,
            CancelRelaySelectionPacket.STREAM_CODEC,
            (packet, ctx) -> {

               ctx.enqueueWork(() -> {
                  if (ctx.player() instanceof ServerPlayer serverPlayer) {
                     WirelessRedstoneLinking.cancelPending(serverPlayer);
                  }
               });
            });

      registrar.playToServer(
            com.neutrinodust.useful_ores.network.SolariteCartSpeedInputPacket.TYPE,
            com.neutrinodust.useful_ores.network.SolariteCartSpeedInputPacket.STREAM_CODEC,
            (packet, ctx) -> ctx.enqueueWork(() -> {
               if (ctx.player() instanceof ServerPlayer serverPlayer
                     && serverPlayer.getVehicle() instanceof com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity cart) {
                  cart.setSpeedInput(packet.direction());
               }
            }));

      registrar.playToClient(
            com.neutrinodust.useful_ores.network.SyncGlowingBlocksPacket.TYPE,
            com.neutrinodust.useful_ores.network.SyncGlowingBlocksPacket.STREAM_CODEC,
            (pkt, ctx) -> ctx.enqueueWork(() -> {
               switch (pkt.mode()) {
                  case FULL -> ClientGlowingBlocks.fullSync(pkt.positions());
                  case ADD -> ClientGlowingBlocks.merge(pkt.positions());
                  case REMOVE -> ClientGlowingBlocks.removeAll(pkt.positions());
               }
            }));

      registrar.playToClient(
            SyncPhosgenePowderBlocksPacket.TYPE,
            SyncPhosgenePowderBlocksPacket.STREAM_CODEC,
            (pkt, ctx) -> ctx.enqueueWork(() -> {
               switch (pkt.mode()) {
                  case FULL -> com.neutrinodust.useful_ores.phosgene.ClientPhosgenePowderBlocks.fullSync(pkt.entries());
                  case ADD -> com.neutrinodust.useful_ores.phosgene.ClientPhosgenePowderBlocks.add(pkt.entries());
                  case REMOVE -> com.neutrinodust.useful_ores.phosgene.ClientPhosgenePowderBlocks.remove(pkt.entries());
               }
            }));

      registrar.playToClient(
            com.neutrinodust.useful_ores.network.SyncLockedChestsPacket.TYPE,
            com.neutrinodust.useful_ores.network.SyncLockedChestsPacket.STREAM_CODEC,
            (pkt, ctx) -> ctx.enqueueWork(() -> {
               switch (pkt.mode()) {
                  case FULL -> ClientLockedChests.fullSync(pkt.positions());
                  case ADD -> ClientLockedChests.merge(pkt.positions());
                  case REMOVE -> ClientLockedChests.removeAll(pkt.positions());
               }
            }));
   }
}

