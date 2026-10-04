package com.neutrinodust.useful_ores.blastproof;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class BlastproofEvents {

   @SubscribeEvent(priority = EventPriority.HIGH)
   public void onRightClickAxeRemoveGlue(PlayerInteractEvent.RightClickBlock event) {
      ItemStack stack = event.getItemStack();
      if (!(stack.getItem() instanceof AxeItem)) return;

      Level level = event.getLevel();
      if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;

      BlockPos pos = event.getPos();
      BlastproofBlockData data = BlastproofBlockData.get(serverLevel);
      if (!data.isBlastproof(pos)) return;

      data.unmark(pos);
      level.playSound(null, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);

      event.setCanceled(true);
      event.setCancellationResult(InteractionResult.SUCCESS);
   }

   @SubscribeEvent
   public void onExplosionDetonate(ExplosionEvent.Detonate event) {
      if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

      BlastproofBlockData data = BlastproofBlockData.get(serverLevel);
      event.getAffectedBlocks().removeIf(data::isBlastproof);

      ExplosionContext.setActive(true);
   }

   @SubscribeEvent
   public void onPlayerTickEnd(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
      ExplosionContext.setActive(false);
   }
}

