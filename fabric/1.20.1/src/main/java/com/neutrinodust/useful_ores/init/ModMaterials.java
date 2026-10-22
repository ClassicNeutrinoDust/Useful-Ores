package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.util.ModTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class ModMaterials {

    public static Tier CHROMITE;
    public static Tier NYXIUM;
    public static Tier PHOSGENE;
    public static Tier ARCANITE;
    public static Tier FULGURITE;
    public static Tier OSMIUM;
    public static Tier SOLARITE;
    public static Tier SPERRYLITE;
    public static Tier ARGENTITE;
    public static Tier ZEPHYRITE;
    public static Tier ILMENITE;
    public static Tier SCHEELITE;
    public static Tier ENDERIUM;
    public static Tier VOIDSHARD;
    public static Tier SUBSPACE;
    public static Tier LONSDALEITE;
    public static Tier PAINITE;

    public static ArmorMaterial CHROMITE_ARMOR;
    public static ArmorMaterial NYXIUM_ARMOR;
    public static ArmorMaterial PHOSGENE_ARMOR;
    public static ArmorMaterial ARCANITE_ARMOR;
    public static ArmorMaterial FULGURITE_ARMOR;
    public static ArmorMaterial OSMIUM_ARMOR;
    public static ArmorMaterial SOLARITE_ARMOR;
    public static ArmorMaterial SPERRYLITE_ARMOR;
    public static ArmorMaterial ARGENTITE_ARMOR;
    public static ArmorMaterial ZEPHYRITE_ARMOR;
    public static ArmorMaterial ILMENITE_ARMOR;
    public static ArmorMaterial SCHEELITE_ARMOR;
    public static ArmorMaterial ENDERIUM_ARMOR;
    public static ArmorMaterial VOIDSHARD_ARMOR;
    public static ArmorMaterial SUBSPACE_ARMOR;
    public static ArmorMaterial LONSDALEITE_ARMOR;
    public static ArmorMaterial PAINITE_ARMOR;

    public static void onConfigLoad() {
        CHROMITE  = tool(3,   "chromite",  ModTags.Items.CHROMITE_INGOT);
        NYXIUM    = tool(4, "nyxium",    ModTags.Items.NYXIUM_INGOT);
        PHOSGENE  = tool(2,      "phosgene",  ModTags.Items.PHOSGENE_INGOT);
        ARCANITE  = tool(4, "arcanite",  ModTags.Items.ARCANITE_INGOT);
        FULGURITE  = tool(2,      "fulgurite", ModTags.Items.FULGURITE_INGOT);
        OSMIUM    = tool(2,      "osmium",    ModTags.Items.OSMIUM_INGOT);
        SOLARITE  = tool(4, "solarite",  ModTags.Items.SOLARITE_INGOT);
        SPERRYLITE = tool(2,      "sperrylite", ModTags.Items.SPERRYLITE_INGOT);
        ARGENTITE = tool(2,      "argentite", ModTags.Items.ARGENTITE_INGOT);
        ZEPHYRITE = tool(1,      "zephyrite", ModTags.Items.ZEPHYRITE_INGOT);
        ILMENITE  = tool(3,   "ilmenite",  ModTags.Items.ILMENITE_INGOT);
        SCHEELITE = tool(2,      "scheelite", ModTags.Items.SCHEELITE_INGOT);
        ENDERIUM  = tool(4, "enderium",  ModTags.Items.ENDERIUM_INGOT);
        VOIDSHARD = tool(5, "voidshard", ModTags.Items.VOIDSHARD_INGOT);
        SUBSPACE  = tool(5, "subspace",  ModTags.Items.SUBSPACE_INGOT);
        LONSDALEITE = tool(4, "lonsdaleite", ModTags.Items.LONSDALEITE_INGOT);
        PAINITE   = tool(3,   "painite",   ModTags.Items.PAINITE_INGOT);

        CHROMITE_ARMOR = armor("chromite", SoundEvents.ARMOR_EQUIP_IRON, ModTags.Items.CHROMITE_INGOT);
        NYXIUM_ARMOR = armor("nyxium", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.NYXIUM_INGOT);
        PHOSGENE_ARMOR = armor("phosgene", SoundEvents.ARMOR_EQUIP_IRON, ModTags.Items.PHOSGENE_INGOT);
        ARCANITE_ARMOR = armor("arcanite", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.ARCANITE_INGOT);
        FULGURITE_ARMOR = armor("fulgurite", SoundEvents.ARMOR_EQUIP_ELYTRA, ModTags.Items.FULGURITE_INGOT);
        OSMIUM_ARMOR = armor("osmium", SoundEvents.ARMOR_EQUIP_DIAMOND, ModTags.Items.OSMIUM_INGOT);
        SOLARITE_ARMOR = armor("solarite", SoundEvents.ARMOR_EQUIP_IRON, ModTags.Items.SOLARITE_INGOT);
        SPERRYLITE_ARMOR = armor("sperrylite", SoundEvents.ARMOR_EQUIP_GOLD, ModTags.Items.SPERRYLITE_INGOT);
        ARGENTITE_ARMOR = armor("argentite", SoundEvents.ARMOR_EQUIP_GOLD, ModTags.Items.ARGENTITE_INGOT);
        ZEPHYRITE_ARMOR = armor("zephyrite", SoundEvents.ARMOR_EQUIP_GENERIC, ModTags.Items.ZEPHYRITE_INGOT);
        ILMENITE_ARMOR = armor("ilmenite", SoundEvents.ARMOR_EQUIP_TURTLE, ModTags.Items.ILMENITE_INGOT);
        SCHEELITE_ARMOR = armor("scheelite", SoundEvents.ARMOR_EQUIP_IRON, ModTags.Items.SCHEELITE_INGOT);
        ENDERIUM_ARMOR = armor("enderium", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.ENDERIUM_INGOT);
        VOIDSHARD_ARMOR = armor("voidshard", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.VOIDSHARD_INGOT);
        SUBSPACE_ARMOR = armor("subspace", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.SUBSPACE_INGOT);
        LONSDALEITE_ARMOR = armor("lonsdaleite", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.LONSDALEITE_INGOT);
        PAINITE_ARMOR = armor("painite", SoundEvents.ARMOR_EQUIP_DIAMOND, ModTags.Items.PAINITE_INGOT);
    }

    private static Tier tool(int miningLevel, String name, TagKey<Item> repairItem) {
        ModConfig.MaterialEntries c = ModConfig.ENTRIES.get(name);
        return new Tier() {
            @Override public int getUses() { return c.toolDurability.get(); }
            @Override public float getSpeed() { return c.miningSpeed.get().floatValue(); }
            @Override public float getAttackDamageBonus() { return c.attackDamage.get().floatValue(); }
            @Override public int getLevel() { return miningLevel; }
            @Override public int getEnchantmentValue() { return c.toolEnchantability.get(); }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(repairItem); }
        };
    }

    private static int baseDurability(ArmorItem.Type type) {
        switch (type) {
            case BOOTS: return 13;
            case LEGGINGS: return 15;
            case CHESTPLATE: return 16;
            default: return 11;
        }
    }

    private static ArmorMaterial armor(String name, SoundEvent sound, TagKey<Item> repairItem) {
        ModConfig.MaterialEntries c = ModConfig.ENTRIES.get(name);
        return new ArmorMaterial() {
            @Override public int getDurabilityForType(ArmorItem.Type type) { return baseDurability(type) * c.armorDurabilityMultiplier.get(); }
            @Override public int getDefenseForType(ArmorItem.Type type) {
                switch (type) {
                    case BOOTS: return c.bootsDefense.get();
                    case LEGGINGS: return c.leggingsDefense.get();
                    case CHESTPLATE: return c.chestplateDefense.get();
                    default: return c.helmetDefense.get();
                }
            }
            @Override public int getEnchantmentValue() { return c.armorEnchantability.get(); }
            @Override public SoundEvent getEquipSound() { return sound; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(repairItem); }
            @Override public String getName() { return "useful_ores:" + name; }
            @Override public float getToughness() { return c.toughness.get().floatValue(); }
            @Override public float getKnockbackResistance() { return c.knockbackResistance.get().floatValue(); }
        };
    }

    public static Properties armorProperties(String name, Properties properties, ArmorItem.Type type) {
        ModConfig.MaterialEntries c = ModConfig.ENTRIES.get(name);
        return properties.durability(baseDurability(type) * c.armorDurabilityMultiplier.get());
    }
}
