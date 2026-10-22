package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.init.ModEntities;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ScheeliteArrowItem extends ArrowItem {

    private static final double DAMAGE_MULTIPLIER = 1.5;

    private static final double VANILLA_ARROW_BASE_DAMAGE = 2.0;

    public ScheeliteArrowItem(Item.Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                ScheeliteArrow arrow = new ScheeliteArrow(level, position.x(), position.y(), position.z());
                arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                arrow.setBaseDamage(VANILLA_ARROW_BASE_DAMAGE * DAMAGE_MULTIPLIER);
                return arrow;
            }
        });
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter) {
        ScheeliteArrow arrow = new ScheeliteArrow(level, shooter);
        arrow.setBaseDamage(VANILLA_ARROW_BASE_DAMAGE * DAMAGE_MULTIPLIER);
        return arrow;
    }

    public static class ScheeliteArrow extends Arrow {

        public ScheeliteArrow(EntityType<? extends ScheeliteArrow> type, Level level) {
            super(type, level);
        }

        public ScheeliteArrow(Level level, LivingEntity owner) {
            super(ModEntities.SCHEELITE_ARROW.get(), level);
            this.setPos(owner.getX(), owner.getEyeY() - 0.1F, owner.getZ());
            this.setOwner(owner);
            if (owner instanceof Player) {
                this.pickup = AbstractArrow.Pickup.ALLOWED;
            }
        }

        public ScheeliteArrow(Level level, double x, double y, double z) {
            super(ModEntities.SCHEELITE_ARROW.get(), level);
            this.setPos(x, y, z);
        }

        @Override
        protected ItemStack getPickupItem() {
            return new ItemStack(ModItems.SCHEELITE_ARROW.get());
        }
    }
}
