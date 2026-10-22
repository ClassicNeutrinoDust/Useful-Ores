package com.neutrinodust.useful_ores.compendium;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class CompendiumBookItem extends Item {

   public CompendiumBookItem(Properties properties) {
      super(properties);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (level.isClientSide()) {
         com.neutrinodust.useful_ores.client.CompendiumClientHelper.openBook();
      }
      return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
   }
}

