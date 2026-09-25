package com.neutrinodust.useful_ores.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class WirelessRedstoneRelayHudEvents {

   private static final double REACH = 5.0;

   private final Map<UUID, BlockPos> lastTarget = new HashMap<>();

   @SubscribeEvent
   public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
      if (!event.getEntity().isShiftKeyDown()) return;
      if (!(event.getEntity() instanceof ServerPlayer player)) return;
      if (WirelessRedstoneLinking.getPending(player) == null) return;

      if (!(event.getLevel().getBlockEntity(event.getPos()) instanceof WirelessRedstoneRelayBlockEntity)) return;

      WirelessRedstoneLinking.cancelPending(player);
      event.setCanceled(true);
   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent.Post event) {
      if (!(event.getEntity() instanceof ServerPlayer player)) return;

      if (!(player.level() instanceof ServerLevel level)) return;
      Vec3 eye = player.getEyePosition();
      Vec3 look = player.getViewVector(1.0F);
      Vec3 end = eye.add(look.scale(REACH));

      BlockHitResult hit = level.clip(new ClipContext(
         eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

      BlockPos target = null;
      WirelessRedstoneRelayBlockEntity relay = null;
      if (hit.getType() == HitResult.Type.BLOCK
            && level.getBlockEntity(hit.getBlockPos()) instanceof WirelessRedstoneRelayBlockEntity be) {
         target = hit.getBlockPos();
         relay = be;
      }

      UUID id = player.getUUID();
      BlockPos previous = lastTarget.get(id);
      if (Objects.equals(previous, target)) return;

      if (target != null) {
         player.displayClientMessage(describe(relay, level, target, player), true);
         lastTarget.put(id, target);
      } else {
         player.displayClientMessage(Component.empty(), true);
         lastTarget.remove(id);
      }
   }

   private static Component describe(WirelessRedstoneRelayBlockEntity relay, ServerLevel level, BlockPos target, ServerPlayer player) {
      Component modeText = switch (relay.getMode()) {
         case SINGLE -> Component.translatable("useful_ores.wireless_redstone_relay.hud.single",
            Component.translatable(relay.getLinkedPos() != null
               ? "useful_ores.wireless_redstone_relay.hud.linked"
               : "useful_ores.wireless_redstone_relay.hud.unlinked"));
         case MULTI -> Component.translatable("useful_ores.wireless_redstone_relay.hud.multi",
            relay.getBroadcastTargets().size());
         case CONNECTION -> Component.translatable("useful_ores.wireless_redstone_relay.hud.connection",
            relay.getBroadcastTargets().size());
      };

      MutableComponent line = modeText.copy();
      if (isPending(player, level, target)) {
         line = line.append(Component.literal(" "))
            .append(Component.translatable("useful_ores.wireless_redstone_relay.hud.selected")
               .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
      }
      return line;
   }

   private static boolean isPending(ServerPlayer player, ServerLevel level, BlockPos target) {
      @Nullable GlobalPos pending = WirelessRedstoneLinking.getPending(player);
      return pending != null && pending.dimension().equals(level.dimension()) && pending.pos().equals(target);
   }
}

