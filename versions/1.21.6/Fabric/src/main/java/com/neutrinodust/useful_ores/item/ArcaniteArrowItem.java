package com.neutrinodust.useful_ores.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.Nullable;

public class ArcaniteArrowItem extends ArrowItem {

    static final float HEART           = 2.0F;
    static final int   ABSORPTION_TICKS = Integer.MAX_VALUE / 2;

    public ArcaniteArrowItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack itemStack, LivingEntity owner, @Nullable ItemStack firedFromWeapon) {
        ArcaniteArrow arrow = new ArcaniteArrow(level, owner, itemStack.copyWithCount(1), firedFromWeapon);
        arrow.setBaseDamage(0.0);
        return arrow;
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
        ArcaniteArrow arrow = new ArcaniteArrow(level, position.x(), position.y(), position.z(), itemStack.copyWithCount(1), null);
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        arrow.setBaseDamage(0.0);
        return arrow;
    }

    static void applyBuff(LivingEntity target) {
        float missing = target.getMaxHealth() - target.getHealth();

        if (missing >= HEART) {
            target.heal(HEART);
        } else if (missing > 0.0F) {
            target.heal(missing);
            addAbsorption(target, HEART - missing);
        } else {
            addAbsorption(target, HEART);
        }
    }

    static void addAbsorption(LivingEntity entity, float amount) {
        float newTotal  = entity.getAbsorptionAmount() + amount;
        float rounded   = (float)(Math.ceil(newTotal / 4.0) * 4.0);
        int   amplifier = Math.max(0, (int)(rounded / 4.0f) - 1);

        entity.addEffect(new MobEffectInstance(
                MobEffects.ABSORPTION,
                ABSORPTION_TICKS,
                amplifier,
                false,
                false,
                true
        ));
        entity.setAbsorptionAmount(newTotal);
    }

    public static class ArcaniteArrow extends Arrow {

        public ArcaniteArrow(Level level, LivingEntity owner, ItemStack pickupItem, @Nullable ItemStack firedFromWeapon) {
            super(level, owner, pickupItem, firedFromWeapon);
        }

        public ArcaniteArrow(Level level, double x, double y, double z, ItemStack pickupItem, @Nullable ItemStack firedFromWeapon) {
            super(level, x, y, z, pickupItem, firedFromWeapon);
        }

        @Override
        protected void onHitEntity(EntityHitResult result) {
            Entity hitEntity = result.getEntity();
            LivingEntity target = hitEntity instanceof LivingEntity le ? le : null;

            float healthBefore     = target != null ? target.getHealth()           : 0.0F;
            float absorptionBefore = target != null ? target.getAbsorptionAmount() : 0.0F;

            super.onHitEntity(result);

            if (this.level().isClientSide()) return;
            if (target == null || !target.isAlive()) return;

            target.setHealth(healthBefore);
            target.setAbsorptionAmount(absorptionBefore);

            applyBuff(target);
        }
    }
}

