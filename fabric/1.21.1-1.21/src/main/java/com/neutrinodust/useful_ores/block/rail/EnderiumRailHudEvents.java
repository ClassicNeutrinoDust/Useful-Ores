package com.neutrinodust.useful_ores.block.rail;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class EnderiumRailHudEvents {

   public static final EnderiumRailHudEvents INSTANCE = new EnderiumRailHudEvents();

   private static final double REACH = 5.0;

   private final Map<UUID, BlockPos> lastTarget = new HashMap<>();

   public void onPlayerTickPost(Player playerEntity) {
      if (!(playerEntity instanceof ServerPlayer player)) return;

      if (!(player.level() instanceof ServerLevel level)) return;
      Vec3 eye = player.getEyePosition();
      Vec3 look = player.getViewVector(1.0F);
      Vec3 end = eye.add(look.scale(REACH));

      BlockHitResult hit = level.clip(new ClipContext(
         eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

      BlockPos target = null;
      EnderiumRailBlockEntity rail = null;
      if (hit.getType() == HitResult.Type.BLOCK
            && level.getBlockEntity(hit.getBlockPos()) instanceof EnderiumRailBlockEntity be) {
         target = hit.getBlockPos();
         rail = be;
      }

      UUID id = player.getUUID();
      BlockPos previous = lastTarget.get(id);
      if (Objects.equals(previous, target)) return;

      if (target != null) {
         player.displayClientMessage(describe(rail, level, target, player), true);
         lastTarget.put(id, target);
      } else {
         player.displayClientMessage(Component.empty(), true);
         lastTarget.remove(id);
      }
   }

   private static Component describe(EnderiumRailBlockEntity rail, ServerLevel level, BlockPos target, ServerPlayer player) {
      BlockState state = level.getBlockState(target);
      Direction exit = rail.getExitDirection(state);

      MutableComponent line = Component.translatable(
         "useful_ores.enderium_rail.hud.exit",
         Component.translatable("direction.useful_ores." + exit.getSerializedName())
            .withStyle(ChatFormatting.AQUA)
      ).copy();

      line = line.append(Component.literal(" - "))
         .append(Component.translatable(rail.isLinked()
            ? "useful_ores.enderium_rail.hud.linked"
            : "useful_ores.enderium_rail.hud.unlinked"));

      if (isPending(player, level, target)) {
         line = line.append(Component.literal(" "))
            .append(Component.translatable("useful_ores.enderium_rail.hud.selected")
               .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
      }
      return line;
   }

   private static boolean isPending(ServerPlayer player, ServerLevel level, BlockPos target) {
      @Nullable GlobalPos pending = EnderiumRailLinking.getPending(player);
      return pending != null && pending.dimension().equals(level.dimension()) && pending.pos().equals(target);
   }
}

