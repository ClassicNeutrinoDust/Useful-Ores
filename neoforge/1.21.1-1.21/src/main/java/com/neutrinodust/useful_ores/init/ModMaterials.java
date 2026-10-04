package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.util.ModTags;
import net.minecraft.core.Holder;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

import java.util.EnumMap;
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

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, "useful_ores");

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CHROMITE_ARMOR = registerArmor("chromite", SoundEvents.ARMOR_EQUIP_IRON, ModTags.Items.CHROMITE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> NYXIUM_ARMOR = registerArmor("nyxium", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.NYXIUM_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PHOSGENE_ARMOR = registerArmor("phosgene", SoundEvents.ARMOR_EQUIP_IRON, ModTags.Items.PHOSGENE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ARCANITE_ARMOR = registerArmor("arcanite", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.ARCANITE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> FULGURITE_ARMOR = registerArmor("fulgurite", SoundEvents.ARMOR_EQUIP_ELYTRA, ModTags.Items.FULGURITE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> OSMIUM_ARMOR = registerArmor("osmium", SoundEvents.ARMOR_EQUIP_DIAMOND, ModTags.Items.OSMIUM_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SOLARITE_ARMOR = registerArmor("solarite", SoundEvents.ARMOR_EQUIP_IRON, ModTags.Items.SOLARITE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SPERRYLITE_ARMOR = registerArmor("sperrylite", SoundEvents.ARMOR_EQUIP_GOLD, ModTags.Items.SPERRYLITE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ARGENTITE_ARMOR = registerArmor("argentite", SoundEvents.ARMOR_EQUIP_GOLD, ModTags.Items.ARGENTITE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ZEPHYRITE_ARMOR = registerArmor("zephyrite", SoundEvents.ARMOR_EQUIP_GENERIC, ModTags.Items.ZEPHYRITE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ILMENITE_ARMOR = registerArmor("ilmenite", SoundEvents.ARMOR_EQUIP_TURTLE, ModTags.Items.ILMENITE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SCHEELITE_ARMOR = registerArmor("scheelite", SoundEvents.ARMOR_EQUIP_IRON, ModTags.Items.SCHEELITE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ENDERIUM_ARMOR = registerArmor("enderium", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.ENDERIUM_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VOIDSHARD_ARMOR = registerArmor("voidshard", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.VOIDSHARD_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SUBSPACE_ARMOR = registerArmor("subspace", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.SUBSPACE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> LONSDALEITE_ARMOR = registerArmor("lonsdaleite", SoundEvents.ARMOR_EQUIP_NETHERITE, ModTags.Items.LONSDALEITE_INGOT);
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PAINITE_ARMOR = registerArmor("painite", SoundEvents.ARMOR_EQUIP_DIAMOND, ModTags.Items.PAINITE_INGOT);

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> registerArmor(String name, Holder<net.minecraft.sounds.SoundEvent> sound, TagKey<Item> repairItem) {
        return ARMOR_MATERIALS.register(name, () -> createArmor(name, sound, repairItem));
    }

    private static ArmorMaterial createArmor(String name, Holder<net.minecraft.sounds.SoundEvent> sound, TagKey<Item> repairItem) {
        ModConfig.MaterialEntries c = ModConfig.ENTRIES.get(name);
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BOOTS, c.bootsDefense.get());
        defense.put(ArmorItem.Type.LEGGINGS, c.leggingsDefense.get());
        defense.put(ArmorItem.Type.CHESTPLATE, c.chestplateDefense.get());
        defense.put(ArmorItem.Type.HELMET, c.helmetDefense.get());
        defense.put(ArmorItem.Type.BODY, c.bodyDefense.get());
        List<ArmorMaterial.Layer> layers = new java.util.ArrayList<>();
        layers.add(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("useful_ores", name)));
        
        
        
        
        if ("painite".equals(name)) {
            layers.add(new ArmorMaterial.Layer(
                    ResourceLocation.fromNamespaceAndPath("useful_ores", "painite_fury_core")
            ));
        }
        return new ArmorMaterial(defense, c.armorEnchantability.get(), sound,
                () -> Ingredient.of(repairItem), layers,
                c.toughness.get().floatValue(), c.knockbackResistance.get().floatValue());
    }

    public static void onConfigLoad() {
        CHROMITE = tool(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, "chromite", ModTags.Items.CHROMITE_INGOT);
        NYXIUM = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "nyxium", ModTags.Items.NYXIUM_INGOT);
        PHOSGENE = tool(BlockTags.INCORRECT_FOR_IRON_TOOL, "phosgene", ModTags.Items.PHOSGENE_INGOT);
        ARCANITE = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "arcanite", ModTags.Items.ARCANITE_INGOT);
        FULGURITE = tool(BlockTags.INCORRECT_FOR_IRON_TOOL, "fulgurite", ModTags.Items.FULGURITE_INGOT);
        OSMIUM = tool(BlockTags.INCORRECT_FOR_IRON_TOOL, "osmium", ModTags.Items.OSMIUM_INGOT);
        SOLARITE = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "solarite", ModTags.Items.SOLARITE_INGOT);
        SPERRYLITE = tool(BlockTags.INCORRECT_FOR_IRON_TOOL, "sperrylite", ModTags.Items.SPERRYLITE_INGOT);
        ARGENTITE = tool(BlockTags.INCORRECT_FOR_IRON_TOOL, "argentite", ModTags.Items.ARGENTITE_INGOT);
        ZEPHYRITE = tool(BlockTags.INCORRECT_FOR_STONE_TOOL, "zephyrite", ModTags.Items.ZEPHYRITE_INGOT);
        ILMENITE = tool(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, "ilmenite", ModTags.Items.ILMENITE_INGOT);
        SCHEELITE = tool(BlockTags.INCORRECT_FOR_IRON_TOOL, "scheelite", ModTags.Items.SCHEELITE_INGOT);
        ENDERIUM = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "enderium", ModTags.Items.ENDERIUM_INGOT);
        VOIDSHARD = tool(ModTags.Blocks.INCORRECT_FOR_VOIDSHARD_TOOL, "voidshard", ModTags.Items.VOIDSHARD_INGOT);
        SUBSPACE = tool(ModTags.Blocks.INCORRECT_FOR_VOIDSHARD_TOOL, "subspace", ModTags.Items.SUBSPACE_INGOT);
        LONSDALEITE = tool(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, "lonsdaleite", ModTags.Items.LONSDALEITE_INGOT);
        PAINITE = tool(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, "painite", ModTags.Items.PAINITE_INGOT);
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

    public static Properties armorProperties(String name, Properties properties, ArmorItem.Type type) {
        ModConfig.MaterialEntries c = ModConfig.ENTRIES.get(name);
        return properties.durability(type.getDurability(c.armorDurabilityMultiplier.get()));
    }

    public static void registerArmorMaterials(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}
