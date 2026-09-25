package com.neutrinodust.useful_ores.entity;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class MeteorBombEntity extends ThrowableItemProjectile {

   private static final float EXPLOSION_POWER = 3.0F;

   public MeteorBombEntity(EntityType<? extends MeteorBombEntity> type, Level level) {
      super(type, level);
   }

   public MeteorBombEntity(ServerLevel level, LivingEntity shooter, ItemStack itemStack) {
      super(com.neutrinodust.useful_ores.init.ModEntities.METEOR_BOMB, shooter, level, itemStack);
   }

   @Override
   protected Item getDefaultItem() {
      return ModItems.METEOR_BOMB.get();
   }

   @Override
   protected void onHit(HitResult result) {
      super.onHit(result);
      detonate();
   }

   @Override
   protected void onHitEntity(EntityHitResult result) {
      super.onHitEntity(result);
      detonate();
   }

   private void detonate() {
      if (level().isClientSide()) return;

      level().explode(this, getX(), getY(), getZ(), EXPLOSION_POWER,
         true, Level.ExplosionInteraction.TNT);

      discard();
   }
}

