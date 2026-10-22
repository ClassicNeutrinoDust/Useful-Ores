package com.neutrinodust.useful_ores.init;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRegisters {

   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "useful_ores");
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "useful_ores");

   public static RegistryObject<Item> registerItem(String name, Function<Properties, Item> function, Properties itemProp) {
      return ITEMS.register(
         name, () -> function.apply(itemProp)
      );
   }

   public static RegistryObject<Item> registerSpear(String name, Tier tool, Supplier<Properties> itemProps) {

      return registerItem(name + "_spear", p -> {
         float[] t = spearTuning(tool.getSpeed());
         return new com.neutrinodust.useful_ores.item.BackportSpearItem(
            tool, t[0], t[1], t[2], t[3], t[4], t[5], t[6], t[7], t[8], p.stacksTo(1)
         );
      }, itemProps.get());
   }

   public static RegistryObject<Block> registerBlock(
      String name,
      Function<net.minecraft.world.level.block.state.BlockBehaviour.Properties, Block> function,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties blockProp,
      Properties itemProp
   ) {
      RegistryObject<Block> blockReg = registerBlock(name, function, blockProp);
      registerItem(name, p -> new BlockItem((Block) blockReg.get(), p), itemProp);
      return blockReg;
   }

   public static RegistryObject<Block> registerBlock(
      String name,
      Function<net.minecraft.world.level.block.state.BlockBehaviour.Properties, Block> function,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties blockProp
   ) {
      return BLOCKS.register(
         name, () -> function.apply(blockProp)
      );
   }

   public static List<RegistryObject<Item>> registerItems(
      String name,
      Tier tool,
      ArmorMaterial armor,
      float[] swordattr,
      float[] pickaxeattr,
      float[] axeattr,
      float[] hoeattr,
      float[] shovelattr,
      Properties itemProp
   ) {
      return List.of(
         registerItem(name + "_sword",      p -> new FractionalToolItems.Sword(tool, swordattr[0], swordattr[1], p), itemProp),
         registerItem(name + "_pickaxe",    p -> new FractionalToolItems.Pickaxe(tool, pickaxeattr[0], pickaxeattr[1], p), itemProp),
         registerItem(name + "_axe",        p -> new FractionalToolItems.Axe(tool, axeattr[0], axeattr[1], p), itemProp),
         registerItem(name + "_hoe",        p -> new FractionalToolItems.Hoe(tool, hoeattr[0], hoeattr[1], p), itemProp),
         registerItem(name + "_shovel",     p -> new FractionalToolItems.Shovel(tool, shovelattr[0], shovelattr[1], p), itemProp),
         registerItem(name + "_helmet",     p -> new ArmorItem(armor, ArmorItem.Type.HELMET, ModMaterials.armorProperties(name, p, EquipmentSlot.HEAD)), itemProp),
         registerItem(name + "_chestplate", p -> new ArmorItem(armor, ArmorItem.Type.CHESTPLATE, ModMaterials.armorProperties(name, p, EquipmentSlot.CHEST)), itemProp),
         registerItem(name + "_leggings",   p -> new ArmorItem(armor, ArmorItem.Type.LEGGINGS, ModMaterials.armorProperties(name, p, EquipmentSlot.LEGS)), itemProp),
         registerItem(name + "_boots",      p -> new ArmorItem(armor, ArmorItem.Type.BOOTS, ModMaterials.armorProperties(name, p, EquipmentSlot.FEET)), itemProp)
      );
   }

   public static List<RegistryObject<Item>> registerAllItems(
      String name,
      Tier tool,
      ArmorMaterial armor,
      float[] swordattr,
      float[] pickaxeattr,
      float[] axeattr,
      float[] hoeattr,
      float[] shovelattr,
      Supplier<Properties> itemProps
   ) {
      return List.of(
         registerItem("raw_" + name,        Item::new, itemProps.get()),
         registerItem(name + "_ingot",      Item::new, itemProps.get()),
         registerItem(name + "_nugget",     Item::new, itemProps.get()),
         registerItem(name + "_sword",      p -> new FractionalToolItems.Sword(tool, swordattr[0], swordattr[1], p), itemProps.get()),
         registerItem(name + "_pickaxe",    p -> new FractionalToolItems.Pickaxe(tool, pickaxeattr[0], pickaxeattr[1], p), itemProps.get()),
         registerItem(name + "_axe",        p -> new FractionalToolItems.Axe(tool, axeattr[0], axeattr[1], p), itemProps.get()),
         registerItem(name + "_hoe",        p -> new FractionalToolItems.Hoe(tool, hoeattr[0], hoeattr[1], p), itemProps.get()),
         registerItem(name + "_shovel",     p -> new FractionalToolItems.Shovel(tool, shovelattr[0], shovelattr[1], p), itemProps.get()),
         registerItem(name + "_helmet",     p -> armorItem(name, armor, EquipmentSlot.HEAD, p), itemProps.get()),
         registerItem(name + "_chestplate", p -> armorItem(name, armor, EquipmentSlot.CHEST, p), itemProps.get()),
         registerItem(name + "_leggings",   p -> armorItem(name, armor, EquipmentSlot.LEGS, p), itemProps.get()),
         registerItem(name + "_boots",      p -> armorItem(name, armor, EquipmentSlot.FEET, p), itemProps.get()),
         registerSpear(name, tool, itemProps)
      );
   }

   public static List<RegistryObject<Item>> registerAllItemsWithSwordAbility(
      String name,
      Tier tool,
      ArmorMaterial armor,
      float[] swordattr,
      float[] pickaxeattr,
      float[] axeattr,
      float[] hoeattr,
      float[] shovelattr,
      Supplier<Properties> itemProps,
      java.util.function.BiConsumer<net.minecraft.world.entity.LivingEntity, net.minecraft.world.entity.LivingEntity> onHit
   ) {
      return List.of(
         registerItem("raw_" + name,        Item::new, itemProps.get()),
         registerItem(name + "_ingot",      Item::new, itemProps.get()),
         registerItem(name + "_nugget",     Item::new, itemProps.get()),
         registerItem(name + "_sword",      p -> new FractionalToolItems.Sword(tool, swordattr[0], swordattr[1], p, onHit), itemProps.get()),
         registerItem(name + "_pickaxe",    p -> new FractionalToolItems.Pickaxe(tool, pickaxeattr[0], pickaxeattr[1], p), itemProps.get()),
         registerItem(name + "_axe",        p -> new FractionalToolItems.Axe(tool, axeattr[0], axeattr[1], p), itemProps.get()),
         registerItem(name + "_hoe",        p -> new FractionalToolItems.Hoe(tool, hoeattr[0], hoeattr[1], p), itemProps.get()),
         registerItem(name + "_shovel",     p -> new FractionalToolItems.Shovel(tool, shovelattr[0], shovelattr[1], p), itemProps.get()),
         registerItem(name + "_helmet",     p -> armorItem(name, armor, EquipmentSlot.HEAD, p), itemProps.get()),
         registerItem(name + "_chestplate", p -> armorItem(name, armor, EquipmentSlot.CHEST, p), itemProps.get()),
         registerItem(name + "_leggings",   p -> armorItem(name, armor, EquipmentSlot.LEGS, p), itemProps.get()),
         registerItem(name + "_boots",      p -> armorItem(name, armor, EquipmentSlot.FEET, p), itemProps.get()),
         registerSpear(name, tool, itemProps)
      );
   }

   private static ArmorItem.Type armorType(EquipmentSlot slot) {
      return switch (slot) { case HEAD -> ArmorItem.Type.HELMET; case CHEST -> ArmorItem.Type.CHESTPLATE; case LEGS -> ArmorItem.Type.LEGGINGS; case FEET -> ArmorItem.Type.BOOTS; default -> ArmorItem.Type.BOOTS; };
   }

   private static Item armorItem(String name, ArmorMaterial armor, EquipmentSlot type, Properties properties) {
      if ("painite".equals(name)) {
         return new com.neutrinodust.useful_ores.item.PainiteArmorItem(
            armor, armorType(type), ModMaterials.armorProperties(name, properties, type)
         );
      }
      return new ArmorItem(armor, armorType(type), ModMaterials.armorProperties(name, properties, type));
   }

   public static List<RegistryObject<Block>> registerAllBlocks(
      String name,
      float[] strengthattr,
      SoundType soundblock,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties blockProp,
      Properties itemProp
   ) {
      return List.of(
         registerBlock(name + "_block",     Block::new,
            blockProp.requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL), itemProp),
         registerBlock(name + "_ore",       Block::new,
            blockProp.requiresCorrectToolForDrops().strength(strengthattr[0], strengthattr[1]).sound(soundblock), itemProp),
         registerBlock("raw_" + name + "_block", Block::new,
            blockProp.requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.STONE), itemProp)
      );
   }

   public static RegistryObject<Block> registerDeepslateOre(
      String name,
      float[] strengthattr,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties blockProp,
      Properties itemProp
   ) {
      return registerBlock("deepslate_" + name + "_ore", Block::new,
         blockProp.requiresCorrectToolForDrops()
            .strength(strengthattr[0] + 1.5F, strengthattr[1])
            .sound(SoundType.DEEPSLATE),
         itemProp);
   }
   private static float[] spearTuning(float speed) {
      if (speed <= 2.5F) return new float[]{0.65F,0.70F,0.75F,5.0F,14.0F,10.0F,5.1F,15.0F,4.6F};
      if (speed <= 4.5F) return new float[]{0.75F,0.82F,0.70F,4.5F,13.0F,9.0F,5.1F,13.75F,4.6F};
      if (speed <= 6.5F) return new float[]{0.95F,0.95F,0.60F,2.5F,11.0F,6.75F,5.1F,11.25F,4.6F};
      if (speed <= 9.0F) return new float[]{1.05F,1.075F,0.50F,3.0F,10.0F,6.5F,5.1F,10.0F,4.6F};
      return new float[]{1.15F,1.20F,0.40F,2.5F,9.0F,5.5F,5.1F,8.75F,4.6F};
   }

}

