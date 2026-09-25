package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.util.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ModMaterials {

    public static ToolMaterial CHROMITE;
    public static ToolMaterial NYXIUM;
    public static ToolMaterial PHOSGENE;
    public static ToolMaterial ARCANITE;
    public static ToolMaterial FULGURITE;
    public static ToolMaterial OSMIUM;
    public static ToolMaterial SOLARITE;
    public static ToolMaterial SPERRYLITE;
    public static ToolMaterial ARGENTITE;
    public static ToolMaterial ZEPHYRITE;
    public static ToolMaterial ILMENITE;
    public static ToolMaterial SCHEELITE;
    public static ToolMaterial ENDERIUM;
    public static ToolMaterial VOIDSHARD;
    public static ToolMaterial SUBSPACE;
    public static ToolMaterial LONSDALEITE;
    public static ToolMaterial PAINITE;

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
        CHROMITE  = tool(BlockTags.INCORRECT_FOR_DIAMOND_TOOL,   "chromite",  ModTags.Items.CHROMITE_INGOT);
        NYXIUM    = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "nyxium",    ModTags.Items.NYXIUM_INGOT);
        PHOSGENE        = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "phosgene",        ModTags.Items.PHOSGENE_INGOT);
        ARCANITE     = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "arcanite",     ModTags.Items.ARCANITE_INGOT);
        FULGURITE    = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "fulgurite",    ModTags.Items.FULGURITE_INGOT);
        OSMIUM  = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "osmium",  ModTags.Items.OSMIUM_INGOT);
        SOLARITE   = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "solarite",   ModTags.Items.SOLARITE_INGOT);
        SPERRYLITE    = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "sperrylite",    ModTags.Items.SPERRYLITE_INGOT);
        ARGENTITE      = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "argentite",      ModTags.Items.ARGENTITE_INGOT);
        ZEPHYRITE         = tool(BlockTags.INCORRECT_FOR_STONE_TOOL,     "zephyrite",         ModTags.Items.ZEPHYRITE_INGOT);
        ILMENITE    = tool(BlockTags.INCORRECT_FOR_DIAMOND_TOOL,   "ilmenite",    ModTags.Items.ILMENITE_INGOT);
        SCHEELITE    = tool(BlockTags.INCORRECT_FOR_IRON_TOOL,      "scheelite",    ModTags.Items.SCHEELITE_INGOT);
        ENDERIUM    = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "enderium",    ModTags.Items.ENDERIUM_INGOT);
        VOIDSHARD   = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "voidshard",   ModTags.Items.VOIDSHARD_INGOT);
        SUBSPACE    = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "subspace",    ModTags.Items.SUBSPACE_INGOT);
        LONSDALEITE = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "lonsdaleite", ModTags.Items.LONSDALEITE_INGOT);
        PAINITE = tool(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, "painite", ModTags.Items.PAINITE_INGOT);

        CHROMITE_ARMOR  = armor("chromite",  SoundEvents.ARMOR_EQUIP_IRON,      ModTags.Items.CHROMITE_INGOT);
        NYXIUM_ARMOR    = armor("nyxium",    SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.NYXIUM_INGOT);
        PHOSGENE_ARMOR        = armor("phosgene",        SoundEvents.ARMOR_EQUIP_IRON,      ModTags.Items.PHOSGENE_INGOT);
        ARCANITE_ARMOR     = armor("arcanite",     SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.ARCANITE_INGOT);
        FULGURITE_ARMOR    = armor("fulgurite",    SoundEvents.ARMOR_EQUIP_ELYTRA,    ModTags.Items.FULGURITE_INGOT);
        OSMIUM_ARMOR  = armor("osmium",  SoundEvents.ARMOR_EQUIP_DIAMOND,   ModTags.Items.OSMIUM_INGOT);
        SOLARITE_ARMOR   = armor("solarite",   SoundEvents.ARMOR_EQUIP_IRON,      ModTags.Items.SOLARITE_INGOT);
        SPERRYLITE_ARMOR    = armor("sperrylite",    SoundEvents.ARMOR_EQUIP_GOLD,      ModTags.Items.SPERRYLITE_INGOT);
        ARGENTITE_ARMOR      = armor("argentite",      SoundEvents.ARMOR_EQUIP_GOLD,      ModTags.Items.ARGENTITE_INGOT);
        ZEPHYRITE_ARMOR         = armor("zephyrite",         SoundEvents.ARMOR_EQUIP_GENERIC,   ModTags.Items.ZEPHYRITE_INGOT);
        ILMENITE_ARMOR    = armor("ilmenite",    SoundEvents.ARMOR_EQUIP_TURTLE,    ModTags.Items.ILMENITE_INGOT);
        SCHEELITE_ARMOR    = armor("scheelite",    SoundEvents.ARMOR_EQUIP_IRON,      ModTags.Items.SCHEELITE_INGOT);
        ENDERIUM_ARMOR    = armor("enderium",    SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.ENDERIUM_INGOT);
        VOIDSHARD_ARMOR   = armor("voidshard",   SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.VOIDSHARD_INGOT);
        SUBSPACE_ARMOR    = armor("subspace",    SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.SUBSPACE_INGOT);
        LONSDALEITE_ARMOR = armor("lonsdaleite", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.LONSDALEITE_INGOT);
        PAINITE_ARMOR = armor("painite", SoundEvents.ARMOR_EQUIP_DIAMOND, ModTags.Items.PAINITE_INGOT);
    }

    @SuppressWarnings("unchecked")
    private static ToolMaterial tool(
            net.minecraft.tags.TagKey<?> incorrectFor,
            String name,
            net.minecraft.tags.TagKey<net.minecraft.world.item.Item> repairItem) {
        ModConfig.MaterialEntries c = ModConfig.ENTRIES.get(name);

        return new ToolMaterial(
            (net.minecraft.tags.TagKey) incorrectFor,
            c.toolDurability.get(),
            c.miningSpeed.get().floatValue(),
            c.attackDamage.get().floatValue(),
            c.toolEnchantability.get(),
            repairItem
        );
    }

    private static ArmorMaterial armor(
            String name,
            Holder<net.minecraft.sounds.SoundEvent> sound,
            net.minecraft.tags.TagKey<net.minecraft.world.item.Item> repairItem) {
        return armorHolder(name, sound, repairItem);
    }

    private static ArmorMaterial armorHolder(
            String name,
            Holder<net.minecraft.sounds.SoundEvent> sound,
            net.minecraft.tags.TagKey<net.minecraft.world.item.Item> repairItem) {
        ModConfig.MaterialEntries c = ModConfig.ENTRIES.get(name);
        EnumMap<ArmorType, Integer> defense = new EnumMap<>(ArmorType.class);

        defense.put(ArmorType.BOOTS, c.bootsDefense.get());
        defense.put(ArmorType.LEGGINGS, c.leggingsDefense.get());
        defense.put(ArmorType.CHESTPLATE, c.chestplateDefense.get());
        defense.put(ArmorType.HELMET, c.helmetDefense.get());
        defense.put(ArmorType.BODY, c.bodyDefense.get());
        return new ArmorMaterial(
            c.armorDurabilityMultiplier.get(),
            defense,
            c.armorEnchantability.get(),
            sound,
            c.toughness.get().floatValue(),
            c.knockbackResistance.get().floatValue(),
            repairItem,
            ResourceKey.create(EquipmentAssets.ROOT_ID,
                ResourceLocation.fromNamespaceAndPath("useful_ores", name))
        );
    }
}

