package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.util.ModTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.eventbus.api.IEventBus;
import java.util.List;
import java.util.Map;

public final class ModMaterials {
 public static Tier CHROMITE, NYXIUM, PHOSGENE, ARCANITE, FULGURITE, OSMIUM, SOLARITE, SPERRYLITE, ARGENTITE, ZEPHYRITE, ILMENITE, SCHEELITE, ENDERIUM, VOIDSHARD, SUBSPACE, LONSDALEITE, PAINITE;
 public static final ArmorMaterial CHROMITE_ARMOR=armor("chromite",SoundEvents.ARMOR_EQUIP_IRON,ModTags.Items.CHROMITE_INGOT);
 public static final ArmorMaterial NYXIUM_ARMOR=armor("nyxium",SoundEvents.ARMOR_EQUIP_NETHERITE,ModTags.Items.NYXIUM_INGOT);
 public static final ArmorMaterial PHOSGENE_ARMOR=armor("phosgene",SoundEvents.ARMOR_EQUIP_IRON,ModTags.Items.PHOSGENE_INGOT);
 public static final ArmorMaterial ARCANITE_ARMOR=armor("arcanite",SoundEvents.ARMOR_EQUIP_NETHERITE,ModTags.Items.ARCANITE_INGOT);
 public static final ArmorMaterial FULGURITE_ARMOR=armor("fulgurite",SoundEvents.ARMOR_EQUIP_IRON,ModTags.Items.FULGURITE_INGOT);
 public static final ArmorMaterial OSMIUM_ARMOR=armor("osmium",SoundEvents.ARMOR_EQUIP_DIAMOND,ModTags.Items.OSMIUM_INGOT);
 public static final ArmorMaterial SOLARITE_ARMOR=armor("solarite",SoundEvents.ARMOR_EQUIP_IRON,ModTags.Items.SOLARITE_INGOT);
 public static final ArmorMaterial SPERRYLITE_ARMOR=armor("sperrylite",SoundEvents.ARMOR_EQUIP_GOLD,ModTags.Items.SPERRYLITE_INGOT);
 public static final ArmorMaterial ARGENTITE_ARMOR=armor("argentite",SoundEvents.ARMOR_EQUIP_GOLD,ModTags.Items.ARGENTITE_INGOT);
 public static final ArmorMaterial ZEPHYRITE_ARMOR=armor("zephyrite",SoundEvents.ARMOR_EQUIP_GENERIC,ModTags.Items.ZEPHYRITE_INGOT);
 public static final ArmorMaterial ILMENITE_ARMOR=armor("ilmenite",SoundEvents.ARMOR_EQUIP_TURTLE,ModTags.Items.ILMENITE_INGOT);
 public static final ArmorMaterial SCHEELITE_ARMOR=armor("scheelite",SoundEvents.ARMOR_EQUIP_IRON,ModTags.Items.SCHEELITE_INGOT);
 public static final ArmorMaterial ENDERIUM_ARMOR=armor("enderium",SoundEvents.ARMOR_EQUIP_NETHERITE,ModTags.Items.ENDERIUM_INGOT);
 public static final ArmorMaterial VOIDSHARD_ARMOR=armor("voidshard",SoundEvents.ARMOR_EQUIP_NETHERITE,ModTags.Items.VOIDSHARD_INGOT);
 public static final ArmorMaterial SUBSPACE_ARMOR=armor("subspace",SoundEvents.ARMOR_EQUIP_NETHERITE,ModTags.Items.SUBSPACE_INGOT);
 public static final ArmorMaterial LONSDALEITE_ARMOR=armor("lonsdaleite",SoundEvents.ARMOR_EQUIP_NETHERITE,ModTags.Items.LONSDALEITE_INGOT);
 public static final ArmorMaterial PAINITE_ARMOR=armor("painite",SoundEvents.ARMOR_EQUIP_DIAMOND,ModTags.Items.PAINITE_INGOT);
 private ModMaterials() {}
 
 private static int safeInt(ModConfig.IntValue value, String material, int fallback) {
  try { return value == null ? fallback : value.get(); }
  catch (RuntimeException ex) { return fallback; }
 }
 
 private static double safeDouble(ModConfig.DoubleValue value, String material, double fallback) {
  try { return value == null ? fallback : value.get(); }
  catch (RuntimeException ex) { return fallback; }
 }
 private static ArmorMaterial armor(String name, SoundEvent sound, TagKey<Item> repairTag) {
  return new ArmorMaterial() {
   private ModConfig.MaterialEntries config(){ return ModConfig.ENTRIES.get(name); }
   @Override public int getDurabilityForType(Type slot){ int mult=switch(slot){case BOOTS->13;case LEGGINGS->15;case CHESTPLATE->16;case HELMET->11;default->0;}; return mult * safeInt(config().armorDurabilityMultiplier, name, 5); }
   @Override public int getDefenseForType(Type slot){ return switch(slot){case BOOTS->safeInt(config().bootsDefense,name,9);case LEGGINGS->safeInt(config().leggingsDefense,name,8);case CHESTPLATE->safeInt(config().chestplateDefense,name,7);case HELMET->safeInt(config().helmetDefense,name,6);default->safeInt(config().bodyDefense,name,10);}; }
   @Override public int getEnchantmentValue(){return safeInt(config().armorEnchantability,name,11);}
   @Override public SoundEvent getEquipSound(){return sound;}
   @Override public Ingredient getRepairIngredient(){return Ingredient.of(repairTag);}
   @Override public String getName(){return "useful_ores:"+name;}
   @Override public float getToughness(){return (float)safeDouble(config().toughness,name,12);}
   @Override public float getKnockbackResistance(){return (float)safeDouble(config().knockbackResistance,name,13);}
  };
 }
 public static void onConfigLoad(){
  CHROMITE=tool(BlockTags.NEEDS_DIAMOND_TOOL,"chromite",ModTags.Items.CHROMITE_INGOT); NYXIUM=tool(BlockTags.NEEDS_DIAMOND_TOOL,"nyxium",ModTags.Items.NYXIUM_INGOT);
  PHOSGENE=tool(BlockTags.NEEDS_IRON_TOOL,"phosgene",ModTags.Items.PHOSGENE_INGOT); ARCANITE=tool(BlockTags.NEEDS_DIAMOND_TOOL,"arcanite",ModTags.Items.ARCANITE_INGOT);
  FULGURITE=tool(BlockTags.NEEDS_IRON_TOOL,"fulgurite",ModTags.Items.FULGURITE_INGOT); OSMIUM=tool(BlockTags.NEEDS_IRON_TOOL,"osmium",ModTags.Items.OSMIUM_INGOT);
  SOLARITE=tool(BlockTags.NEEDS_DIAMOND_TOOL,"solarite",ModTags.Items.SOLARITE_INGOT); SPERRYLITE=tool(BlockTags.NEEDS_IRON_TOOL,"sperrylite",ModTags.Items.SPERRYLITE_INGOT);
  ARGENTITE=tool(BlockTags.NEEDS_IRON_TOOL,"argentite",ModTags.Items.ARGENTITE_INGOT); ZEPHYRITE=tool(BlockTags.NEEDS_STONE_TOOL,"zephyrite",ModTags.Items.ZEPHYRITE_INGOT);
  ILMENITE=tool(BlockTags.NEEDS_DIAMOND_TOOL,"ilmenite",ModTags.Items.ILMENITE_INGOT); SCHEELITE=tool(BlockTags.NEEDS_IRON_TOOL,"scheelite",ModTags.Items.SCHEELITE_INGOT);
  ENDERIUM=tool(BlockTags.NEEDS_DIAMOND_TOOL,"enderium",ModTags.Items.ENDERIUM_INGOT); VOIDSHARD=tool(BlockTags.NEEDS_DIAMOND_TOOL,"voidshard",ModTags.Items.VOIDSHARD_INGOT);
  SUBSPACE=tool(BlockTags.NEEDS_DIAMOND_TOOL,"subspace",ModTags.Items.SUBSPACE_INGOT); LONSDALEITE=tool(BlockTags.NEEDS_DIAMOND_TOOL,"lonsdaleite",ModTags.Items.LONSDALEITE_INGOT);
  PAINITE=tool(BlockTags.NEEDS_DIAMOND_TOOL,"painite",ModTags.Items.PAINITE_INGOT);
 }
 private static Tier tool(TagKey<Block> correctBlocks,String name,TagKey<Item> repairItem){
  Tier tier = new Tier(){private ModConfig.MaterialEntries c(){return ModConfig.ENTRIES.get(name);}
   @Override public int getUses(){

    int fallback = switch (name) {
     case "chromite" -> 1650; case "nyxium" -> 2200; case "phosgene" -> 650;
     case "arcanite" -> 2150; case "fulgurite" -> 1750; case "osmium" -> 1900;
     case "solarite" -> 2400; case "sperrylite" -> 700; case "argentite" -> 380;
     case "zephyrite" -> 190; case "ilmenite" -> 2600; case "scheelite" -> 900;
     case "enderium" -> 2700; case "voidshard" -> 3000; case "subspace" -> 6400;
     case "lonsdaleite" -> 6200; case "painite" -> 1561; default -> 1561;
    };
    ModConfig.MaterialEntries entries = c();
    int configured = safeInt(entries == null ? null : entries.toolDurability,name,fallback);

    return configured < 64 ? fallback : configured;
   }
   @Override public int getLevel(){return switch(name){case "voidshard","subspace"->5;case "nyxium","arcanite","solarite","enderium","lonsdaleite"->4;case "chromite","ilmenite","painite"->3;case "zephyrite"->1;default->2;};}
   @Override public float getSpeed(){

    double configured = safeDouble(c() == null ? null : c().miningSpeed, name,
        switch (name) {
         case "zephyrite","argentite" -> 5.0;
         case "phosgene","sperrylite","scheelite" -> 6.5;
         case "osmium" -> 6.0;
         case "chromite" -> 7.0;
         case "fulgurite" -> 7.5;
         case "ilmenite" -> 7.0;
         case "nyxium" -> 9.0;
         case "arcanite" -> 9.5;
         case "solarite" -> 15.0;
         case "enderium" -> 20.0;
         case "voidshard","lonsdaleite" -> 25.0;
         case "subspace" -> 32.0;
         case "painite" -> 8.0;
         default -> 3.0;
        });
    double floor = switch (name) {
     case "zephyrite","argentite" -> 5.0;
     case "phosgene","sperrylite","scheelite" -> 6.5;
     case "osmium" -> 6.0;
     case "chromite" -> 7.0;
     case "fulgurite" -> 7.5;
     case "ilmenite" -> 7.0;
     case "nyxium" -> 9.0;
     case "arcanite" -> 9.5;
     case "solarite" -> 15.0;
     case "enderium" -> 20.0;
     case "voidshard","lonsdaleite" -> 25.0;
     case "subspace" -> 32.0;
     case "painite" -> 8.0;
     default -> 3.0;
    };
    return (float)Math.max(configured, floor);
   }
   @Override public float getAttackDamageBonus(){return (float)safeDouble(c().attackDamage,name,2);}
   @Override public int getEnchantmentValue(){return safeInt(c().toolEnchantability,name,4);}
   @Override public Ingredient getRepairIngredient(){return Ingredient.of(repairItem);}
   @Override public TagKey<Block> getTag(){return correctBlocks;}
  };
  return tier;
 }
 public static Properties armorProperties(String name,Properties properties,EquipmentSlot type){ return properties; }
 
 public static void registerArmorMaterials(IEventBus eventBus) {}
}
