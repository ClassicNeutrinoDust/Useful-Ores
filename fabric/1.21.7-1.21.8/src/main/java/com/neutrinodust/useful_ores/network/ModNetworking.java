package com.neutrinodust.useful_ores.network;

import com.neutrinodust.useful_ores.bioluminescence.ClientGlowingBlocks;
import com.neutrinodust.useful_ores.lock.ClientLockedChests;
import com.neutrinodust.useful_ores.block.WirelessRedstoneLinking;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import com.neutrinodust.useful_ores.init.SpearTags;
import com.neutrinodust.useful_ores.combat.SpearChargeCombat;

public final class ModNetworking {

   private ModNetworking() {}

   public static void init() {
      PayloadTypeRegistry.playC2S().register(
            CancelRelaySelectionPacket.TYPE, CancelRelaySelectionPacket.STREAM_CODEC);
      PayloadTypeRegistry.playC2S().register(
            SolariteCartSpeedInputPacket.TYPE, SolariteCartSpeedInputPacket.STREAM_CODEC);
      PayloadTypeRegistry.playS2C().register(
            SyncGlowingBlocksPacket.TYPE, SyncGlowingBlocksPacket.STREAM_CODEC);
      PayloadTypeRegistry.playS2C().register(
            SyncPhosgenePowderBlocksPacket.TYPE, SyncPhosgenePowderBlocksPacket.STREAM_CODEC);
      PayloadTypeRegistry.playS2C().register(
            com.neutrinodust.useful_ores.network.SyncLockedChestsPacket.TYPE,
            com.neutrinodust.useful_ores.network.SyncLockedChestsPacket.STREAM_CODEC);
      PayloadTypeRegistry.playC2S().register(
            SpearCancelChargePacket.TYPE, SpearCancelChargePacket.STREAM_CODEC);
      PayloadTypeRegistry.playC2S().register(
            SpearJabPacket.TYPE, SpearJabPacket.STREAM_CODEC);
      PayloadTypeRegistry.playC2S().register(
            SpearChargeFramePacket.TYPE, SpearChargeFramePacket.STREAM_CODEC);

      ServerPlayNetworking.registerGlobalReceiver(SpearCancelChargePacket.TYPE,
            (packet, context) -> context.server().execute(() -> {
               ServerPlayer player = context.player();
               if (player.isUsingItem() && player.getUseItem().is(SpearTags.SPEARS)) {
                  player.stopUsingItem();
               }
               SpearChargeCombat.endUse(player, player.getMainHandItem());
            }));

      ServerPlayNetworking.registerGlobalReceiver(SpearJabPacket.TYPE,
            (packet, context) -> context.server().execute(() ->
                  SpearChargeCombat.performJab(context.player())));

      ServerPlayNetworking.registerGlobalReceiver(SpearChargeFramePacket.TYPE,
            (packet, context) -> context.server().execute(() ->
                  SpearChargeCombat.onClientChargeFrame(context.player(), packet)));

      ServerPlayNetworking.registerGlobalReceiver(CancelRelaySelectionPacket.TYPE,
            (packet, context) -> context.server().execute(() -> {
               if (context.player() instanceof ServerPlayer serverPlayer) {
                  WirelessRedstoneLinking.cancelPending(serverPlayer);
               }
            }));

      ServerPlayNetworking.registerGlobalReceiver(SolariteCartSpeedInputPacket.TYPE,
            (packet, context) -> context.server().execute(() -> {
               if (context.player() instanceof ServerPlayer serverPlayer
                     && serverPlayer.getVehicle() instanceof SolariteBatteryMinecartEntity cart) {
                  cart.setSpeedInput(packet.direction());
               }
            }));
   }

   public static void initClient() {
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
            SyncGlowingBlocksPacket.TYPE,
            (pkt, context) -> context.client().execute(() -> {
               switch (pkt.mode()) {
                  case FULL -> ClientGlowingBlocks.fullSync(pkt.positions());
                  case ADD -> ClientGlowingBlocks.merge(pkt.positions());
                  case REMOVE -> ClientGlowingBlocks.removeAll(pkt.positions());
               }
            }));

      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
            SyncPhosgenePowderBlocksPacket.TYPE,
            (pkt, context) -> context.client().execute(() -> {
               switch (pkt.mode()) {
                  case FULL -> com.neutrinodust.useful_ores.phosgene.ClientPhosgenePowderBlocks.fullSync(pkt.entries());
                  case ADD -> com.neutrinodust.useful_ores.phosgene.ClientPhosgenePowderBlocks.add(pkt.entries());
                  case REMOVE -> com.neutrinodust.useful_ores.phosgene.ClientPhosgenePowderBlocks.remove(pkt.entries());
               }
            }));

      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
            com.neutrinodust.useful_ores.network.SyncLockedChestsPacket.TYPE,
            (pkt, context) -> context.client().execute(() -> {
               switch (pkt.mode()) {
                  case FULL -> ClientLockedChests.fullSync(pkt.positions());
                  case ADD -> ClientLockedChests.merge(pkt.positions());
                  case REMOVE -> ClientLockedChests.removeAll(pkt.positions());
               }
            }));
   }
}

