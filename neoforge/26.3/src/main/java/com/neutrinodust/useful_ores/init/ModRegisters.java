package com.neutrinodust.useful_ores.init;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ModRegisters {
   public static final Items ITEMS = DeferredRegister.createItems("useful_ores");
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("useful_ores");

   public static DeferredItem<Item> registerItem(String name, Function<Properties, Item> function, Properties itemProp) {
      return ITEMS.register(
         name, () -> function.apply(itemProp.setId(ResourceKey.create(Registries.ITEM,
               Identifier.fromNamespaceAndPath("useful_ores", name))))
      );
   }

   public static DeferredItem<Item> registerSpear(String name, ToolMaterial tool, Supplier<Properties> itemProps) {
      float[] t = spearTuning(tool.speed());
      return registerItem(name + "_spear", p -> new Item(p.spear(tool, t[0], t[1], t[2], t[3], t[4], t[5], t[6], t[7], t[8])), itemProps.get());
   }


   public static DeferredBlock<Block> registerBlock(
      String name,
      Function<net.minecraft.world.level.block.state.BlockBehaviour.Properties, Block> function,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties blockProp,
      Properties itemProp
   ) {
      DeferredBlock<Block> blockReg = registerBlock(name, function, blockProp);
      registerItem(name, p -> new BlockItem((Block) blockReg.get(), p), itemProp.useBlockDescriptionPrefix());
      return blockReg;
   }

   public static DeferredBlock<Block> registerBlock(
      String name,
      Function<net.minecraft.world.level.block.state.BlockBehaviour.Properties, Block> function,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties blockProp
   ) {
      return BLOCKS.register(
         name, () -> function.apply(blockProp.setId(ResourceKey.create(Registries.BLOCK,
               Identifier.fromNamespaceAndPath("useful_ores", name))))
      );
   }

   public static List<DeferredItem<Item>> registerItems(
      String name,
      ToolMaterial tool,
      ArmorMaterial armor,
      float[] swordattr,
      float[] pickaxeattr,
      float[] axeattr,
      float[] hoeattr,
      float[] shovelattr,
      Properties itemProp
   ) {
      return List.of(
         registerItem(name + "_sword",      p -> new Item(p.sword(tool, swordattr[0], swordattr[1])), itemProp),
         registerItem(name + "_pickaxe",    p -> new Item(p.pickaxe(tool, pickaxeattr[0], pickaxeattr[1])), itemProp),
         registerItem(name + "_axe",        p -> new Item(p.axe(tool, axeattr[0], axeattr[1])), itemProp),
         registerItem(name + "_hoe",        p -> new Item(p.hoe(tool, hoeattr[0], hoeattr[1])), itemProp),
         registerItem(name + "_shovel",     p -> new Item(p.shovel(tool, shovelattr[0], shovelattr[1])), itemProp),
         registerItem(name + "_helmet",     p -> new Item(p.humanoidArmor(armor, ArmorType.HELMET)), itemProp),
         registerItem(name + "_chestplate", p -> new Item(p.humanoidArmor(armor, ArmorType.CHESTPLATE)), itemProp),
         registerItem(name + "_leggings",   p -> new Item(p.humanoidArmor(armor, ArmorType.LEGGINGS)), itemProp),
         registerItem(name + "_boots",      p -> new Item(p.humanoidArmor(armor, ArmorType.BOOTS)), itemProp)
      );
   }

   public static List<DeferredItem<Item>> registerAllItems(
      String name,
      ToolMaterial tool,
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
         registerItem(name + "_sword",      p -> new Item(p.sword(tool, swordattr[0], swordattr[1])), itemProps.get()),
         registerItem(name + "_pickaxe",    p -> new Item(p.pickaxe(tool, pickaxeattr[0], pickaxeattr[1])), itemProps.get()),
         registerItem(name + "_axe",        p -> new Item(p.axe(tool, axeattr[0], axeattr[1])), itemProps.get()),
         registerItem(name + "_hoe",        p -> new Item(p.hoe(tool, hoeattr[0], hoeattr[1])), itemProps.get()),
         registerItem(name + "_shovel",     p -> new Item(p.shovel(tool, shovelattr[0], shovelattr[1])), itemProps.get()),
         registerItem(name + "_helmet",     p -> new Item(p.humanoidArmor(armor, ArmorType.HELMET)), itemProps.get()),
         registerItem(name + "_chestplate", p -> new Item(p.humanoidArmor(armor, ArmorType.CHESTPLATE)), itemProps.get()),
         registerItem(name + "_leggings",   p -> new Item(p.humanoidArmor(armor, ArmorType.LEGGINGS)), itemProps.get()),
         registerItem(name + "_boots",      p -> new Item(p.humanoidArmor(armor, ArmorType.BOOTS)), itemProps.get()),
         registerSpear(name, tool, itemProps)
      );
   }

   public static List<DeferredItem<Item>> registerAllItemsWithSwordAbility(
      String name,
      ToolMaterial tool,
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
         registerItem(name + "_sword",      p -> new Item(p.sword(tool, swordattr[0], swordattr[1])) {
            @Override
            public void postHurtEnemy(net.minecraft.world.item.ItemStack stack,
                                       net.minecraft.world.entity.LivingEntity target,
                                       net.minecraft.world.entity.LivingEntity attacker) {
               super.postHurtEnemy(stack, target, attacker);
               onHit.accept(target, attacker);
            }
         }, itemProps.get()),
         registerItem(name + "_pickaxe",    p -> new Item(p.pickaxe(tool, pickaxeattr[0], pickaxeattr[1])), itemProps.get()),
         registerItem(name + "_axe",        p -> new Item(p.axe(tool, axeattr[0], axeattr[1])), itemProps.get()),
         registerItem(name + "_hoe",        p -> new Item(p.hoe(tool, hoeattr[0], hoeattr[1])), itemProps.get()),
         registerItem(name + "_shovel",     p -> new Item(p.shovel(tool, shovelattr[0], shovelattr[1])), itemProps.get()),
         registerItem(name + "_helmet",     p -> new Item(p.humanoidArmor(armor, ArmorType.HELMET)), itemProps.get()),
         registerItem(name + "_chestplate", p -> new Item(p.humanoidArmor(armor, ArmorType.CHESTPLATE)), itemProps.get()),
         registerItem(name + "_leggings",   p -> new Item(p.humanoidArmor(armor, ArmorType.LEGGINGS)), itemProps.get()),
         registerItem(name + "_boots",      p -> new Item(p.humanoidArmor(armor, ArmorType.BOOTS)), itemProps.get()),
         registerSpear(name, tool, itemProps)
      );
   }

   public static List<DeferredBlock<Block>> registerAllBlocks(
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

   public static DeferredBlock<Block> registerDeepslateOre(
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

