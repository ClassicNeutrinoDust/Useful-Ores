package com.neutrinodust.useful_ores.compendium;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class CompendiumBookItem extends Item {

   public CompendiumBookItem(Properties properties) {
      super(properties);
   }

   @Override
   public InteractionResult use(Level level, Player player, InteractionHand hand) {
      if (level.isClientSide()) {
         com.neutrinodust.useful_ores.client.CompendiumClientHelper.openBook();
         return InteractionResult.CONSUME;
      }
      return InteractionResult.SUCCESS;
   }
}

