package com.neutrinodust.useful_ores.compendium;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CompendiumFirstJoinEvents {

   private static final String GIVEN_TAG = "useful_ores_gave_compendium";

   public static void onLogin(ServerPlayer player) {
      if (player.getTags().contains(GIVEN_TAG)) return;

      ItemStack book = new ItemStack(ModItems.USEFUL_ORES_COMPENDIUM.get());
      if (!player.getInventory().add(book)) {
         player.drop(book, false);
      }

      player.addTag(GIVEN_TAG);
   }
}
