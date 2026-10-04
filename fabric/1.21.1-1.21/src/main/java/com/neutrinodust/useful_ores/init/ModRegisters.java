package com.neutrinodust.useful_ores.init;

import java.util.List;
import java.util.ArrayList;
import com.neutrinodust.useful_ores.item.SpearItem;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.ArrayList;
import com.neutrinodust.useful_ores.item.SpearItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegisters {
   public static final String MODID = "useful_ores";

   public static final class RegisteredItem<T extends Item> implements Supplier<T> {
      private final T value;
      RegisteredItem(T value) { this.value = value; }
      @Override public T get() { return value; }
      public T asItem() { return value; }
   }

   public static final class RegisteredBlock<T extends Block> implements Supplier<T> {
      private final T value;
      RegisteredBlock(T value) { this.value = value; }
      @Override public T get() { return value; }
   }

   public static RegisteredItem<Item> registerItem(String name, Function<Properties, Item> function, Properties itemProp) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MODID, name);
      Item item = Registry.register(
         BuiltInRegistries.ITEM, id, function.apply(itemProp)
      );
      return new RegisteredItem<>(item);
   }

   
   public static RegisteredItem<Item> registerSpear(String name, Tier tool, Supplier<Properties> itemProps) {
      float[] t = spearTuning(tool.getSpeed());
      return registerItem(name + "_spear",
         p -> new SpearItem(tool, 1.0F, (1.0F / t[0]) - 4.0F, p.stacksTo(1)),
         itemProps.get());
   }


   public static RegisteredBlock<Block> registerBlock(
      String name,
      Function<BlockBehaviour.Properties, Block> function,
      BlockBehaviour.Properties blockProp,
      Properties itemProp
   ) {
      RegisteredBlock<Block> blockReg = registerBlock(name, function, blockProp);
      registerItem(name, p -> new BlockItem(blockReg.get(), p), itemProp);
      return blockReg;
   }

   public static RegisteredBlock<Block> registerBlock(
      String name,
      Function<BlockBehaviour.Properties, Block> function,
      BlockBehaviour.Properties blockProp
   ) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MODID, name);
      Block block = Registry.register(
         BuiltInRegistries.BLOCK, id, function.apply(blockProp)
      );
      return new RegisteredBlock<>(block);
   }

   






   public static ItemAttributeModifiers preserveBaseAttackDamage(ItemAttributeModifiers base, float requestedBaseDamage) {
      int rounded = Math.round(requestedBaseDamage);
      double delta = (double) requestedBaseDamage - rounded;
      if (Math.abs(delta) < 1.0E-6D) return base;

      java.util.List<ItemAttributeModifiers.Entry> entries = new ArrayList<>(base.modifiers());
      for (int i = 0; i < entries.size(); i++) {
         ItemAttributeModifiers.Entry entry = entries.get(i);
         if (Item.BASE_ATTACK_DAMAGE_ID.equals(entry.modifier().id())) {
            AttributeModifier old = entry.modifier();
            AttributeModifier adjusted = new AttributeModifier(
               old.id(), old.amount() + delta, old.operation()
            );
            entries.set(i, new ItemAttributeModifiers.Entry(entry.attribute(), adjusted, entry.slot()));
            return new ItemAttributeModifiers(entries, base.showInTooltip());
         }
      }
      throw new IllegalStateException("Vanilla base attack-damage modifier missing from tool attributes");
   }

   public static ItemAttributeModifiers createSwordAttributes(Tier tool, float baseDamage, float attackSpeed) {
      return preserveBaseAttackDamage(
         SwordItem.createAttributes(tool, Math.round(baseDamage), attackSpeed), baseDamage
      );
   }

   public static ItemAttributeModifiers createDiggerAttributes(Tier tool, float baseDamage, float attackSpeed) {
      return preserveBaseAttackDamage(
         DiggerItem.createAttributes(tool, Math.round(baseDamage), attackSpeed), baseDamage
      );
   }

   public static List<RegisteredItem<Item>> registerItems(
      String name, Tier tool, Holder<ArmorMaterial> armor,
      float[] swordattr, float[] pickaxeattr, float[] axeattr, float[] hoeattr, float[] shovelattr,
      Properties itemProp
   ) {
      return List.of(
         registerItem(name + "_sword",      p -> new SwordItem(tool, p.attributes(createSwordAttributes(tool, swordattr[0], swordattr[1]))), itemProp),
         registerItem(name + "_pickaxe",    p -> new PickaxeItem(tool, p.attributes(createDiggerAttributes(tool, pickaxeattr[0], pickaxeattr[1]))), itemProp),
         registerItem(name + "_axe",        p -> new AxeItem(tool, p.attributes(createDiggerAttributes(tool, axeattr[0], axeattr[1]))), itemProp),
         registerItem(name + "_hoe",        p -> new HoeItem(tool, p.attributes(createDiggerAttributes(tool, hoeattr[0], hoeattr[1]))), itemProp),
         registerItem(name + "_shovel",     p -> new ShovelItem(tool, p.attributes(createDiggerAttributes(tool, shovelattr[0], shovelattr[1]))), itemProp),
         registerItem(name + "_helmet",     p -> new ArmorItem(armor, ArmorItem.Type.HELMET, ModMaterials.armorProperties(name, p, ArmorItem.Type.HELMET)), itemProp),
         registerItem(name + "_chestplate", p -> new ArmorItem(armor, ArmorItem.Type.CHESTPLATE, ModMaterials.armorProperties(name, p, ArmorItem.Type.CHESTPLATE)), itemProp),
         registerItem(name + "_leggings",   p -> new ArmorItem(armor, ArmorItem.Type.LEGGINGS, ModMaterials.armorProperties(name, p, ArmorItem.Type.LEGGINGS)), itemProp),
         registerItem(name + "_boots",      p -> new ArmorItem(armor, ArmorItem.Type.BOOTS, ModMaterials.armorProperties(name, p, ArmorItem.Type.BOOTS)), itemProp)
      );
   }

   public static List<RegisteredItem<Item>> registerAllItems(
      String name, Tier tool, Holder<ArmorMaterial> armor,
      float[] swordattr, float[] pickaxeattr, float[] axeattr, float[] hoeattr, float[] shovelattr,
      Supplier<Properties> itemProps
   ) {
      return List.of(
         registerItem("raw_" + name,        Item::new, itemProps.get()),
         registerItem(name + "_ingot",      Item::new, itemProps.get()),
         registerItem(name + "_nugget",     Item::new, itemProps.get()),
         registerItem(name + "_sword",      p -> new SwordItem(tool, p.attributes(createSwordAttributes(tool, swordattr[0], swordattr[1]))), itemProps.get()),
         registerItem(name + "_pickaxe",    p -> new PickaxeItem(tool, p.attributes(createDiggerAttributes(tool, pickaxeattr[0], pickaxeattr[1]))), itemProps.get()),
         registerItem(name + "_axe",        p -> new AxeItem(tool, p.attributes(createDiggerAttributes(tool, axeattr[0], axeattr[1]))), itemProps.get()),
         registerItem(name + "_hoe",        p -> new HoeItem(tool, p.attributes(createDiggerAttributes(tool, hoeattr[0], hoeattr[1]))), itemProps.get()),
         registerItem(name + "_shovel",     p -> new ShovelItem(tool, p.attributes(createDiggerAttributes(tool, shovelattr[0], shovelattr[1]))), itemProps.get()),
         registerItem(name + "_helmet",     p -> new ArmorItem(armor, ArmorItem.Type.HELMET, ModMaterials.armorProperties(name, p, ArmorItem.Type.HELMET)), itemProps.get()),
         registerItem(name + "_chestplate", p -> new ArmorItem(armor, ArmorItem.Type.CHESTPLATE, ModMaterials.armorProperties(name, p, ArmorItem.Type.CHESTPLATE)), itemProps.get()),
         registerItem(name + "_leggings",   p -> new ArmorItem(armor, ArmorItem.Type.LEGGINGS, ModMaterials.armorProperties(name, p, ArmorItem.Type.LEGGINGS)), itemProps.get()),
         registerItem(name + "_boots",      p -> new ArmorItem(armor, ArmorItem.Type.BOOTS, ModMaterials.armorProperties(name, p, ArmorItem.Type.BOOTS)), itemProps.get()),
         registerSpear(name, tool, itemProps)
      );
   }

   public static List<RegisteredItem<Item>> registerAllItemsWithSwordAbility(
      String name, Tier tool, Holder<ArmorMaterial> armor,
      float[] swordattr, float[] pickaxeattr, float[] axeattr, float[] hoeattr, float[] shovelattr,
      Supplier<Properties> itemProps,
      BiConsumer<LivingEntity, LivingEntity> onHit
   ) {
      return List.of(
         registerItem("raw_" + name,        Item::new, itemProps.get()),
         registerItem(name + "_ingot",      Item::new, itemProps.get()),
         registerItem(name + "_nugget",     Item::new, itemProps.get()),
         registerItem(name + "_sword",      p -> new SwordItem(tool, p.attributes(createSwordAttributes(tool, swordattr[0], swordattr[1]))) {
            @Override
            public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
               
               
               
               super.hurtEnemy(stack, target, attacker);
               return true;
            }

            @Override
            public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
               super.postHurtEnemy(stack, target, attacker);
               onHit.accept(target, attacker);
            }
         }, itemProps.get()),
         registerItem(name + "_pickaxe",    p -> new PickaxeItem(tool, p.attributes(createDiggerAttributes(tool, pickaxeattr[0], pickaxeattr[1]))), itemProps.get()),
         registerItem(name + "_axe",        p -> new AxeItem(tool, p.attributes(createDiggerAttributes(tool, axeattr[0], axeattr[1]))), itemProps.get()),
         registerItem(name + "_hoe",        p -> new HoeItem(tool, p.attributes(createDiggerAttributes(tool, hoeattr[0], hoeattr[1]))), itemProps.get()),
         registerItem(name + "_shovel",     p -> new ShovelItem(tool, p.attributes(createDiggerAttributes(tool, shovelattr[0], shovelattr[1]))), itemProps.get()),
         registerItem(name + "_helmet",     p -> new ArmorItem(armor, ArmorItem.Type.HELMET, ModMaterials.armorProperties(name, p, ArmorItem.Type.HELMET)), itemProps.get()),
         registerItem(name + "_chestplate", p -> new ArmorItem(armor, ArmorItem.Type.CHESTPLATE, ModMaterials.armorProperties(name, p, ArmorItem.Type.CHESTPLATE)), itemProps.get()),
         registerItem(name + "_leggings",   p -> new ArmorItem(armor, ArmorItem.Type.LEGGINGS, ModMaterials.armorProperties(name, p, ArmorItem.Type.LEGGINGS)), itemProps.get()),
         registerItem(name + "_boots",      p -> new ArmorItem(armor, ArmorItem.Type.BOOTS, ModMaterials.armorProperties(name, p, ArmorItem.Type.BOOTS)), itemProps.get()),
         registerSpear(name, tool, itemProps)
      );
   }

   public static List<RegisteredBlock<Block>> registerAllBlocks(
      String name, float[] strengthattr, SoundType soundblock,
      BlockBehaviour.Properties blockProp, Properties itemProp
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

   public static RegisteredBlock<Block> registerDeepslateOre(
      String name, float[] strengthattr,
      BlockBehaviour.Properties blockProp, Properties itemProp
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

