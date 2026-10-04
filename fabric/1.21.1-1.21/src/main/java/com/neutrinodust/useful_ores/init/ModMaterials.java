package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.util.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

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

    public static Holder<ArmorMaterial> CHROMITE_ARMOR;
    public static Holder<ArmorMaterial> NYXIUM_ARMOR;
    public static Holder<ArmorMaterial> PHOSGENE_ARMOR;
    public static Holder<ArmorMaterial> ARCANITE_ARMOR;
    public static Holder<ArmorMaterial> FULGURITE_ARMOR;
    public static Holder<ArmorMaterial> OSMIUM_ARMOR;
    public static Holder<ArmorMaterial> SOLARITE_ARMOR;
    public static Holder<ArmorMaterial> SPERRYLITE_ARMOR;
    public static Holder<ArmorMaterial> ARGENTITE_ARMOR;
    public static Holder<ArmorMaterial> ZEPHYRITE_ARMOR;
    public static Holder<ArmorMaterial> ILMENITE_ARMOR;
    public static Holder<ArmorMaterial> SCHEELITE_ARMOR;
    public static Holder<ArmorMaterial> ENDERIUM_ARMOR;
    public static Holder<ArmorMaterial> VOIDSHARD_ARMOR;
    public static Holder<ArmorMaterial> SUBSPACE_ARMOR;
    public static Holder<ArmorMaterial> LONSDALEITE_ARMOR;
    public static Holder<ArmorMaterial> PAINITE_ARMOR;

    public static void onConfigLoad() {
        CHROMITE  = tool(BlockTags.INCORRECT_FOR_DIAMOND_TOOL,   "chromite",  ModTags.Items.CHROMITE_INGOT);
        NYXIUM    = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "nyxium",    ModTags.Items.NYXIUM_INGOT);
        PHOSGENE  = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "phosgene",  ModTags.Items.PHOSGENE_INGOT);
        ARCANITE  = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "arcanite",  ModTags.Items.ARCANITE_INGOT);
        FULGURITE  = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "fulgurite", ModTags.Items.FULGURITE_INGOT);
        OSMIUM    = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "osmium",    ModTags.Items.OSMIUM_INGOT);
        SOLARITE  = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "solarite",  ModTags.Items.SOLARITE_INGOT);
        SPERRYLITE = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "sperrylite", ModTags.Items.SPERRYLITE_INGOT);
        ARGENTITE = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "argentite", ModTags.Items.ARGENTITE_INGOT);
        ZEPHYRITE = tool(BlockTags.INCORRECT_FOR_STONE_TOOL,      "zephyrite", ModTags.Items.ZEPHYRITE_INGOT);
        ILMENITE  = tool(BlockTags.INCORRECT_FOR_DIAMOND_TOOL,   "ilmenite",  ModTags.Items.ILMENITE_INGOT);
        SCHEELITE = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "scheelite", ModTags.Items.SCHEELITE_INGOT);
        ENDERIUM  = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "enderium",  ModTags.Items.ENDERIUM_INGOT);
        VOIDSHARD = tool(ModTags.Blocks.INCORRECT_FOR_VOIDSHARD_TOOL, "voidshard", ModTags.Items.VOIDSHARD_INGOT);
        SUBSPACE  = tool(ModTags.Blocks.INCORRECT_FOR_VOIDSHARD_TOOL, "subspace",  ModTags.Items.SUBSPACE_INGOT);
        LONSDALEITE = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "lonsdaleite", ModTags.Items.LONSDALEITE_INGOT);
        PAINITE   = tool(BlockTags.INCORRECT_FOR_DIAMOND_TOOL,   "painite",   ModTags.Items.PAINITE_INGOT);

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

    private static Tier tool(TagKey<Block> incorrectFor, String name, TagKey<Item> repairItem) {
        ModConfig.MaterialEntries c = ModConfig.ENTRIES.get(name);
        return new Tier() {
            @Override public int getUses() { return c.toolDurability.get(); }
            @Override public float getSpeed() { return c.miningSpeed.get().floatValue(); }
            @Override public float getAttackDamageBonus() { return c.attackDamage.get().floatValue(); }
            @Override public int getEnchantmentValue() { return c.toolEnchantability.get(); }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(repairItem); }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return incorrectFor; }
        };
    }

    private static Holder<ArmorMaterial> armor(String name, Holder<net.minecraft.sounds.SoundEvent> sound,
                                                TagKey<Item> repairItem) {
        ModConfig.MaterialEntries c = ModConfig.ENTRIES.get(name);
        java.util.EnumMap<ArmorItem.Type, Integer> defense = new java.util.EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BOOTS, c.bootsDefense.get());
        defense.put(ArmorItem.Type.LEGGINGS, c.leggingsDefense.get());
        defense.put(ArmorItem.Type.CHESTPLATE, c.chestplateDefense.get());
        defense.put(ArmorItem.Type.HELMET, c.helmetDefense.get());
        defense.put(ArmorItem.Type.BODY, c.bodyDefense.get());
        ArmorMaterial material = new ArmorMaterial(defense, c.armorEnchantability.get(), sound,
                () -> Ingredient.of(repairItem),
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("useful_ores", name))),
                c.toughness.get().floatValue(), c.knockbackResistance.get().floatValue());
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("useful_ores", name);
        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, id, material);
    }

    public static Properties armorProperties(String name, Properties properties, ArmorItem.Type type) {
        ModConfig.MaterialEntries c = ModConfig.ENTRIES.get(name);
        return properties.durability(type.getDurability(c.armorDurabilityMultiplier.get()));
    }
}
