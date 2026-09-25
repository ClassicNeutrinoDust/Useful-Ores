package com.neutrinodust.useful_ores.block;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

public final class WirelessRedstoneLinking {
   public static final int MAX_DISTANCE_BLOCKS = 100;

   private static final Map<ServerPlayer, GlobalPos> PENDING = new WeakHashMap<>();

   private WirelessRedstoneLinking() {
   }

   public static void handleInteract(ServerLevel level, BlockPos pos, ServerPlayer player, WirelessRedstoneRelayBlockEntity relay) {
      GlobalPos here = GlobalPos.of(level.dimension(), pos);
      GlobalPos pending = PENDING.get(player);

      if (pending == null) {
         PENDING.put(player, here);
         message(player, "select");
         spark(level, pos);
         return;
      }

      if (pending.pos().equals(pos) && pending.dimension().equals(level.dimension())) {
         PENDING.remove(player);
         message(player, "deselect");
         return;
      }

      if (!pending.dimension().equals(level.dimension())) {
         PENDING.put(player, here);
         message(player, "wrong_dimension");
         spark(level, pos);
         return;
      }

      double distance = Math.sqrt(pending.pos().distSqr(pos));
      if (distance > MAX_DISTANCE_BLOCKS) {
         PENDING.put(player, here);
         message(player, "too_far");
         spark(level, pos);
         return;
      }

      BlockPos otherPos = pending.pos();
      if (!(level.getBlockEntity(otherPos) instanceof WirelessRedstoneRelayBlockEntity other)) {

         PENDING.put(player, here);
         message(player, "select");
         spark(level, pos);
         return;
      }

      boolean otherIsHub = other.getMode() != WirelessRedstoneRelayBlockEntity.LinkMode.SINGLE;
      boolean relayIsHub = relay.getMode() != WirelessRedstoneRelayBlockEntity.LinkMode.SINGLE;

      if (otherIsHub && relayIsHub) {
         PENDING.put(player, here);
         message(player, "two_hubs");
         spark(level, pos);
         return;
      }

      if (otherIsHub || relayIsHub) {
         WirelessRedstoneRelayBlockEntity hub = otherIsHub ? other : relay;
         BlockPos hubPos = otherIsHub ? otherPos : pos;
         WirelessRedstoneRelayBlockEntity target = otherIsHub ? relay : other;
         BlockPos targetPos = otherIsHub ? pos : otherPos;

         boolean wasReceiver = hub.getBroadcastTargets().contains(targetPos);
         hub.addOrRemoveBroadcastTarget(level, targetPos, target);

         PENDING.put(player, GlobalPos.of(level.dimension(), hubPos));
         message(player, wasReceiver ? "receiver_removed" : "receiver_added");
         spark(level, pos);
         spark(level, otherPos);
         return;
      }

      relay.setLinkedPosOnly(otherPos);
      other.setLinkedPosOnly(pos);

      relay.resync(level);
      other.resync(level);
      relay.updateForceLoad(level);
      other.updateForceLoad(level);
      PENDING.remove(player);
      message(player, "linked");
      spark(level, pos);
      spark(level, otherPos);
   }

   public static void cancelPending(ServerPlayer player) {
      if (PENDING.remove(player) != null) {
         message(player, "cancelled");
      }
   }

   public static @Nullable GlobalPos getPending(ServerPlayer player) {
      return PENDING.get(player);
   }

   private static void message(ServerPlayer player, String key) {
      player.sendSystemMessage(Component.translatable("message.useful_ores.wireless_redstone_relay." + key), true);
   }

   private static void spark(ServerLevel level, BlockPos pos) {
      level.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 8, 0.2, 0.2, 0.2, 0.01);
   }
}

