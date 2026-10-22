package com.neutrinodust.useful_ores.network;

import com.neutrinodust.useful_ores.bioluminescence.ClientGlowingBlocks;
import com.neutrinodust.useful_ores.lock.ClientLockedChests;
import com.neutrinodust.useful_ores.block.WirelessRedstoneLinking;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import com.neutrinodust.useful_ores.init.SpearTags;
import com.neutrinodust.useful_ores.combat.SpearChargeCombat;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ModNetworking {
   private ModNetworking() {}

   public static void sendToPlayer(ServerPlayer player, ModPacket packet) {
      net.minecraft.network.FriendlyByteBuf buf = PacketByteBufs.create();
      packet.write(buf);
      ServerPlayNetworking.send(player, packet.id(), buf);
   }

   public static void sendToServer(ModPacket packet) {
      net.minecraft.network.FriendlyByteBuf buf = PacketByteBufs.create();
      packet.write(buf);
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(packet.id(), buf);
   }

   private static <P extends ModPacket> void onServer(net.minecraft.resources.ResourceLocation id,
         Function<net.minecraft.network.FriendlyByteBuf, P> reader, BiConsumer<ServerPlayer, P> handler) {
      ServerPlayNetworking.registerGlobalReceiver(id, (server, player, listener, buf, responseSender) -> {
         P packet = reader.apply(buf);
         server.execute(() -> handler.accept(player, packet));
      });
   }

   private static <P extends ModPacket> void onClient(net.minecraft.resources.ResourceLocation id,
         Function<net.minecraft.network.FriendlyByteBuf, P> reader, java.util.function.Consumer<P> handler) {
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(id,
         (client, listener, buf, responseSender) -> {
            P packet = reader.apply(buf);
            client.execute(() -> handler.accept(packet));
         });
   }

   public static void init() {
      onServer(SpearCancelChargePacket.ID, SpearCancelChargePacket::read, (player, packet) -> {
         if (player.isUsingItem() && player.getUseItem().is(SpearTags.SPEARS)) {
            player.stopUsingItem();
         }
         SpearChargeCombat.endUse(player, player.getMainHandItem());
      });

      onServer(SpearJabPacket.ID, SpearJabPacket::read, (player, packet) ->
            SpearChargeCombat.performJab(player));

      onServer(SpearChargeFramePacket.ID, SpearChargeFramePacket::read, (player, packet) ->
            SpearChargeCombat.onClientChargeFrame(player, packet));

      onServer(CancelRelaySelectionPacket.ID, CancelRelaySelectionPacket::read, (player, packet) ->
            WirelessRedstoneLinking.cancelPending(player));

      onServer(SolariteCartSpeedInputPacket.ID, SolariteCartSpeedInputPacket::read, (player, packet) -> {
         if (player.getVehicle() instanceof SolariteBatteryMinecartEntity cart) {
            cart.setSpeedInput(packet.direction());
         }
      });
   }

   public static void initClient() {
      onClient(SolariteCartStepsPacket.ID, SolariteCartStepsPacket::read, pkt -> {
         net.minecraft.client.multiplayer.ClientLevel level = net.minecraft.client.Minecraft.getInstance().level;
         if (level != null && level.getEntity(pkt.entityId()) instanceof SolariteBatteryMinecartEntity cart) {
            cart.getBehavior().receiveSteps(pkt.steps(), pkt.reset());
         }
      });

      onClient(SyncGlowingBlocksPacket.ID, SyncGlowingBlocksPacket::read, pkt -> {
         switch (pkt.mode()) {
            case FULL -> ClientGlowingBlocks.fullSync(pkt.positions());
            case ADD -> ClientGlowingBlocks.merge(pkt.positions());
            case REMOVE -> ClientGlowingBlocks.removeAll(pkt.positions());
         }
      });

      onClient(SyncPhosgenePowderBlocksPacket.ID, SyncPhosgenePowderBlocksPacket::read, pkt -> {
         switch (pkt.mode()) {
            case FULL -> com.neutrinodust.useful_ores.phosgene.ClientPhosgenePowderBlocks.fullSync(pkt.entries());
            case ADD -> com.neutrinodust.useful_ores.phosgene.ClientPhosgenePowderBlocks.add(pkt.entries());
            case REMOVE -> com.neutrinodust.useful_ores.phosgene.ClientPhosgenePowderBlocks.remove(pkt.entries());
         }
      });

      onClient(SyncLockedChestsPacket.ID, SyncLockedChestsPacket::read, pkt -> {
         switch (pkt.mode()) {
            case FULL -> ClientLockedChests.fullSync(pkt.positions());
            case ADD -> ClientLockedChests.merge(pkt.positions());
            case REMOVE -> ClientLockedChests.removeAll(pkt.positions());
         }
      });
   }
}
