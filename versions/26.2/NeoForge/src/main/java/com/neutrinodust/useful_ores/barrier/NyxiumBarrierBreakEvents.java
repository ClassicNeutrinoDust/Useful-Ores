package com.neutrinodust.useful_ores.barrier;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

public class NyxiumBarrierBreakEvents {

   @SubscribeEvent
   public void onBreak(BreakBlockEvent event) {
      if (!(event.getState().getBlock() instanceof NyxiumDarkBarrierBlock)) return;
      if (event.getPlayer() == null || event.getPlayer().isCreative()) return;

      ItemStack tool = event.getPlayer().getMainHandItem();
      if (isSubspacePickaxe(tool)) return;

      event.setCanceled(true);
      event.getPlayer().sendSystemMessage(
         Component.translatable("message.useful_ores.nyxium_dark_barrier.wrong_tool"));
   }

   private static boolean isSubspacePickaxe(ItemStack stack) {
      if (stack.isEmpty()) return false;
      return stack.is(ModItems.SUBSPACE_ITEMS.get(1).get())
         || stack.is(com.neutrinodust.useful_ores.attribution.ModAttributedItems.ATTRIBUTED_SUBSPACE_PICKAXE.get());
   }
}

