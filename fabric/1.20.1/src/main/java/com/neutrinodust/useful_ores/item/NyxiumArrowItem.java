package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.init.ModEntities;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DispenserBlock;
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

public class NyxiumArrowItem extends ArrowItem {

    static final float STEAL_AMOUNT     = 2.0F;
    static final float MIN_TARGET_HP    = 1.0F;
    static final int   ABSORPTION_TICKS = Integer.MAX_VALUE / 2;

    public NyxiumArrowItem(Item.Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                NyxiumArrow arrow = new NyxiumArrow(level, position.x(), position.y(), position.z());
                arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                arrow.setBaseDamage(0.0);
                return arrow;
            }
        });
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter) {
        NyxiumArrow arrow = new NyxiumArrow(level, shooter);
        arrow.setBaseDamage(0.0);
        return arrow;
    }

    static void applySiphon(LivingEntity target, LivingEntity shooter) {
        float available = target.getHealth() - MIN_TARGET_HP;
        float stolen    = Math.min(STEAL_AMOUNT, Math.max(0.0F, available));
        if (stolen <= 0.0F) return;

        target.setHealth(target.getHealth() - stolen);

        float missing = shooter.getMaxHealth() - shooter.getHealth();

        if (missing >= stolen) {
            shooter.heal(stolen);
        } else if (missing > 0.0F) {
            shooter.heal(missing);
            addAbsorption(shooter, stolen - missing);
        } else {
            addAbsorption(shooter, stolen);
        }
    }

    static void addAbsorption(LivingEntity entity, float amount) {
        float current   = entity.getAbsorptionAmount();
        float newTotal  = current + amount;
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

    public static class NyxiumArrow extends Arrow {

        public NyxiumArrow(EntityType<? extends NyxiumArrow> type, Level level) {
            super(type, level);
        }

        public NyxiumArrow(Level level, LivingEntity owner) {
            super(ModEntities.NYXIUM_ARROW, level);
            this.setPos(owner.getX(), owner.getEyeY() - 0.1F, owner.getZ());
            this.setOwner(owner);
            if (owner instanceof Player) {
                this.pickup = AbstractArrow.Pickup.ALLOWED;
            }
        }

        public NyxiumArrow(Level level, double x, double y, double z) {
            super(ModEntities.NYXIUM_ARROW, level);
            this.setPos(x, y, z);
        }

        @Override
        protected ItemStack getPickupItem() {
            return new ItemStack(ModItems.NYXIUM_ARROW.get());
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

            Entity ownerEntity = this.getOwner();
            if (!(ownerEntity instanceof LivingEntity shooter) || !shooter.isAlive()) return;

            applySiphon(target, shooter);
        }
    }
}

