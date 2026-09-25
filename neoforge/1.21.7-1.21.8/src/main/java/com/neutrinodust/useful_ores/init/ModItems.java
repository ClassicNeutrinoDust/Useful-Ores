package com.neutrinodust.useful_ores.init;

import java.util.List;
import com.neutrinodust.useful_ores.block.SuperBeaconBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {

   public static final List<DeferredItem<Item>> CHROMITE_ITEMS = ModRegisters.registerAllItems(
      "chromite",
            ModMaterials.CHROMITE,
            ModMaterials.CHROMITE_ARMOR,
            new float[]{7.0F, -3.0F},
            new float[]{6.0F, -2.8F},
            new float[]{8.0F, -3.0F},
            new float[]{0.0F, -2.0F},
            new float[]{5.0F, -3.0F},
            () -> new Properties().rarity(Rarity.RARE)
   );
   public static final List<DeferredBlock<Block>> CHROMITE_BLOCKS = ModRegisters.registerAllBlocks(
      "chromite",
      new float[]{8.0F, 10.0F},
      SoundType.DEEPSLATE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.RARE)
   );
   public static final DeferredBlock<Block> CHISELED_CHROMITE_BLOCK = ModRegisters.registerBlock(
      "chiseled_chromite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().rarity(Rarity.RARE)
   );
   public static final DeferredBlock<Block> DEEPSLATE_CHROMITE_ORE = ModRegisters.registerDeepslateOre(
      "chromite", new float[]{8.0F, 10.0F},
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.RARE)
   );

   public static final List<DeferredItem<Item>> ENDERIUM_ITEMS = ModRegisters.registerAllItems(
      "enderium",
            ModMaterials.ENDERIUM,
            ModMaterials.ENDERIUM_ARMOR,
            new float[]{9.0F, -2.0F},
            new float[]{8.0F, -2.2F},
            new float[]{10.0F, -2.4F},
            new float[]{1.0F, 0.8F},
            new float[]{6.0F, -2.4F},
            () -> new Properties().rarity(Rarity.RARE)
   );
   public static final List<DeferredBlock<Block>> ENDERIUM_BLOCKS = ModRegisters.registerAllBlocks(
      "enderium",
      new float[]{8.0F, 10.0F},
      SoundType.DEEPSLATE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.RARE)
   );
   public static final DeferredBlock<Block> CHISELED_ENDERIUM_BLOCK = ModRegisters.registerBlock(
      "chiseled_enderium_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().rarity(Rarity.RARE)
   );

   public static final List<DeferredItem<Item>> NYXIUM_ITEMS = ModRegisters.registerAllItemsWithSwordAbility(
      "nyxium",
            ModMaterials.NYXIUM,
            ModMaterials.NYXIUM_ARMOR,
            new float[]{6.0F, -2.2F},
            new float[]{5.0F, -2.8F},
            new float[]{7.0F, -2.8F},
            new float[]{0.0F, -2.0F},
            new float[]{4.0F, -3.0F},
            () -> new Properties().fireResistant().rarity(Rarity.RARE),
            (target, attacker) -> target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
               net.minecraft.world.effect.MobEffects.WITHER, 60, 0))
   );
   public static final List<DeferredBlock<Block>> NYXIUM_BLOCKS = ModRegisters.registerAllBlocks(
      "nyxium",
      new float[]{8.0F, 10.0F},
      SoundType.NETHER_ORE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().fireResistant().rarity(Rarity.RARE)
   );
   public static final DeferredBlock<Block> CHISELED_NYXIUM_BLOCK = ModRegisters.registerBlock(
      "chiseled_nyxium_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().fireResistant().rarity(Rarity.RARE)
   );
   public static final DeferredBlock<Block> DEEPSLATE_NYXIUM_ORE = ModRegisters.registerDeepslateOre(
      "nyxium",
      new float[]{8.0F, 10.0F},
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().fireResistant().rarity(Rarity.RARE)
   );

   public static final List<DeferredItem<Item>> PHOSGENE_ITEMS = ModRegisters.registerAllItemsWithSwordAbility(
      "phosgene",
            ModMaterials.PHOSGENE,
            ModMaterials.PHOSGENE_ARMOR,
            new float[]{5.0F, -2.6F},
            new float[]{3.0F, -2.8F},
            new float[]{5.0F, -3.1F},
            new float[]{0.0F, -1.0F},
            new float[]{3.0F, -3.0F},
            () -> new Properties(),
            (target, attacker) -> target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
               net.minecraft.world.effect.MobEffects.POISON, 60, 0))
   );
   public static final List<DeferredBlock<Block>> PHOSGENE_BLOCKS = ModRegisters.registerAllBlocks(
      "phosgene",
      new float[]{4.0F, 6.0F},
      SoundType.STONE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties()
   );
   public static final DeferredBlock<Block> CHISELED_PHOSGENE_BLOCK = ModRegisters.registerBlock(
      "chiseled_phosgene_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties()
   );
   public static final DeferredBlock<Block> DEEPSLATE_PHOSGENE_ORE = ModRegisters.registerDeepslateOre(
      "phosgene", new float[]{4.0F, 6.0F},
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties()
   );

   public static final List<DeferredItem<Item>> ARCANITE_ITEMS = ModRegisters.registerAllItems(
      "arcanite",
            ModMaterials.ARCANITE,
            ModMaterials.ARCANITE_ARMOR,
            new float[]{5.0F, -2.6F},
            new float[]{4.0F, -2.8F},
            new float[]{6.0F, -3.0F},
            new float[]{1.0F, -2.0F},
            new float[]{3.0F, -3.0F},
            () -> new Properties().rarity(Rarity.EPIC)
   );
   public static final List<DeferredBlock<Block>> ARCANITE_BLOCKS = ModRegisters.registerAllBlocks(
      "arcanite",
      new float[]{10.0F, 12.0F},
      SoundType.DEEPSLATE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.EPIC)
   );
   public static final DeferredBlock<Block> CHISELED_ARCANITE_BLOCK = ModRegisters.registerBlock(
      "chiseled_arcanite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().rarity(Rarity.EPIC)
   );
   public static final DeferredBlock<Block> DEEPSLATE_ARCANITE_ORE = ModRegisters.registerDeepslateOre(
      "arcanite", new float[]{10.0F, 12.0F},
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.EPIC)
   );

   public static final List<DeferredItem<Item>> FULGURITE_ITEMS = ModRegisters.registerAllItemsWithSwordAbility(
      "fulgurite",
            ModMaterials.FULGURITE,
            ModMaterials.FULGURITE_ARMOR,
            new float[]{7.0F, -3.4F},
            new float[]{6.0F, -3.0F},
            new float[]{8.0F, -3.6F},
            new float[]{1.0F, -2.0F},
            new float[]{5.0F, -3.2F},
            () -> new Properties().fireResistant().rarity(Rarity.UNCOMMON),
            (target, attacker) -> attacker.addEffect(new net.minecraft.world.effect.MobEffectInstance(
               net.minecraft.world.effect.MobEffects.SPEED, 60, 0))
   );

   public static final List<DeferredBlock<Block>> FULGURITE_BLOCKS = ModRegisters.registerAllBlocks(
      "fulgurite",
      new float[]{8.0F, 10.0F},
      SoundType.DEEPSLATE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().fireResistant().rarity(Rarity.UNCOMMON)
   );
   public static final DeferredBlock<Block> CHISELED_FULGURITE_BLOCK = ModRegisters.registerBlock(
      "chiseled_fulgurite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().fireResistant().rarity(Rarity.UNCOMMON)
   );
   public static final DeferredBlock<Block> DEEPSLATE_FULGURITE_ORE = ModRegisters.registerDeepslateOre(
      "fulgurite", new float[]{8.0F, 10.0F},
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().fireResistant().rarity(Rarity.UNCOMMON)
   );

   public static final DeferredBlock<Block> FULGURITE_ELECTRIC_TRAP = ModRegisters.registerBlock(
      "fulgurite_electric_trap",
      com.neutrinodust.useful_ores.block.FulguriteElectricTrapBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops().strength(6.0F, 1200.0F).sound(SoundType.METAL).noOcclusion().lightLevel(state ->
            state.getValue(com.neutrinodust.useful_ores.block.FulguriteElectricTrapBlock.ACTIVE) ? 10 : 0),
      new Properties().fireResistant().rarity(Rarity.UNCOMMON)
   );

   public static final List<DeferredItem<Item>> OSMIUM_ITEMS = ModRegisters.registerAllItemsWithSwordAbility(
      "osmium",
            ModMaterials.OSMIUM,
            ModMaterials.OSMIUM_ARMOR,
            new float[]{5.0F, -2.4F},
            new float[]{4.0F, -2.8F},
            new float[]{6.0F, -3.0F},
            new float[]{0.0F, -2.0F},
            new float[]{3.0F, -3.0F},
            () -> new Properties().rarity(Rarity.RARE),
            (target, attacker) -> target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
               net.minecraft.world.effect.MobEffects.SLOWNESS, 60, 0))
   );
   public static final List<DeferredBlock<Block>> OSMIUM_BLOCKS = ModRegisters.registerAllBlocks(
      "osmium",
      new float[]{8.0F, 10.0F},
      SoundType.DEEPSLATE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.RARE)
   );
   public static final DeferredBlock<Block> CHISELED_OSMIUM_BLOCK = ModRegisters.registerBlock(
      "chiseled_osmium_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().rarity(Rarity.RARE)
   );
   public static final DeferredBlock<Block> DEEPSLATE_OSMIUM_ORE = ModRegisters.registerDeepslateOre(
      "osmium", new float[]{8.0F, 10.0F},
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.RARE)
   );

   public static final List<DeferredItem<Item>> SOLARITE_ITEMS = ModRegisters.registerAllItems(
      "solarite",
            ModMaterials.SOLARITE,
            ModMaterials.SOLARITE_ARMOR,
            new float[]{5.0F, -2.2F},
            new float[]{4.0F, -2.6F},
            new float[]{6.0F, -2.8F},
            new float[]{0.0F, 0.5F},
            new float[]{3.0F, -2.8F},
            () -> new Properties().rarity(Rarity.RARE)
   );
   public static final List<DeferredBlock<Block>> SOLARITE_BLOCKS = ModRegisters.registerAllBlocks(
      "solarite",
      new float[]{6.0F, 8.0F},
      SoundType.STONE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.RARE)
   );
   public static final DeferredBlock<Block> CHISELED_SOLARITE_BLOCK = ModRegisters.registerBlock(
      "chiseled_solarite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().rarity(Rarity.RARE)
   );

   public static final List<DeferredItem<Item>> SPERRYLITE_ITEMS = ModRegisters.registerAllItemsWithSwordAbility(
      "sperrylite",
            ModMaterials.SPERRYLITE,
            ModMaterials.SPERRYLITE_ARMOR,
            new float[]{6.0F, -2.4F},
            new float[]{1.0F, -2.8F},
            new float[]{4.0F, -3.0F},
            new float[]{0.0F, -3.0F},
            new float[]{1.0F, -3.0F},
            () -> new Properties().rarity(Rarity.UNCOMMON),
            (target, attacker) -> target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
               net.minecraft.world.effect.MobEffects.WEAKNESS, 60, 0))
   );
   public static final List<DeferredBlock<Block>> SPERRYLITE_BLOCKS = ModRegisters.registerAllBlocks(
      "sperrylite",
      new float[]{6.0F, 8.0F},
      SoundType.STONE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.UNCOMMON)
   );
   public static final DeferredBlock<Block> CHISELED_SPERRYLITE_BLOCK = ModRegisters.registerBlock(
      "chiseled_sperrylite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().rarity(Rarity.UNCOMMON)
   );
   public static final DeferredBlock<Block> DEEPSLATE_SPERRYLITE_ORE = ModRegisters.registerDeepslateOre(
      "sperrylite", new float[]{6.0F, 8.0F},
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.UNCOMMON)
   );

   public static final List<DeferredItem<Item>> ARGENTITE_ITEMS = ModRegisters.registerAllItems(
      "argentite",
            ModMaterials.ARGENTITE,
            ModMaterials.ARGENTITE_ARMOR,
            new float[]{4.0F, -1.8F},
            new float[]{3.0F, -2.8F},
            new float[]{5.0F, -3.1F},
            new float[]{0.0F, -1.0F},
            new float[]{3.0F, -3.0F},
            () -> new Properties().rarity(Rarity.UNCOMMON)
   );
   public static final List<DeferredBlock<Block>> ARGENTITE_BLOCKS = ModRegisters.registerAllBlocks(
      "argentite",
      new float[]{4.0F, 6.0F},
      SoundType.STONE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.UNCOMMON)
   );
   public static final DeferredBlock<Block> CHISELED_ARGENTITE_BLOCK = ModRegisters.registerBlock(
      "chiseled_argentite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().rarity(Rarity.UNCOMMON)
   );
   public static final DeferredBlock<Block> DEEPSLATE_ARGENTITE_ORE = ModRegisters.registerDeepslateOre(
      "argentite", new float[]{4.0F, 6.0F},
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.UNCOMMON)
   );

   public static final List<DeferredItem<Item>> ZEPHYRITE_ITEMS = ModRegisters.registerAllItems(
      "zephyrite",
            ModMaterials.ZEPHYRITE,
            ModMaterials.ZEPHYRITE_ARMOR,
            new float[]{3.0F, -2.4F},
            new float[]{2.0F, -2.8F},
            new float[]{5.0F, -3.2F},
            new float[]{0.0F, -2.0F},
            new float[]{2.0F, -3.0F},
            () -> new Properties()
   );
   public static final List<DeferredBlock<Block>> ZEPHYRITE_BLOCKS = ModRegisters.registerAllBlocks(
      "zephyrite",
      new float[]{4.0F, 6.0F},
      SoundType.STONE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties()
   );
   public static final DeferredBlock<Block> CHISELED_ZEPHYRITE_BLOCK = ModRegisters.registerBlock(
      "chiseled_zephyrite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties()
   );
   public static final DeferredBlock<Block> DEEPSLATE_ZEPHYRITE_ORE = ModRegisters.registerDeepslateOre(
      "zephyrite", new float[]{4.0F, 6.0F},
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties()
   );

   public static final List<DeferredItem<Item>> ILMENITE_ITEMS = ModRegisters.registerAllItems(
      "ilmenite",
            ModMaterials.ILMENITE,
            ModMaterials.ILMENITE_ARMOR,
            new float[]{6.0F, -2.6F},
            new float[]{5.0F, -2.8F},
            new float[]{7.0F, -3.0F},
            new float[]{1.0F, -2.0F},
            new float[]{4.0F, -3.0F},
            () -> new Properties().rarity(Rarity.EPIC)
   );
   public static final List<DeferredBlock<Block>> ILMENITE_BLOCKS = ModRegisters.registerAllBlocks(
      "ilmenite",
      new float[]{10.0F, 14.0F},
      SoundType.DEEPSLATE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.EPIC)
   );
   public static final DeferredBlock<Block> CHISELED_ILMENITE_BLOCK = ModRegisters.registerBlock(
      "chiseled_ilmenite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().rarity(Rarity.EPIC)
   );
   public static final DeferredBlock<Block> DEEPSLATE_ILMENITE_ORE = ModRegisters.registerDeepslateOre(
      "ilmenite", new float[]{10.0F, 14.0F},
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.EPIC)
   );

   public static final DeferredItem<Item> TITANIUM_LOCK = ModRegisters.registerItem(
      "titanium_lock",
      com.neutrinodust.useful_ores.item.TitaniumLockItem::new,
      new Properties().rarity(Rarity.RARE)
   );

   public static final DeferredItem<Item> TITANIUM_KEY = ModRegisters.registerItem(
      "titanium_key",
      com.neutrinodust.useful_ores.item.TitaniumKeyItem::new,
      new Properties().rarity(Rarity.RARE)
   );

   public static final List<DeferredItem<Item>> SCHEELITE_ITEMS = ModRegisters.registerAllItemsWithSwordAbility(
      "scheelite",
            ModMaterials.SCHEELITE,
            ModMaterials.SCHEELITE_ARMOR,
            new float[]{4.0F, -2.4F},
            new float[]{3.0F, -2.8F},
            new float[]{5.0F, -3.1F},
            new float[]{0.0F, -1.0F},
            new float[]{3.0F, -3.0F},
            () -> new Properties().rarity(Rarity.UNCOMMON),
            (target, attacker) -> target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
               net.minecraft.world.effect.MobEffects.SLOWNESS, 40, 1))
   );
   public static final List<DeferredBlock<Block>> SCHEELITE_BLOCKS = ModRegisters.registerAllBlocks(
      "scheelite",
      new float[]{4.0F, 6.0F},
      SoundType.STONE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.UNCOMMON)
   );
   public static final DeferredBlock<Block> CHISELED_SCHEELITE_BLOCK = ModRegisters.registerBlock(
      "chiseled_scheelite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().rarity(Rarity.UNCOMMON)
   );
   public static final DeferredBlock<Block> DEEPSLATE_SCHEELITE_ORE = ModRegisters.registerDeepslateOre(
      "scheelite", new float[]{4.0F, 6.0F},
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.UNCOMMON)
   );

   public static final DeferredItem<Item> SCHEELITE_CHISEL = ModRegisters.registerItem(
      "scheelite_chisel",
      com.neutrinodust.useful_ores.item.ScheeliteChiselItem::new,
      new Properties().durability(200).rarity(Rarity.UNCOMMON)
   );

   public static final List<DeferredItem<Item>> VOIDSHARD_ITEMS = ModRegisters.registerAllItems(
      "voidshard",
            ModMaterials.VOIDSHARD,
            ModMaterials.VOIDSHARD_ARMOR,
            new float[]{11.0F, -1.8F},
            new float[]{9.0F, -2.0F},
            new float[]{13.0F, -2.2F},
            new float[]{2.0F, 1.0F},
            new float[]{8.0F, -2.2F},
            () -> new Properties().rarity(Rarity.EPIC)
   );
   public static final List<DeferredBlock<Block>> VOIDSHARD_BLOCKS = ModRegisters.registerAllBlocks(
      "voidshard",
      new float[]{9.0F, 12.0F},
      SoundType.DEEPSLATE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.EPIC)
   );
   public static final DeferredBlock<Block> CHISELED_VOIDSHARD_BLOCK = ModRegisters.registerBlock(
      "chiseled_voidshard_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().rarity(Rarity.EPIC)
   );

   public static final List<DeferredItem<Item>> LONSDALEITE_ITEMS = ModRegisters.registerAllItems(
      "lonsdaleite",
            ModMaterials.LONSDALEITE,
            ModMaterials.LONSDALEITE_ARMOR,
            new float[]{11.0F, -1.8F},
            new float[]{9.0F, -2.0F},
            new float[]{13.0F, -2.2F},
            new float[]{2.0F, 1.0F},
            new float[]{8.0F, -2.2F},
            () -> new Properties().rarity(Rarity.EPIC)
   );
   public static final List<DeferredBlock<Block>> LONSDALEITE_BLOCKS = ModRegisters.registerAllBlocks(
      "lonsdaleite",
      new float[]{12.0F, 18.0F},
      SoundType.DEEPSLATE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().rarity(Rarity.EPIC)
   );
   public static final DeferredBlock<Block> CHISELED_LONSDALEITE_BLOCK = ModRegisters.registerBlock(
      "chiseled_lonsdaleite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().rarity(Rarity.EPIC)
   );
   public static final List<DeferredItem<Item>> PAINITE_ITEMS = ModRegisters.registerAllItems(
      "painite",
            ModMaterials.PAINITE,
            ModMaterials.PAINITE_ARMOR,
            new float[]{3.0F, -2.4F},
            new float[]{1.0F, -2.8F},
            new float[]{5.0F, -3.0F},
            new float[]{-3.0F, 0.0F},
            new float[]{1.5F, -3.0F},
            () -> new Properties().fireResistant().rarity(Rarity.RARE)
   );
   public static final List<DeferredBlock<Block>> PAINITE_BLOCKS = ModRegisters.registerAllBlocks(
      "painite", new float[]{3.0F, 3.0F}, SoundType.NETHER_ORE,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(),
      new Properties().fireResistant().rarity(Rarity.RARE)
   );
   public static final DeferredBlock<Block> CHISELED_PAINITE_BLOCK = ModRegisters.registerBlock(
      "chiseled_painite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL),
      new Properties().fireResistant().rarity(Rarity.RARE)
   );



   public static final DeferredBlock<Block> METEORITE_BLOCK = ModRegisters.registerBlock(
      "meteorite_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops().strength(9.0F, 14.0F).sound(SoundType.NETHERITE_BLOCK),
      new Properties()
   );
   public static final DeferredBlock<Block> METEORITE_CORE = ModRegisters.registerBlock(
      "meteorite_core", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops().strength(10.0F, 16.0F).sound(SoundType.NETHERITE_BLOCK)
         .lightLevel(state -> 3),
      new Properties()
   );
   public static final DeferredBlock<Block> SCORCHED_STONE = ModRegisters.registerBlock(
      "scorched_stone", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops().strength(2.5F, 6.0F).sound(SoundType.BASALT),
      new Properties()
   );
   public static final DeferredBlock<Block> SCORCHED_DIRT = ModRegisters.registerBlock(
      "scorched_dirt", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.6F, 3.0F).sound(SoundType.BASALT),
      new Properties()
   );
   public static final DeferredBlock<Block> METEORITE_ASH_BLOCK = ModRegisters.registerBlock(
      "meteorite_ash_block", Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.5F, 3.0F).sound(SoundType.SAND),
      new Properties()
   );

   public static final DeferredItem<Item> HEATED_SHARD = ModRegisters.registerItem(
      "heated_shard", Item::new, new Properties().rarity(Rarity.EPIC).fireResistant()
   );

   public static final DeferredItem<Item> DARK_CORE = ModRegisters.registerItem(
      "dark_core", Item::new, new Properties().rarity(Rarity.EPIC).fireResistant()
   );

   public static final DeferredBlock<Block> NYXIUM_DARK_BARRIER = ModRegisters.registerBlock(
      "nyxium_dark_barrier",
      com.neutrinodust.useful_ores.barrier.NyxiumDarkBarrierBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops()
         .strength(50.0F, 1200.0F)
         .sound(SoundType.AMETHYST_CLUSTER)
         .noOcclusion()
         .noCollission()
         .lightLevel(state -> 8),
      new Properties().rarity(Rarity.EPIC).fireResistant()
   );

   public static final DeferredItem<Item> NYXIUM_ARCANITE_CORE = ModRegisters.registerItem(
      "nyxium_arcanite_core", Item::new, new Properties().rarity(Rarity.RARE)
   );

   public static final DeferredItem<Item> VOLATILE_NYXIUMNITE_CORE = ModRegisters.registerItem(
      "volatile_nyxiumnite_core", p -> new Item(p) {
         @Override
         public boolean isFoil(net.minecraft.world.item.ItemStack stack) {
            return true;
         }
      }, new Properties().rarity(Rarity.EPIC)
   );

   public static final DeferredItem<Item> NYXIUMNITE_STAFF = ModRegisters.registerItem(
      "nyxiumnite_staff",
      com.neutrinodust.useful_ores.item.NyxiumniteStaffItem::new,
      new Properties().rarity(Rarity.EPIC).stacksTo(1)
   );

   public static final DeferredBlock<Block> ANCIENT_PEDESTAL = ModRegisters.registerBlock(
      "ancient_pedestal",
      com.neutrinodust.useful_ores.pedestal.AncientPedestalBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops()
         .strength(35.0F, 900.0F)
         .sound(SoundType.NETHERITE_BLOCK)
         .noOcclusion(),
      new Properties().rarity(Rarity.EPIC).fireResistant()
   );

   public static final DeferredBlock<Block> DARK_WORMHOLE_PORTAL = ModRegisters.registerBlock(
      "dark_wormhole_portal",
      com.neutrinodust.useful_ores.pedestal.DarkWormholePortalBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .noCollission()
         .noOcclusion()
         .noLootTable()
         .strength(-1.0F, 3600000.0F)
         .sound(SoundType.AMETHYST_CLUSTER)
         .lightLevel(state -> 10)
   );

   public static final DeferredItem<Item> SUBSPACE_INGOT = ModRegisters.registerItem(
      "subspace_ingot", Item::new, new Properties().rarity(Rarity.EPIC)
   );
   public static final DeferredItem<Item> SUBSPACE_NUGGET = ModRegisters.registerItem(
      "subspace_nugget", Item::new, new Properties().rarity(Rarity.EPIC)
   );

   public static final DeferredItem<Item> FARSEEKER_CRYSTAL = ModRegisters.registerItem(
      "farseeker_crystal", Item::new, new Properties().rarity(Rarity.EPIC)
   );
   public static final List<DeferredItem<Item>> SUBSPACE_ITEMS = ModRegisters.registerItems(
      "subspace",
      ModMaterials.SUBSPACE,
      ModMaterials.SUBSPACE_ARMOR,
      new float[]{15.0F, -1.4F},
      new float[]{13.0F, -1.6F},
      new float[]{17.0F, -1.8F},
      new float[]{2.0F, 1.2F},
      new float[]{11.0F, -1.8F},
      new Properties().rarity(Rarity.EPIC)
   );

   
   public static final DeferredItem<Item> SUBSPACE_SPEAR = ModRegisters.registerSpear(
      "subspace", ModMaterials.SUBSPACE, () -> new Properties().rarity(Rarity.EPIC).fireResistant()
   );

   public static final DeferredBlock<Block> SUBSPACE_BLOCK = ModRegisters.registerBlock(
      "subspace_block",
      Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops()
         .strength(8.0F, 12.0F)
         .sound(SoundType.METAL),
      new Properties().rarity(Rarity.EPIC)
   );

   public static final DeferredBlock<Block> EXTRUDING_CRYSTAL_ORE = ModRegisters.registerBlock(
      "extruding_crystal_ore",
      Block::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops()
         .strength(3.0F, 5.0F)
         .sound(SoundType.AMETHYST),
      new Properties().rarity(Rarity.RARE)
   );

   public static final DeferredItem<Item> EXTRUDING_CRYSTAL = ModRegisters.registerItem(
      "extruding_crystal", Item::new, new Properties().rarity(Rarity.RARE)
   );

   public static final DeferredItem<Item> EXTRUDING_CRYSTAL_NUGGET = ModRegisters.registerItem(
      "extruding_crystal_nugget", Item::new, new Properties().rarity(Rarity.RARE)
   );

   public static final DeferredItem<Item> EXTRUDING_NETHER_STAR = ModRegisters.registerItem(
      "extruding_nether_star", Item::new,
      new Properties().rarity(Rarity.EPIC).fireResistant()
   );

   public static final DeferredBlock<Block> SUPER_BEACON = ModRegisters.registerBlock(
      "super_beacon",
      SuperBeaconBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops()
         .strength(3.0F, 6.0F)
         .sound(SoundType.GLASS)
         .lightLevel(state -> 15),
      new Properties().rarity(Rarity.EPIC)
   );

   public static final DeferredBlock<Block> ARGENTITE_FILTER = ModRegisters.registerBlock(
      "argentite_filter",
      com.neutrinodust.useful_ores.filter.ArgentiteFilterBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops()
         .strength(3.0F)
         .sound(SoundType.METAL),
      new Properties()
   );

   public static final DeferredBlock<Block> WIRELESS_REDSTONE_RELAY = ModRegisters.registerBlock(
      "wireless_redstone_relay",
      com.neutrinodust.useful_ores.block.WirelessRedstoneRelayBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.4F)
         .sound(SoundType.STONE)
         .noCollission()
         .isRedstoneConductor((state, level, pos) -> false),
      new Properties()
   );

   public static final DeferredBlock<Block> REDSTONE_CLOCK = ModRegisters.registerBlock(
      "redstone_clock",
      com.neutrinodust.useful_ores.block.RedstoneClockBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.4F)
         .sound(SoundType.STONE)
         .noCollission()
         .isRedstoneConductor((state, level, pos) -> false),
      new Properties()
   );

   public static final DeferredBlock<Block> AND_GATE = ModRegisters.registerBlock(
      "and_gate",
      com.neutrinodust.useful_ores.block.AndGateBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.4F)
         .sound(SoundType.STONE)
         .noCollission()
         .isRedstoneConductor((state, level, pos) -> false),
      new Properties()
   );

   public static final DeferredBlock<Block> NAND_GATE = ModRegisters.registerBlock(
      "nand_gate",
      com.neutrinodust.useful_ores.block.NandGateBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.4F)
         .sound(SoundType.STONE)
         .noCollission()
         .isRedstoneConductor((state, level, pos) -> false),
      new Properties()
   );

   public static final DeferredBlock<Block> OR_GATE = ModRegisters.registerBlock(
      "or_gate",
      com.neutrinodust.useful_ores.block.OrGateBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.4F)
         .sound(SoundType.STONE)
         .noCollission()
         .isRedstoneConductor((state, level, pos) -> false),
      new Properties()
   );

   public static final DeferredBlock<Block> NOR_GATE = ModRegisters.registerBlock(
      "nor_gate",
      com.neutrinodust.useful_ores.block.NorGateBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.4F)
         .sound(SoundType.STONE)
         .noCollission()
         .isRedstoneConductor((state, level, pos) -> false),
      new Properties()
   );

   public static final DeferredBlock<Block> XOR_GATE = ModRegisters.registerBlock(
      "xor_gate",
      com.neutrinodust.useful_ores.block.XorGateBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.4F)
         .sound(SoundType.STONE)
         .noCollission()
         .isRedstoneConductor((state, level, pos) -> false),
      new Properties()
   );

   public static final DeferredBlock<Block> NOT_GATE = ModRegisters.registerBlock(
      "not_gate",
      com.neutrinodust.useful_ores.block.NotGateBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.4F)
         .sound(SoundType.STONE)
         .noCollission()
         .isRedstoneConductor((state, level, pos) -> false),
      new Properties()
   );

   public static final DeferredBlock<Block> CHROMITE_PISTON = ModRegisters.registerBlock(
      "chromite_piston",
      com.neutrinodust.useful_ores.block.ChromitePistonBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(1.5F)
         .sound(SoundType.STONE)

         .dynamicShape()

         .noOcclusion(),
      new Properties()
   );

   public static final DeferredBlock<Block> CHROMITE_STICKY_PISTON = ModRegisters.registerBlock(
      "chromite_sticky_piston",
      com.neutrinodust.useful_ores.block.ChromiteStickyPistonBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(1.5F)
         .sound(SoundType.STONE)
         .dynamicShape()
         .noOcclusion(),
      new Properties()
   );

   public static final DeferredBlock<Block> CHROMITE_PISTON_HEAD = ModRegisters.registerBlock(
      "chromite_piston_head",
      com.neutrinodust.useful_ores.block.ChromitePistonHeadBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(1.5F)
         .sound(SoundType.STONE)
         .dynamicShape()

         .noOcclusion()
   );

   public static final DeferredItem<Item> METEORITE_CORE_SHARD = ModRegisters.registerItem(
      "meteorite_core_shard", Item::new, new Properties().rarity(Rarity.UNCOMMON)
   );

   public static final DeferredItem<Item> VOLATILE_METEORITE_CORE = ModRegisters.registerItem(
      "volatile_meteorite_core", p -> new Item(p) {
         @Override
         public boolean isFoil(net.minecraft.world.item.ItemStack stack) {
            return true;
         }
      }, new Properties().rarity(Rarity.RARE)
   );

   public static final DeferredItem<Item> METEOR_STAFF = ModRegisters.registerItem(
      "meteor_staff",
      com.neutrinodust.useful_ores.item.MeteorStaffItem::new,
      new Properties().rarity(Rarity.EPIC).durability(250)
   );

   public static final DeferredItem<Item> METEOR_DUST = ModRegisters.registerItem(
      "meteor_dust", Item::new, new Properties()
   );

   public static final DeferredItem<Item> METEOR_BOMB = ModRegisters.registerItem(
      "meteor_bomb",
      com.neutrinodust.useful_ores.item.MeteorBombItem::new,
      new Properties().stacksTo(16)
   );

   public static final DeferredItem<Item> LONSDALEITE_LAYER_GLUE = ModRegisters.registerItem(
      "lonsdaleite_layer_glue",
      com.neutrinodust.useful_ores.item.LonsdaleiteGlueItem::new,
      new Properties().durability(64)
   );

   public static final DeferredItem<Item> PHOSGENE_RAW_POWDER = ModRegisters.registerItem(
      "phosgene_raw_powder", Item::new, new Properties().rarity(Rarity.UNCOMMON)
   );

   public static final DeferredItem<Item> PHOSGENE_POTION_POWDER = ModRegisters.registerItem(
      "phosgene_potion_powder",
      com.neutrinodust.useful_ores.phosgene.PhosgenePotionPowderItem::new,
      new Properties().rarity(Rarity.RARE)
   );

   public static final DeferredBlock<Block> SOLARITE_FURNACE_COMPONENT = ModRegisters.registerBlock(
      "solarite_furnace_component",
      com.neutrinodust.useful_ores.solar.SolariteFurnaceComponentBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops()
         .strength(5.0F, 8.0F)
         .sound(SoundType.METAL),
      new Properties().rarity(Rarity.RARE)
   );

   public static final DeferredBlock<Block> SOLARITE_FURNACE_BLOCK = ModRegisters.registerBlock(
      "solarite_furnace",
      com.neutrinodust.useful_ores.solar.SolariteFurnaceBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops()
         .strength(5.0F, 8.0F)
         .sound(SoundType.METAL)

         .lightLevel(state -> state.getValue(com.neutrinodust.useful_ores.solar.SolariteFurnaceBlock.LIT) ? 13 : 0)
   );

   public static final DeferredBlock<Block> SOLARITE_FURNACE_PART = ModRegisters.registerBlock(
      "solarite_furnace_part",
      com.neutrinodust.useful_ores.solar.SolariteFurnacePartBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .requiresCorrectToolForDrops()
         .strength(5.0F, 8.0F)
         .sound(SoundType.METAL)
   );

   public static final DeferredBlock<Block> SOLAR_BATTERY_BLOCK = ModRegisters.registerBlock(
      "solar_battery",
      com.neutrinodust.useful_ores.solar.SolarBatteryBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.5F)
         .sound(SoundType.METAL)
         .noLootTable()
   );

   public static final DeferredItem<Item> SOLAR_BATTERY = ModRegisters.registerItem(
      "solar_battery",
      p -> new com.neutrinodust.useful_ores.solar.SolarBatteryItem((Block) SOLAR_BATTERY_BLOCK.get(), p),

      new Properties().stacksTo(16).rarity(Rarity.RARE)
   );

   public static final DeferredItem<Item> SCHEELITE_ARROW = ModRegisters.registerItem(
      "scheelite_arrow",
      com.neutrinodust.useful_ores.item.ScheeliteArrowItem::new,
      new Properties().stacksTo(64)
   );

   public static final DeferredItem<Item> ARCANITE_ARROW = ModRegisters.registerItem(
      "arcanite_arrow",
      com.neutrinodust.useful_ores.item.ArcaniteArrowItem::new,
      new Properties().stacksTo(64)
   );

   public static final DeferredBlock<Block> ARCANITE_XP_JAR_BLOCK = ModRegisters.registerBlock(
      "arcanite_xp_jar",
      com.neutrinodust.useful_ores.xpjar.ArcaniteXpJarBlock::new,
      net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
         .strength(0.3F)
         .sound(SoundType.GLASS)
         .noLootTable()
         .noOcclusion()
   );

   public static final DeferredItem<Item> ARCANITE_XP_JAR = ModRegisters.registerItem(
      "arcanite_xp_jar",
      p -> new com.neutrinodust.useful_ores.xpjar.ArcaniteXpJarItem(
               (Block) ARCANITE_XP_JAR_BLOCK.get(), p),
      new Properties().stacksTo(1).rarity(Rarity.EPIC)
   );

   public static final DeferredItem<Item> SILVER_CORE_ARCANITE_SHARD = ModRegisters.registerItem(
      "silver_core_arcanite_shard",
      Item::new,
      new Properties().rarity(Rarity.RARE)
   );

   public static final DeferredItem<Item> NYXIUM_ARROW = ModRegisters.registerItem(
      "nyxium_arrow",
      com.neutrinodust.useful_ores.item.NyxiumArrowItem::new,
      new Properties().stacksTo(64)
   );

   public static final DeferredItem<Item> SOLARITE_BATTERY_MINECART = ModRegisters.registerItem(
      "solarite_battery_minecart",
      com.neutrinodust.useful_ores.item.SolariteBatteryMinecartItem::new,
      new Properties().stacksTo(1)
   );

   public static final DeferredItem<Item> SPERRYLITE_CATALYTIC_VIAL = ModRegisters.registerItem(
      "sperrylite_catalytic_vial",
      com.neutrinodust.useful_ores.item.SperryliteCatalyticVialItem::new,
      new Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
         .component(
            net.minecraft.core.component.DataComponents.CONSUMABLE,
            net.minecraft.world.item.component.Consumable.builder()
               .consumeSeconds(1.6F)
               .animation(net.minecraft.world.item.ItemUseAnimation.DRINK)
               .sound(net.minecraft.sounds.SoundEvents.GENERIC_DRINK)
               .build()
         )
   );

   public static final DeferredItem<Item> SPERRYLITE_CATALYTIC_VIAL_SPLASH = ModRegisters.registerItem(
      "sperrylite_catalytic_vial_splash",
      com.neutrinodust.useful_ores.item.SperryliteCatalyticVialSplashItem::new,
      new Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
   );

   public static final DeferredItem<Item> SPERRYLITE_CATALYTIC_VIAL_LINGERING = ModRegisters.registerItem(
      "sperrylite_catalytic_vial_lingering",
      com.neutrinodust.useful_ores.item.SperryliteCatalyticVialLingeringItem::new,
      new Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
   );

   public static final DeferredItem<Item> ELECTRIC_RING = ModRegisters.registerItem(
      "electric_ring", Item::new, new Properties()
   );

   public static final DeferredItem<Item> USEFUL_ORES_COMPENDIUM = ModRegisters.registerItem(
      "useful_ores_compendium",
      com.neutrinodust.useful_ores.compendium.CompendiumBookItem::new,
      new Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
   );

   public static void init(IEventBus bus) {
      ModRegisters.ITEMS.register(bus);
      ModRegisters.BLOCKS.register(bus);
   }
}

