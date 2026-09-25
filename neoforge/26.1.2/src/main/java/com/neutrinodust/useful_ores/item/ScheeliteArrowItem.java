package com.neutrinodust.useful_ores.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class ScheeliteArrowItem extends ArrowItem {

    private static final double DAMAGE_MULTIPLIER = 1.5;

    private static final double VANILLA_ARROW_BASE_DAMAGE = 2.0;

    public ScheeliteArrowItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack itemStack, LivingEntity owner, @Nullable ItemStack firedFromWeapon) {
        Arrow arrow = new Arrow(level, owner, itemStack.copyWithCount(1), firedFromWeapon);
        arrow.setBaseDamage(VANILLA_ARROW_BASE_DAMAGE * DAMAGE_MULTIPLIER);
        return arrow;
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
        Arrow arrow = new Arrow(level, position.x(), position.y(), position.z(), itemStack.copyWithCount(1), null);
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        arrow.setBaseDamage(VANILLA_ARROW_BASE_DAMAGE * DAMAGE_MULTIPLIER);
        return arrow;
    }
}

