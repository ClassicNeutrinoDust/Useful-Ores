package com.neutrinodust.useful_ores.blastproof;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class BlastproofEvents {

   public InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
      if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
      ItemStack stack = player.getItemInHand(hand);
      if (!stack.is(ItemTags.AXES)) return InteractionResult.PASS;

      if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;

      BlockPos pos = hitResult.getBlockPos();
      BlastproofBlockData data = BlastproofBlockData.get(serverLevel);
      if (!data.isBlastproof(pos)) return InteractionResult.PASS;

      data.unmark(pos);
      level.playSound(null, pos, SoundEvents.AXE_SCRAPE.value(), SoundSource.BLOCKS, 1.0F, 1.0F);

      return InteractionResult.SUCCESS;
   }
}

