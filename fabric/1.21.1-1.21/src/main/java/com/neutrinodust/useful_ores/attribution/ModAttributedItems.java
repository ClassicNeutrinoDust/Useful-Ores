package com.neutrinodust.useful_ores.attribution;

import com.neutrinodust.useful_ores.init.ModMaterials;
import com.neutrinodust.useful_ores.init.ModRegisters;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ArmorMaterial;
import com.neutrinodust.useful_ores.init.ModRegisters.RegisteredItem;

public class ModAttributedItems {

    static Component attributedName(ItemStack stack, Component base) {
        String id = stack.get(ModDataComponents.ATTRIBUTED_EFFECT);
        if (id == null) return base;
        String[] words = id.split("_");
        StringBuilder pretty = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            if (!pretty.isEmpty()) pretty.append(' ');
            pretty.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return Component.literal(pretty + " ").append(base);
    }

    private static class AttributedItem extends Item {
        AttributedItem(Properties props) { super(props); }

        @Override
        public Component getName(ItemStack stack) {
            return attributedName(stack, super.getName(stack));
        }
    }

    private static class AttributedArmorItem extends ArmorItem {
        AttributedArmorItem(Holder<ArmorMaterial> material, Type type, Properties props) {
            super(material, type, props);
        }

        @Override
        public Component getName(ItemStack stack) {
            return attributedName(stack, super.getName(stack));
        }
    }

    public static final RegisteredItem<Item> ATTRIBUTED_SUBSPACE_INGOT = ModRegisters.registerItem(
            "attributed_subspace_ingot", AttributedItem::new,
            new Item.Properties().rarity(Rarity.EPIC).stacksTo(16)
    );

    public static final RegisteredItem<Item> ATTRIBUTED_SUBSPACE_HELMET = ModRegisters.registerItem(
            "attributed_subspace_helmet",
            p -> new AttributedArmorItem(ModMaterials.SUBSPACE_ARMOR, ArmorItem.Type.HELMET, ModMaterials.armorProperties("subspace", p, ArmorItem.Type.HELMET)),
            new Item.Properties().rarity(Rarity.EPIC)
    );
    public static final RegisteredItem<Item> ATTRIBUTED_SUBSPACE_CHESTPLATE = ModRegisters.registerItem(
            "attributed_subspace_chestplate",
            p -> new AttributedArmorItem(ModMaterials.SUBSPACE_ARMOR, ArmorItem.Type.CHESTPLATE, ModMaterials.armorProperties("subspace", p, ArmorItem.Type.CHESTPLATE)),
            new Item.Properties().rarity(Rarity.EPIC)
    );
    public static final RegisteredItem<Item> ATTRIBUTED_SUBSPACE_LEGGINGS = ModRegisters.registerItem(
            "attributed_subspace_leggings",
            p -> new AttributedArmorItem(ModMaterials.SUBSPACE_ARMOR, ArmorItem.Type.LEGGINGS, ModMaterials.armorProperties("subspace", p, ArmorItem.Type.LEGGINGS)),
            new Item.Properties().rarity(Rarity.EPIC)
    );
    public static final RegisteredItem<Item> ATTRIBUTED_SUBSPACE_BOOTS = ModRegisters.registerItem(
            "attributed_subspace_boots",
            p -> new AttributedArmorItem(ModMaterials.SUBSPACE_ARMOR, ArmorItem.Type.BOOTS, ModMaterials.armorProperties("subspace", p, ArmorItem.Type.BOOTS)),
            new Item.Properties().rarity(Rarity.EPIC)
    );

    private static class AttributedSwordItem extends SwordItem {
        AttributedSwordItem(Tier material, float attackDamage, float attackSpeed, Properties props) {
            super(material, props.attributes(ModRegisters.createSwordAttributes(material, attackDamage, attackSpeed)));
        }
        @Override public Component getName(ItemStack stack) { return attributedName(stack, super.getName(stack)); }
        @Override public boolean hurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target, net.minecraft.world.entity.LivingEntity attacker) {
            super.hurtEnemy(stack, target, attacker);
            return true;
        }
        @Override public void postHurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target, net.minecraft.world.entity.LivingEntity attacker) {
            super.postHurtEnemy(stack, target, attacker);
            applyToolEffect(stack, target, attacker);
        }
    }

    public static final RegisteredItem<Item> ATTRIBUTED_SUBSPACE_SWORD = ModRegisters.registerItem(
            "attributed_subspace_sword",
            p -> new AttributedSwordItem(ModMaterials.SUBSPACE, 15.0F, -2.6F, p) ,
            new Item.Properties().rarity(Rarity.EPIC)
    );

    private static class AttributedAxeItem extends net.minecraft.world.item.AxeItem {
        AttributedAxeItem(Tier material,
                           float attackDamage, float attackSpeed, Properties props) {
            super(material, props.attributes(ModRegisters.createDiggerAttributes(material, attackDamage, attackSpeed)));
        }

        @Override
        public Component getName(ItemStack stack) {
            return attributedName(stack, super.getName(stack));
        }

        @Override
        public void postHurtEnemy(ItemStack stack,
                                   net.minecraft.world.entity.LivingEntity target,
                                   net.minecraft.world.entity.LivingEntity attacker) {
            super.postHurtEnemy(stack, target, attacker);
            applyToolEffect(stack, target, attacker);
        }
    }

    public static final RegisteredItem<Item> ATTRIBUTED_SUBSPACE_AXE = ModRegisters.registerItem(
            "attributed_subspace_axe",
            p -> new AttributedAxeItem(ModMaterials.SUBSPACE, 17.0F, -2.8F, p),
            new Item.Properties().rarity(Rarity.EPIC)
    );

    private static class AttributedPickaxeItem extends PickaxeItem {
        AttributedPickaxeItem(Tier material, float attackDamage, float attackSpeed, Properties props) {
            super(material, props.attributes(ModRegisters.createDiggerAttributes(material, attackDamage, attackSpeed)));
        }
        @Override public Component getName(ItemStack stack) { return attributedName(stack, super.getName(stack)); }
        @Override public boolean hurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target, net.minecraft.world.entity.LivingEntity attacker) {
            super.hurtEnemy(stack, target, attacker);
            return true;
        }
        @Override public void postHurtEnemy(ItemStack stack, net.minecraft.world.entity.LivingEntity target, net.minecraft.world.entity.LivingEntity attacker) {
            super.postHurtEnemy(stack, target, attacker);
            applyToolEffect(stack, target, attacker);
        }
    }

    public static final RegisteredItem<Item> ATTRIBUTED_SUBSPACE_PICKAXE = ModRegisters.registerItem(
            "attributed_subspace_pickaxe",
            p -> new AttributedPickaxeItem(ModMaterials.SUBSPACE, 13.0F, -2.8F, p) ,
            new Item.Properties().rarity(Rarity.EPIC)
    );

    private static class AttributedShovelItem extends ShovelItem {
        AttributedShovelItem(Tier material,
                              float attackDamage, float attackSpeed, Properties props) {
            super(material, props.attributes(ModRegisters.createDiggerAttributes(material, attackDamage, attackSpeed)));
        }

        @Override
        public Component getName(ItemStack stack) {
            return attributedName(stack, super.getName(stack));
        }

        @Override
        public void postHurtEnemy(ItemStack stack,
                                   net.minecraft.world.entity.LivingEntity target,
                                   net.minecraft.world.entity.LivingEntity attacker) {
            super.postHurtEnemy(stack, target, attacker);
            applyToolEffect(stack, target, attacker);
        }
    }

    public static final RegisteredItem<Item> ATTRIBUTED_SUBSPACE_SHOVEL = ModRegisters.registerItem(
            "attributed_subspace_shovel",
            p -> new AttributedShovelItem(ModMaterials.SUBSPACE, 11.0F, -3.0F, p),
            new Item.Properties().rarity(Rarity.EPIC)
    );

    private static class AttributedHoeItem extends HoeItem {
        AttributedHoeItem(Tier material,
                           float attackDamage, float attackSpeed, Properties props) {
            super(material, props.attributes(ModRegisters.createDiggerAttributes(material, attackDamage, attackSpeed)));
        }

        @Override
        public Component getName(ItemStack stack) {
            return attributedName(stack, super.getName(stack));
        }

        @Override
        public void postHurtEnemy(ItemStack stack,
                                   net.minecraft.world.entity.LivingEntity target,
                                   net.minecraft.world.entity.LivingEntity attacker) {
            super.postHurtEnemy(stack, target, attacker);
            applyToolEffect(stack, target, attacker);
        }
    }

    public static final RegisteredItem<Item> ATTRIBUTED_SUBSPACE_HOE = ModRegisters.registerItem(
            "attributed_subspace_hoe",
            p -> new AttributedHoeItem(ModMaterials.SUBSPACE, 2.0F, -3.0F, p),
            new Item.Properties().rarity(Rarity.EPIC)
    );

    public static final int TOOL_EFFECT_DURATION_TICKS = 200;

    private static final float INSTANT_EFFECT_AMOUNT = 6.0F;

    static void applyToolEffect(ItemStack stack,
                                 net.minecraft.world.entity.LivingEntity target,
                                 net.minecraft.world.entity.LivingEntity attacker) {
        String id = stack.get(ModDataComponents.ATTRIBUTED_EFFECT);
        if (id == null) return;
        AttributedEffects attribute = AttributedEffects.byId(id);
        if (attribute == null) return;
        if (target == null || target.isDeadOrDying()) return;

        net.minecraft.world.entity.LivingEntity recipient =
                attribute.buffsAttacker() ? attacker : target;

        if (attribute.instant()) {
            if (attribute == AttributedEffects.HEALING) {
                recipient.heal(INSTANT_EFFECT_AMOUNT);
            } else if (attribute == AttributedEffects.HARMING) {
                recipient.hurt(recipient.damageSources().magic(), INSTANT_EFFECT_AMOUNT);
            }
            return;
        }

        for (net.minecraft.world.effect.MobEffectInstance instance :
                attribute.freshHitInstances(TOOL_EFFECT_DURATION_TICKS)) {
            recipient.addEffect(instance);
        }
    }

    public static final java.util.List<RegisteredItem<Item>> ALL = java.util.List.of(
            ATTRIBUTED_SUBSPACE_INGOT,
            ATTRIBUTED_SUBSPACE_HELMET, ATTRIBUTED_SUBSPACE_CHESTPLATE,
            ATTRIBUTED_SUBSPACE_LEGGINGS, ATTRIBUTED_SUBSPACE_BOOTS,
            ATTRIBUTED_SUBSPACE_SWORD, ATTRIBUTED_SUBSPACE_AXE,
            ATTRIBUTED_SUBSPACE_PICKAXE, ATTRIBUTED_SUBSPACE_SHOVEL, ATTRIBUTED_SUBSPACE_HOE
    );

    public static void init() {

    }
}

