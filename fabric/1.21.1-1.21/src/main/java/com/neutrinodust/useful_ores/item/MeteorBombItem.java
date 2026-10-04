package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.entity.MeteorBombEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MeteorBombItem extends Item {

   public static final float PROJECTILE_SHOOT_POWER = 1.5F;

   public MeteorBombItem(Properties props) {
      super(props);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);

      level.playSound(null, player.getX(), player.getY(), player.getZ(),
         SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F,
         0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

      if (level instanceof ServerLevel serverLevel) {
         ServerLevel server = (ServerLevel) level;
         MeteorBombEntity projectile = new MeteorBombEntity(server, player, stack.copy());
         projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
         server.addFreshEntity(projectile);

      }

      player.awardStat(Stats.ITEM_USED.get(this));
      stack.consume(1, player);

      return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
   }
}

