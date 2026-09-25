package com.neutrinodust.useful_ores.pedestal;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PedestalBreakEvents {

   public boolean onBeforeBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
      if (!(state.getBlock() instanceof AncientPedestalBlock)) return true;
      if (player == null || player.isCreative()) return true;

      ItemStack tool = player.getMainHandItem();
      if (isVoidshardPickaxe(tool)) return true;

      player.sendSystemMessage(
         Component.translatable("message.useful_ores.ancient_pedestal.wrong_tool"));
      return false;
   }

   private static boolean isVoidshardPickaxe(ItemStack stack) {
      if (stack.isEmpty()) return false;

      return stack.is(ModItems.VOIDSHARD_ITEMS.get(4).get());
   }
}

