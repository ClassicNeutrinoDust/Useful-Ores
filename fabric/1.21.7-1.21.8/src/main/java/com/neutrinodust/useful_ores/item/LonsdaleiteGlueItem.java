package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.blastproof.BlastproofBlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class LonsdaleiteGlueItem extends Item {

   public LonsdaleiteGlueItem(Properties props) {
      super(props);
   }

   @Override
   public InteractionResult useOn(UseOnContext ctx) {
      Level level = ctx.getLevel();
      BlockPos pos = ctx.getClickedPos();

      if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
         return InteractionResult.SUCCESS;
      }
      if (level.getBlockState(pos).isAir()) {
         return InteractionResult.PASS;
      }

      BlastproofBlockData data = BlastproofBlockData.get(serverLevel);
      if (data.isBlastproof(pos)) {

         return InteractionResult.PASS;
      }

      data.markBlastproof(pos);

      level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

      ItemStack stack = ctx.getItemInHand();
      Player player = ctx.getPlayer();

      if (player == null || !player.getAbilities().instabuild) {
         int newDamage = stack.getDamageValue() + 1;
         if (newDamage >= stack.getMaxDamage()) {
            stack.shrink(1);
         } else {
            stack.setDamageValue(newDamage);
         }
      }

      return InteractionResult.SUCCESS;
   }
}

