package com.neutrinodust.useful_ores.compendium;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CompendiumFirstJoinEvents {

   private static final String GIVEN_TAG = "useful_ores_gave_compendium";

   public static void onLogin(ServerPlayer player) {
      boolean alreadyGiven = player.get(DataComponents.CUSTOM_DATA)
            .copyTag().getBooleanOr(GIVEN_TAG, false);
      if (alreadyGiven) return;

      ItemStack book = new ItemStack(ModItems.USEFUL_ORES_COMPENDIUM.get());
      if (!player.getInventory().add(book)) {
         player.drop(book, false);
      }

      var current = player.get(DataComponents.CUSTOM_DATA);
      var updated = current.update(tag -> tag.putBoolean(GIVEN_TAG, true));
      player.setComponent(DataComponents.CUSTOM_DATA, updated);
   }
}

