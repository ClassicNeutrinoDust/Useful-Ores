package com.neutrinodust.useful_ores.blastproof;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlastproofHudEvents {

   private static final double REACH = 5.0;

   private final Map<UUID, BlockPos> lastTarget = new HashMap<>();

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent.Post event) {
      if (!(event.getEntity() instanceof ServerPlayer player)) return;

      ServerLevel level = player.level();
      Vec3 eye = player.getEyePosition();
      Vec3 look = player.getViewVector(1.0F);
      Vec3 end = eye.add(look.scale(REACH));

      BlockHitResult hit = level.clip(new ClipContext(
         eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

      BlockPos target = null;
      if (hit.getType() == HitResult.Type.BLOCK
            && BlastproofBlockData.get(level).isBlastproof(hit.getBlockPos())) {
         target = hit.getBlockPos();
      }

      UUID id = player.getUUID();
      BlockPos previous = lastTarget.get(id);
      if (java.util.Objects.equals(previous, target)) return;

      if (target != null) {
         BlockState state = level.getBlockState(target);
         Component name = state.getBlock().getName();
         player.displayClientMessage(
            Component.translatable("useful_ores.blastproof.marker", name),
            true);
         lastTarget.put(id, target);
      } else {
         player.displayClientMessage(Component.empty(), true);
         lastTarget.remove(id);
      }
   }
}

