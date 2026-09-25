package com.neutrinodust.useful_ores.compendium;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class CompendiumFirstJoinEvents {

   private static final String GIVEN_TAG = "useful_ores_gave_compendium";

   @SubscribeEvent
   public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      if (player.level().isClientSide()) return;

      var data = player.getPersistentData();
      if (data.getBooleanOr(GIVEN_TAG, false)) return;

      ItemStack book = new ItemStack(ModItems.USEFUL_ORES_COMPENDIUM.get());
      if (!player.getInventory().add(book)) {
         player.drop(book, false, Prediction.SERVER_ONLY);
      }

      data.putBoolean(GIVEN_TAG, true);
   }
}

