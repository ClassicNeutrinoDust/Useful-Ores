package com.neutrinodust.useful_ores.barrier;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NyxiumBarrierBreakEvents {

   public boolean onBeforeBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
      if (!(state.getBlock() instanceof NyxiumDarkBarrierBlock)) return true;
      if (player == null || player.isCreative()) return true;

      ItemStack tool = player.getMainHandItem();
      if (isSubspacePickaxe(tool)) return true;

      player.sendSystemMessage(
         Component.translatable("message.useful_ores.nyxium_dark_barrier.wrong_tool"));
      return false;
   }

   private static boolean isSubspacePickaxe(ItemStack stack) {
      if (stack.isEmpty()) return false;
      return stack.is(ModItems.SUBSPACE_ITEMS.get(1).get())
         || stack.is(com.neutrinodust.useful_ores.attribution.ModAttributedItems.ATTRIBUTED_SUBSPACE_PICKAXE.get());
   }
}

