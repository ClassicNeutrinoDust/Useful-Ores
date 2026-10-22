package com.neutrinodust.useful_ores.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
   public interface Blocks {

      private static TagKey<Block> createTag(ResourceLocation name) {
         return TagKey.create(Registries.BLOCK, name);
      }
   }

   public interface Items {
      TagKey<Item> CHROMITE_INGOT = createTag(new ResourceLocation("c:ingots/chromite"));
      TagKey<Item> NYXIUM_INGOT = createTag(new ResourceLocation("c:ingots/nyxium"));
      TagKey<Item> PHOSGENE_INGOT = createTag(new ResourceLocation("c:ingots/phosgene"));
      TagKey<Item> ARCANITE_INGOT = createTag(new ResourceLocation("c:ingots/arcanite"));
      TagKey<Item> FULGURITE_INGOT = createTag(new ResourceLocation("c:ingots/fulgurite"));
      TagKey<Item> OSMIUM_INGOT = createTag(new ResourceLocation("c:ingots/osmium"));
      TagKey<Item> SOLARITE_INGOT = createTag(new ResourceLocation("c:ingots/solarite"));
      TagKey<Item> SPERRYLITE_INGOT = createTag(new ResourceLocation("c:ingots/sperrylite"));
      TagKey<Item> ARGENTITE_INGOT = createTag(new ResourceLocation("c:ingots/argentite"));
      TagKey<Item> ZEPHYRITE_INGOT = createTag(new ResourceLocation("c:ingots/zephyrite"));
      TagKey<Item> ILMENITE_INGOT = createTag(new ResourceLocation("c:ingots/ilmenite"));
      TagKey<Item> ENDERIUM_INGOT = createTag(new ResourceLocation("c:ingots/enderium"));
      TagKey<Item> SCHEELITE_INGOT = createTag(new ResourceLocation("c:ingots/scheelite"));
      TagKey<Item> VOIDSHARD_INGOT = createTag(new ResourceLocation("c:ingots/voidshard"));
      TagKey<Item> SUBSPACE_INGOT = createTag(new ResourceLocation("c:ingots/subspace"));
      TagKey<Item> PAINITE_INGOT = createTag(new ResourceLocation("c:ingots/painite"));
      TagKey<Item> LONSDALEITE_INGOT = createTag(new ResourceLocation("c:ingots/lonsdaleite"));

      private static TagKey<Item> createTag(ResourceLocation name) {
         return TagKey.create(Registries.ITEM, name);
      }
   }
}

