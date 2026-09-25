package com.neutrinodust.useful_ores.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
   public interface Items {
      TagKey<Item> CHROMITE_INGOT = createTag(ResourceLocation.parse("c:ingots/chromite"));
      TagKey<Item> NYXIUM_INGOT = createTag(ResourceLocation.parse("c:ingots/nyxium"));
      TagKey<Item> PHOSGENE_INGOT = createTag(ResourceLocation.parse("c:ingots/phosgene"));
      TagKey<Item> ARCANITE_INGOT = createTag(ResourceLocation.parse("c:ingots/arcanite"));
      TagKey<Item> FULGURITE_INGOT = createTag(ResourceLocation.parse("c:ingots/fulgurite"));
      TagKey<Item> OSMIUM_INGOT = createTag(ResourceLocation.parse("c:ingots/osmium"));
      TagKey<Item> SOLARITE_INGOT = createTag(ResourceLocation.parse("c:ingots/solarite"));
      TagKey<Item> SPERRYLITE_INGOT = createTag(ResourceLocation.parse("c:ingots/sperrylite"));
      TagKey<Item> ARGENTITE_INGOT = createTag(ResourceLocation.parse("c:ingots/argentite"));
      TagKey<Item> ZEPHYRITE_INGOT = createTag(ResourceLocation.parse("c:ingots/zephyrite"));
      TagKey<Item> ILMENITE_INGOT = createTag(ResourceLocation.parse("c:ingots/ilmenite"));
      TagKey<Item> ENDERIUM_INGOT = createTag(ResourceLocation.parse("c:ingots/enderium"));
      TagKey<Item> SCHEELITE_INGOT = createTag(ResourceLocation.parse("c:ingots/scheelite"));
      TagKey<Item> VOIDSHARD_INGOT = createTag(ResourceLocation.parse("c:ingots/voidshard"));
      TagKey<Item> SUBSPACE_INGOT = createTag(ResourceLocation.parse("c:ingots/subspace"));
      TagKey<Item> PAINITE_INGOT = createTag(ResourceLocation.parse("c:ingots/painite"));
      TagKey<Item> LONSDALEITE_INGOT = createTag(ResourceLocation.parse("c:ingots/lonsdaleite"));

      private static TagKey<Item> createTag(ResourceLocation name) {
         return TagKey.create(Registries.ITEM, name);
      }
   }
}

