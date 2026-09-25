package com.neutrinodust.useful_ores.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
   public interface Blocks {

      TagKey<Block> INCORRECT_FOR_VOIDSHARD_TOOL = createTag(Identifier.parse("useful_ores:incorrect_for_voidshard_tool"));

      private static TagKey<Block> createTag(Identifier name) {
         return TagKey.create(Registries.BLOCK, name);
      }
   }

   public interface Items {
      TagKey<Item> CHROMITE_INGOT = createTag(Identifier.parse("c:ingots/chromite"));
      TagKey<Item> NYXIUM_INGOT = createTag(Identifier.parse("c:ingots/nyxium"));
      TagKey<Item> PHOSGENE_INGOT = createTag(Identifier.parse("c:ingots/phosgene"));
      TagKey<Item> ARCANITE_INGOT = createTag(Identifier.parse("c:ingots/arcanite"));
      TagKey<Item> FULGURITE_INGOT = createTag(Identifier.parse("c:ingots/fulgurite"));
      TagKey<Item> OSMIUM_INGOT = createTag(Identifier.parse("c:ingots/osmium"));
      TagKey<Item> SOLARITE_INGOT = createTag(Identifier.parse("c:ingots/solarite"));
      TagKey<Item> SPERRYLITE_INGOT = createTag(Identifier.parse("c:ingots/sperrylite"));
      TagKey<Item> ARGENTITE_INGOT = createTag(Identifier.parse("c:ingots/argentite"));
      TagKey<Item> ZEPHYRITE_INGOT = createTag(Identifier.parse("c:ingots/zephyrite"));
      TagKey<Item> ILMENITE_INGOT = createTag(Identifier.parse("c:ingots/ilmenite"));
      TagKey<Item> ENDERIUM_INGOT = createTag(Identifier.parse("c:ingots/enderium"));
      TagKey<Item> SCHEELITE_INGOT = createTag(Identifier.parse("c:ingots/scheelite"));
      TagKey<Item> VOIDSHARD_INGOT = createTag(Identifier.parse("c:ingots/voidshard"));
      TagKey<Item> SUBSPACE_INGOT = createTag(Identifier.parse("c:ingots/subspace"));
      TagKey<Item> PAINITE_INGOT = createTag(Identifier.parse("c:ingots/painite"));
      TagKey<Item> LONSDALEITE_INGOT = createTag(Identifier.parse("c:ingots/lonsdaleite"));

      private static TagKey<Item> createTag(Identifier name) {
         return TagKey.create(Registries.ITEM, name);
      }
   }
}

