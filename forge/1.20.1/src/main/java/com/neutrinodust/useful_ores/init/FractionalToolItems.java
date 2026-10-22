package com.neutrinodust.useful_ores.init;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public final class FractionalToolItems {
    private FractionalToolItems() {}

    private static Multimap<Attribute, AttributeModifier> addDamageDelta(
            Multimap<Attribute, AttributeModifier> base, EquipmentSlot slot, double delta) {
        if (slot != EquipmentSlot.MAINHAND || Math.abs(delta) < 1.0E-7D) return base;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> result = ImmutableMultimap.builder();
        for (var entry : base.entries()) {
            Attribute attribute = entry.getKey();
            AttributeModifier modifier = entry.getValue();
            if (attribute == Attributes.ATTACK_DAMAGE) {
                modifier = new AttributeModifier(modifier.getId(), modifier.getName(),
                        modifier.getAmount() + delta, modifier.getOperation());
            }
            result.put(attribute, modifier);
        }
        return result.build();
    }

    public static class Sword extends SwordItem {
        private final double damageDelta;
        private final BiConsumer<LivingEntity, LivingEntity> onHit;
        public Sword(Tier tier, float damage, float speed, Item.Properties properties) {
            this(tier, damage, speed, properties, null);
        }
        public Sword(Tier tier, float damage, float speed, Item.Properties properties,
                     BiConsumer<LivingEntity, LivingEntity> onHit) {
            super(tier, Math.round(damage), speed, properties);
            this.damageDelta = damage - Math.round(damage);
            this.onHit = onHit;
        }
        @Override public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
            return addDamageDelta(super.getDefaultAttributeModifiers(slot), slot, damageDelta);
        }
        @Override public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            super.hurtEnemy(stack, target, attacker);
            if (onHit != null) onHit.accept(target, attacker);
            return true;
        }
    }

    public static class Pickaxe extends PickaxeItem {
        private final double damageDelta;
        public Pickaxe(Tier tier, float damage, float speed, Item.Properties properties) {
            super(tier, Math.round(damage), speed, properties);
            damageDelta = damage - Math.round(damage);
        }
        @Override public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
            return addDamageDelta(super.getDefaultAttributeModifiers(slot), slot, damageDelta);
        }
    }

    public static class Axe extends AxeItem {
        private final double damageDelta;
        public Axe(Tier tier, float damage, float speed, Item.Properties properties) {
            super(tier, Math.round(damage), speed, properties);
            damageDelta = damage - Math.round(damage);
        }
        @Override public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
            return addDamageDelta(super.getDefaultAttributeModifiers(slot), slot, damageDelta);
        }
    }

    public static class Hoe extends HoeItem {
        private final double damageDelta;
        public Hoe(Tier tier, float damage, float speed, Item.Properties properties) {
            super(tier, Math.round(damage), speed, properties);
            damageDelta = damage - Math.round(damage);
        }
        @Override public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
            return addDamageDelta(super.getDefaultAttributeModifiers(slot), slot, damageDelta);
        }
    }

    public static class Shovel extends ShovelItem {
        private final double damageDelta;
        public Shovel(Tier tier, float damage, float speed, Item.Properties properties) {
            super(tier, Math.round(damage), speed, properties);
            damageDelta = damage - Math.round(damage);
        }
        @Override public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
            return addDamageDelta(super.getDefaultAttributeModifiers(slot), slot, damageDelta);
        }
    }
}
