package com.neutrinodust.useful_ores.block.rail;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

public final class EnderiumRailLinking {
   public static final int MAX_DISTANCE_BLOCKS = 100;

   private static final Map<ServerPlayer, GlobalPos> PENDING = new WeakHashMap<>();

   private EnderiumRailLinking() {
   }

   public static void handleInteract(ServerLevel level, BlockPos pos, ServerPlayer player, EnderiumRailBlockEntity rail) {
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
      if (!(level.getBlockEntity(otherPos) instanceof EnderiumRailBlockEntity other)) {

         PENDING.put(player, here);
         message(player, "select");
         spark(level, pos);
         return;
      }

      unlinkExisting(level, rail);
      unlinkExisting(level, other);

      rail.setLinkedPos(otherPos);
      other.setLinkedPos(pos);
      PENDING.remove(player);
      message(player, "linked");
      spark(level, pos);
      spark(level, otherPos);
   }

   private static void unlinkExisting(ServerLevel level, EnderiumRailBlockEntity rail) {
      GlobalPos linked = rail.getLinkedPos();
      if (linked == null) return;
      if (level.getBlockEntity(linked.pos()) instanceof EnderiumRailBlockEntity partner) {
         partner.clearLinkedPos();
      }
      rail.clearLinkedPos();
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
      player.displayClientMessage(Component.translatable("message.useful_ores.enderium_rail." + key), true);
   }

   private static void spark(ServerLevel level, BlockPos pos) {
      level.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 12, 0.25, 0.25, 0.25, 0.02);
   }
}

