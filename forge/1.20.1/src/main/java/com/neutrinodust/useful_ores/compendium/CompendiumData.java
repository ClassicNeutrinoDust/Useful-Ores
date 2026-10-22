package com.neutrinodust.useful_ores.compendium;

import java.util.ArrayList;
import java.util.List;

public class CompendiumData {

   public static class Page {
      public final String title;
      public final List<String> paragraphs;
      public final String textFile;
      public final List<String> itemIds;
      public String imageId;
      public int imageWidth = 0;
      public int imageHeight = 0;
      public List<GridEntry> gridEntries;
      public Recipe recipe;
      public List<Recipe> recipes = List.of();
      public boolean showItemNames = true;

      private Page(String title, List<String> paragraphs, String textFile, List<String> itemIds) {
         this.title = title;
         this.paragraphs = paragraphs;
         this.textFile = textFile;
         this.itemIds = itemIds;
      }

      public static Page of(String title, String... paragraphs) {
         return new Page(title, List.of(paragraphs), null, List.of());
      }

      public static Page withItems(String title, List<String> itemIds, String... paragraphs) {
         return new Page(title, List.of(paragraphs), null, itemIds);
      }

      public static Page fromFile(String title, String textFile, String... itemIds) {
         return new Page(title, null, textFile, List.of(itemIds));
      }

      public static Page grid(String title, List<GridEntry> entries) {
         Page page = new Page(title, List.of(), null, List.of());
         page.gridEntries = entries;
         return page;
      }

      public Page withImage(String imageId, int width, int height) {
         this.imageId = imageId;
         this.imageWidth = width;
         this.imageHeight = height;
         return this;
      }

      public Page withRecipe(Recipe recipe) {
         this.recipe = recipe;
         this.recipes = List.of(recipe);
         return this;
      }

      public Page withRecipes(List<Recipe> recipes) {
         this.recipes = List.copyOf(recipes);
         this.recipe = this.recipes.isEmpty() ? null : this.recipes.get(0);
         return this;
      }

      public Page hideItemNames() {
         this.showItemNames = false;
         return this;
      }
   }

   public static class GridEntry {
      public final String itemId;
      public final List<String> itemIds;
      public final String label;
      public final String targetChapter;
      public final String targetPage;

      private GridEntry(List<String> itemIds, String label, String targetChapter, String targetPage) {
         this.itemIds = List.copyOf(itemIds);
         this.itemId = itemIds.isEmpty() ? "" : itemIds.get(0);
         this.label = label;
         this.targetChapter = targetChapter;
         this.targetPage = targetPage;
      }

      public static GridEntry of(String itemId, String targetChapter, String targetPage) {
         return new GridEntry(List.of(itemId), null, targetChapter, targetPage);
      }

      public static GridEntry of(String itemId, String label, String targetChapter, String targetPage) {
         return new GridEntry(List.of(itemId), label, targetChapter, targetPage);
      }

      public static GridEntry ofMany(List<String> itemIds, String label, String targetChapter, String targetPage) {
         return new GridEntry(itemIds, label, targetChapter, targetPage);
      }
   }

   public static class Recipe {
      public enum Type { CRAFTING, SMELTING, BREWING }

      public final Type type;
      public final String[] grid;
      public final String smeltingInput;
      public final String[] brewingBottles;
      public final String brewingIngredient;
      public final String outputItemId;
      public final int outputCount;

      private Recipe(Type type, String[] grid, String smeltingInput, String[] brewingBottles,
                      String brewingIngredient, String outputItemId, int outputCount) {
         this.type = type;
         this.grid = grid;
         this.smeltingInput = smeltingInput;
         this.brewingBottles = brewingBottles;
         this.brewingIngredient = brewingIngredient;
         this.outputItemId = outputItemId;
         this.outputCount = outputCount;
      }

      public static Recipe crafting(String[] grid, String outputItemId, int outputCount) {
         if (grid.length != 9) {
            throw new IllegalArgumentException("Recipe.crafting grid must have exactly 9 slots, got " + grid.length);
         }
         return new Recipe(Type.CRAFTING, grid, null, null, null, outputItemId, outputCount);
      }

      public static Recipe shapeless(List<String> ingredients, String outputItemId, int outputCount) {
         String[] slots = new String[9];
         for (int i = 0; i < Math.min(9, ingredients.size()); i++) {
            slots[i] = ingredients.get(i);
         }
         return new Recipe(Type.CRAFTING, slots, null, null, null, outputItemId, outputCount);
      }

      public static Recipe smelting(String inputItemId, String outputItemId, int outputCount) {
         return new Recipe(Type.SMELTING, null, inputItemId, null, null, outputItemId, outputCount);
      }

      public static Recipe brewing(List<String> bottles, String ingredientItemId, String outputItemId, int outputCount) {
         if (bottles.isEmpty() || bottles.size() > 3) {
            throw new IllegalArgumentException("Recipe.brewing needs 1-3 bottle slots, got " + bottles.size());
         }
         String[] slots = new String[3];
         for (int i = 0; i < bottles.size(); i++) {
            slots[i] = bottles.get(i);
         }
         return new Recipe(Type.BREWING, null, null, slots, ingredientItemId, outputItemId, outputCount);
      }
   }

   public static class Chapter {
      public final String name;
      public final String iconItemId;
      public final List<Page> pages;

      public Chapter(String name, String iconItemId, List<Page> pages) {
         this.name = name;
         this.iconItemId = iconItemId;
         this.pages = pages;
      }
   }

   private static Chapter category(String name, String iconItemId, List<Page> detailPages) {
      List<GridEntry> entries = new ArrayList<>();
      for (Page detail : detailPages) {
         if (detail.itemIds.size() > 1) {
            entries.add(GridEntry.ofMany(detail.itemIds, detail.title, name, detail.title));
         } else {
            String icon = !detail.itemIds.isEmpty() ? detail.itemIds.get(0) : iconItemId;
            entries.add(GridEntry.of(icon, detail.title, name, detail.title));
         }
      }
      List<Page> pages = new ArrayList<>();
      pages.add(Page.grid(name, entries));
      pages.addAll(detailPages);
      return new Chapter(name, iconItemId, pages);
   }

   private static final Chapter ORES_AND_MATERIALS = category("Ores & Materials", "useful_ores:chromite_ingot", List.of(

      Page.fromFile(
            "Chromite",
            "chromite",
            "useful_ores:chromite_ore", "useful_ores:chromite_ingot", "useful_ores:chromite_block"
         )
         .withImage("useful_ores:textures/gui/compendium/chromite_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_chromite", "useful_ores:chromite_ingot", 1)),

      Page.fromFile(
            "Enderium",
            "enderium",
            "useful_ores:enderium_ore", "useful_ores:enderium_ingot", "useful_ores:enderium_block"
         )
         .withImage("useful_ores:textures/gui/compendium/enderium_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_enderium", "useful_ores:enderium_ingot", 1)),

      Page.fromFile(
            "Nyxium",
            "nyxium",
            "useful_ores:nyxium_ore", "useful_ores:nyxium_ingot", "useful_ores:nyxium_block"
         )
         .withImage("useful_ores:textures/gui/compendium/nyxium_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_nyxium", "useful_ores:nyxium_ingot", 1)),

      Page.fromFile(
            "Phosgene",
            "phosgene",
            "useful_ores:phosgene_ore", "useful_ores:phosgene_ingot", "useful_ores:phosgene_block"
         )
         .withImage("useful_ores:textures/gui/compendium/phosgene_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_phosgene", "useful_ores:phosgene_ingot", 1)),

      Page.fromFile(
            "Arcanite",
            "arcanite",
            "useful_ores:arcanite_ore", "useful_ores:arcanite_ingot", "useful_ores:arcanite_block"
         )
         .withImage("useful_ores:textures/gui/compendium/arcanite_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_arcanite", "useful_ores:arcanite_ingot", 1)),

      Page.fromFile(
            "Fulgurite",
            "fulgurite",
            "useful_ores:fulgurite_ore", "useful_ores:fulgurite_ingot", "useful_ores:fulgurite_block"
         )
         .withImage("useful_ores:textures/gui/compendium/fulgurite_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_fulgurite", "useful_ores:fulgurite_ingot", 1)),

      Page.fromFile(
            "Osmium",
            "osmium",
            "useful_ores:osmium_ore", "useful_ores:osmium_ingot", "useful_ores:osmium_block"
         )
         .withImage("useful_ores:textures/gui/compendium/osmium_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_osmium", "useful_ores:osmium_ingot", 1)),

      Page.fromFile(
            "Solarite",
            "solarite",
            "useful_ores:solarite_ore", "useful_ores:solarite_ingot", "useful_ores:solarite_block"
         )
         .withImage("useful_ores:textures/gui/compendium/solarite_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_solarite", "useful_ores:solarite_ingot", 1)),

      Page.fromFile(
            "Sperrylite",
            "sperrylite",
            "useful_ores:sperrylite_ore", "useful_ores:sperrylite_ingot", "useful_ores:sperrylite_block"
         )
         .withImage("useful_ores:textures/gui/compendium/sperrylite_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_sperrylite", "useful_ores:sperrylite_ingot", 1)),

      Page.fromFile(
            "Argentite",
            "argentite",
            "useful_ores:argentite_ore", "useful_ores:argentite_ingot", "useful_ores:argentite_block"
         )
         .withImage("useful_ores:textures/gui/compendium/argentite_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_argentite", "useful_ores:argentite_ingot", 1)),

      Page.fromFile(
            "Zephyrite",
            "zephyrite",
            "useful_ores:zephyrite_ore", "useful_ores:zephyrite_ingot", "useful_ores:zephyrite_block"
         )
         .withImage("useful_ores:textures/gui/compendium/zephyrite_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_zephyrite", "useful_ores:zephyrite_ingot", 1)),

      Page.fromFile(
            "Ilmenite",
            "ilmenite",
            "useful_ores:ilmenite_ore", "useful_ores:ilmenite_ingot", "useful_ores:ilmenite_block"
         )
         .withImage("useful_ores:textures/gui/compendium/ilmenite_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_ilmenite", "useful_ores:ilmenite_ingot", 1)),

      Page.fromFile(
            "Scheelite",
            "scheelite",
            "useful_ores:scheelite_ore", "useful_ores:scheelite_ingot", "useful_ores:scheelite_block"
         )
         .withImage("useful_ores:textures/gui/compendium/scheelite_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_scheelite", "useful_ores:scheelite_ingot", 1)),

      Page.fromFile(
            "Voidshard",
            "voidshard",
            "useful_ores:voidshard_ore", "useful_ores:voidshard_ingot", "useful_ores:voidshard_block"
         )
         .withImage("useful_ores:textures/gui/compendium/voidshard_book.png", 432, 73)
         .withRecipe(Recipe.smelting("useful_ores:raw_voidshard", "useful_ores:voidshard_ingot", 1)),

      Page.fromFile(
            "Subspace",
            "subspace",
            "useful_ores:subspace_ingot", "useful_ores:subspace_block"
         )
         .withImage("useful_ores:textures/gui/compendium/subspace_book.png", 432, 73),

      Page.fromFile(
            "Painite",
            "painite",
            "useful_ores:raw_painite", "useful_ores:painite_ore", "useful_ores:painite_ingot",
            "useful_ores:painite_nugget", "useful_ores:painite_block", "useful_ores:raw_painite_block",
            "useful_ores:chiseled_painite_block", "useful_ores:painite_helmet", "useful_ores:painite_chestplate",
            "useful_ores:painite_leggings", "useful_ores:painite_boots", "useful_ores:painite_sword",
            "useful_ores:painite_pickaxe", "useful_ores:painite_axe", "useful_ores:painite_hoe",
            "useful_ores:painite_shovel", "useful_ores:painite_spear"
         )
         .withRecipes(List.of(
            Recipe.smelting("useful_ores:raw_painite", "useful_ores:painite_ingot", 1),
            Recipe.smelting("useful_ores:painite_ore", "useful_ores:painite_ingot", 1),
            Recipe.crafting(new String[]{
               "useful_ores:raw_painite", "useful_ores:raw_painite", "useful_ores:raw_painite",
               "useful_ores:raw_painite", "useful_ores:raw_painite", "useful_ores:raw_painite",
               "useful_ores:raw_painite", "useful_ores:raw_painite", "useful_ores:raw_painite"
            }, "useful_ores:raw_painite_block", 1),
            Recipe.crafting(new String[]{
               "useful_ores:painite_ingot", "useful_ores:painite_ingot", "useful_ores:painite_ingot",
               "useful_ores:painite_ingot", "useful_ores:painite_ingot", "useful_ores:painite_ingot",
               "useful_ores:painite_ingot", "useful_ores:painite_ingot", "useful_ores:painite_ingot"
            }, "useful_ores:painite_block", 1),
            Recipe.crafting(new String[]{
               "useful_ores:painite_ingot", null, null,
               null, null, null,
               null, null, null
            }, "useful_ores:painite_nugget", 9),
            Recipe.crafting(new String[]{
               "useful_ores:painite_block", "useful_ores:painite_block", null,
               "useful_ores:painite_block", "useful_ores:painite_block", null,
               null, null, null
            }, "useful_ores:chiseled_painite_block", 4),
            Recipe.shapeless(List.of("useful_ores:painite_nugget", "useful_ores:painite_nugget",
               "useful_ores:painite_nugget", "useful_ores:painite_nugget", "useful_ores:painite_nugget",
               "useful_ores:painite_nugget", "useful_ores:painite_nugget", "useful_ores:painite_nugget"),
               "useful_ores:painite_ingot", 1)
         )),

      Page.fromFile(
            "Lonsdaleite",
            "lonsdaleite",
            "useful_ores:lonsdaleite_ore", "useful_ores:lonsdaleite_ingot", "useful_ores:lonsdaleite_block"
         )
         .withRecipe(Recipe.smelting("useful_ores:lonsdaleite_ore", "useful_ores:lonsdaleite_ingot", 1))

   ));

   private static final String[] MATERIALS = {
      "chromite", "enderium", "nyxium", "phosgene", "arcanite", "fulgurite", "osmium",
      "solarite", "sperrylite", "argentite", "zephyrite", "ilmenite", "scheelite", "painite", "voidshard",
      "subspace", "lonsdaleite"
   };

   private static final Chapter TOOLS_AND_COMBAT = category("Tools & Combat", "useful_ores:arcanite_sword", buildToolsAndCombatPages());

   private static List<Page> buildToolsAndCombatPages() {
      List<Page> pages = new ArrayList<>(List.of(
         Page.fromFile(
            "Special Arrows", "special_arrows",
            "useful_ores:scheelite_arrow", "useful_ores:arcanite_arrow", "useful_ores:nyxium_arrow"
         ).withRecipes(List.of(
            Recipe.crafting(new String[]{
               "useful_ores:scheelite_ingot", null, null,
               "minecraft:stick", null, null,
               "minecraft:feather", null, null
            }, "useful_ores:scheelite_arrow", 4),
            Recipe.crafting(new String[]{
               "useful_ores:arcanite_ingot", null, null,
               "minecraft:stick", null, null,
               "minecraft:feather", null, null
            }, "useful_ores:arcanite_arrow", 4),
            Recipe.crafting(new String[]{
               "useful_ores:nyxium_ingot", null, null,
               "minecraft:stick", null, null,
               "minecraft:feather", null, null
            }, "useful_ores:nyxium_arrow", 4)
         ))
      ));
      pages.add(pickaxeTierListPage());
      pages.addAll(materialArmorGroupPages());
      pages.addAll(materialToolGroupPages());
      pages.add(painiteFuryPage());
      pages.add(attributedSubspaceGearPage());
      return pages;
   }

   private static Page pickaxeTierListPage() {
      List<String> ids = List.of(
         "minecraft:wooden_pickaxe", "minecraft:stone_pickaxe",
         "useful_ores:zephyrite_pickaxe", "useful_ores:argentite_pickaxe",
         "minecraft:iron_pickaxe",
         "useful_ores:phosgene_pickaxe", "useful_ores:fulgurite_pickaxe", "useful_ores:osmium_pickaxe",
         "useful_ores:sperrylite_pickaxe", "useful_ores:scheelite_pickaxe",
         "minecraft:diamond_pickaxe",
         "useful_ores:chromite_pickaxe", "useful_ores:ilmenite_pickaxe", "useful_ores:painite_pickaxe",
         "minecraft:netherite_pickaxe",
         "useful_ores:nyxium_pickaxe", "useful_ores:arcanite_pickaxe", "useful_ores:solarite_pickaxe",
         "useful_ores:enderium_pickaxe", "useful_ores:voidshard_pickaxe", "useful_ores:subspace_pickaxe",
         "useful_ores:lonsdaleite_pickaxe"
      );
      return Page.fromFile("Pickaxe Tier List", "pickaxe_tier_list", ids.toArray(new String[0])).hideItemNames();
   }

   private static Page attributedSubspaceGearPage() {
      return Page.fromFile("Attributed Subspace Gear", "attributed_subspace_gear",
            "useful_ores:attributed_subspace_sword", "useful_ores:attributed_subspace_helmet",
            "useful_ores:attributed_subspace_pickaxe")
         .withRecipes(List.of(
            Recipe.shapeless(List.of("useful_ores:subspace_ingot", "minecraft:potion"),
               "useful_ores:attributed_subspace_ingot", 1),
            Recipe.crafting(new String[]{
               "useful_ores:attributed_subspace_ingot", null, null,
               "useful_ores:attributed_subspace_ingot", null, null,
               "minecraft:stick", null, null
            }, "useful_ores:attributed_subspace_sword", 1),
            Recipe.crafting(new String[]{
               "useful_ores:attributed_subspace_ingot", "useful_ores:attributed_subspace_ingot", "useful_ores:attributed_subspace_ingot",
               "useful_ores:attributed_subspace_ingot", null, "useful_ores:attributed_subspace_ingot",
               null, null, null
            }, "useful_ores:attributed_subspace_helmet", 1)
         ));
   }

   private static Page painiteFuryPage() {
      return Page.fromFile("Painite Fury", "painite_fury",
            "useful_ores:painite_helmet", "useful_ores:painite_chestplate", "useful_ores:painite_leggings",
            "useful_ores:painite_boots", "useful_ores:painite_sword", "useful_ores:painite_pickaxe",
            "useful_ores:painite_axe", "useful_ores:painite_hoe", "useful_ores:painite_shovel",
            "useful_ores:painite_spear").hideItemNames();
   }

   private static List<Page> materialArmorGroupPages() {
      String[] materials = MATERIALS;
      List<Page> pages = new ArrayList<>();
      for (String m : materials) {
         String p = pretty(m);
         List<String> ids = List.of("useful_ores:" + m + "_helmet", "useful_ores:" + m + "_chestplate",
            "useful_ores:" + m + "_leggings", "useful_ores:" + m + "_boots");
         List<String> paras = new ArrayList<>(List.of(armorStats(m).split("\n")));
         paras.add(armorSpecial(m));
         pages.add(Page.fromFile(p + " Armor Set", m + "_armor_set", ids.toArray(new String[0]))
            .withRecipes(List.of(armorRecipe(m, "helmet"), armorRecipe(m, "chestplate"), armorRecipe(m, "leggings"), armorRecipe(m, "boots")))
            .hideItemNames());
      }
      return pages;
   }

   private static List<Page> materialToolGroupPages() {
      List<Page> pages = new ArrayList<>();
      for (String m : MATERIALS) {
         String p = pretty(m);
         List<String> ids = List.of("useful_ores:" + m + "_sword", "useful_ores:" + m + "_pickaxe",
            "useful_ores:" + m + "_axe", "useful_ores:" + m + "_hoe", "useful_ores:" + m + "_shovel");
         List<String> paras = new ArrayList<>(List.of(toolStats(m).split("\n")));
         paras.add(swordAbility(m));
         pages.add(Page.fromFile(p + " Weapons & Tools", m + "_weapons_tools", ids.toArray(new String[0]))
            .withRecipes(List.of(swordRecipe(m), pickaxeRecipe(m), axeRecipe(m), hoeRecipe(m), shovelRecipe(m)))
            .hideItemNames());
      }
      return pages;
   }

   private static String armorStats(String m) {
      return switch (m) {
         case "chromite" -> "Defense: helmet 2, chestplate 7, leggings 5, boots 2.\nDurability: helmet 297, chestplate 432, leggings 405, boots 351.\nEnchantability 12, toughness 1.2, knockback resistance 15%.";
         case "enderium" -> "Defense: helmet 4, chestplate 11, leggings 7, boots 3.\nDurability: helmet 308, chestplate 448, leggings 420, boots 364.\nEnchantability 24, toughness 3.4, knockback resistance 18%.";
         case "nyxium" -> "Defense: helmet 3, chestplate 8, leggings 5, boots 2.\nDurability: helmet 374, chestplate 544, leggings 510, boots 442.\nEnchantability 15, toughness 2.2, knockback resistance 10%.";
         case "phosgene" -> "Defense: helmet 2, chestplate 5, leggings 3, boots 1.\nDurability: helmet 198, chestplate 288, leggings 270, boots 234.\nEnchantability 14, toughness 0.4, knockback resistance 10%.";
         case "arcanite" -> "Defense: helmet 2, chestplate 7, leggings 5, boots 2.\nDurability: helmet 319, chestplate 464, leggings 435, boots 377.\nEnchantability 32, toughness 2.2, knockback resistance 0%.";
         case "fulgurite" -> "Defense: helmet 2, chestplate 7, leggings 5, boots 2.\nDurability: helmet 286, chestplate 416, leggings 390, boots 338.\nEnchantability 16, toughness 1.8, knockback resistance 20%.";
         case "osmium" -> "Defense: helmet 2, chestplate 8, leggings 5, boots 2.\nDurability: helmet 330, chestplate 480, leggings 450, boots 390.\nEnchantability 20, toughness 1.5, knockback resistance 35%.";
         case "solarite" -> "Defense: helmet 3, chestplate 10, leggings 7, boots 3.\nDurability: helmet 330, chestplate 480, leggings 450, boots 390.\nEnchantability 20, toughness 3.0, knockback resistance 15%.";
         case "sperrylite" -> "Defense: helmet 2, chestplate 6, leggings 4, boots 2.\nDurability: helmet 231, chestplate 336, leggings 315, boots 273.\nEnchantability 29, toughness 0.3, knockback resistance 5%.";
         case "argentite" -> "Defense: helmet 2, chestplate 5, leggings 3, boots 1.\nDurability: helmet 209, chestplate 304, leggings 285, boots 247.\nEnchantability 18, toughness 0.2, knockback resistance 0%.";
         case "zephyrite" -> "Defense: helmet 1, chestplate 3, leggings 2, boots 1.\nDurability: helmet 154, chestplate 224, leggings 210, boots 182.\nEnchantability 8, toughness 0, knockback resistance 0%.";
         case "ilmenite" -> "Defense: helmet 3, chestplate 8, leggings 5, boots 2.\nDurability: helmet 418, chestplate 608, leggings 570, boots 494.\nEnchantability 10, toughness 3.2, knockback resistance 20%.";
         case "scheelite" -> "Defense: helmet 2, chestplate 5, leggings 3, boots 1.\nDurability: helmet 209, chestplate 304, leggings 285, boots 247.\nEnchantability 10, toughness 0.1, knockback resistance 15%.";
         case "voidshard" -> "Defense: helmet 4, chestplate 13, leggings 9, boots 3.\nDurability: helmet 330, chestplate 480, leggings 450, boots 390.\nEnchantability 27, toughness 3.8, knockback resistance 22%.";
         case "subspace" -> "Defense: helmet 5, chestplate 15, leggings 10, boots 4.\nDurability: helmet 682, chestplate 992, leggings 930, boots 806.\nEnchantability 34, toughness 4.0, knockback resistance 40%.";
         case "lonsdaleite" -> "Defense: helmet 4, chestplate 13, leggings 9, boots 3.\nDurability: helmet 638, chestplate 928, leggings 870, boots 754.\nEnchantability 27, toughness 3.8, knockback resistance 22%.";
         case "painite" -> "Defense: helmet 3, chestplate 8, leggings 6, boots 3.\nDurability: helmet 363, chestplate 528, leggings 495, boots 429.\nEnchantability 10, toughness 2.0, knockback resistance 0%.";
         default -> "Armor stats come from the material configuration.";
      };
   }

   private static String armorSpecial(String m) {
      if (m.equals("osmium")) return "Set bonus: the full four-piece set pulls you down through water and lets you walk on the seafloor instead of floating. A partial set does nothing.";
      if (m.equals("lonsdaleite")) return "Set bonus: each worn piece cuts explosion damage by 17.5%, up to 70% with all four. Losing a piece weakens the bonus in proportion. All Lonsdaleite armor and tools are fully enchantable.";
      if (m.equals("painite")) return "Set bonus: each worn piece lowers max health by 1 HP, down to 8 hearts with the full set. At 5 hearts or below, Fury turns on: each piece adds 60% of its defense again, the armor switches to its blue-crimson look and the chest shows a molten crack. See [Painite Fury](Tools & Combat/Painite Fury).";
      return "Set bonus: none. This armor only has its stats.";
   }

   private static String toolStats(String m) {
      return switch (m) {
         case "chromite" -> "Material: durability 1650, mining speed 7.0, damage bonus +8.0, enchantability 12.\nDamage / attack speed: sword 15.0 / 1.0, pickaxe 14.0 / 1.2, axe 16.0 / 1.0, hoe 8.0 / 2.0, shovel 13.0 / 1.0.";
         case "enderium" -> "Material: durability 2700, mining speed 20.0, damage bonus +13.5, enchantability 26.\nDamage / attack speed: sword 22.5 / 2.0, pickaxe 21.5 / 1.8, axe 23.5 / 1.6, hoe 14.5 / 4.8, shovel 19.5 / 1.6.";
         case "nyxium" -> "Material: durability 2200, mining speed 8.0, damage bonus +10.0, enchantability 15.\nDamage / attack speed: sword 16.0 / 1.8, pickaxe 15.0 / 1.2, axe 17.0 / 1.2, hoe 10.0 / 2.0, shovel 14.0 / 1.0.";
         case "phosgene" -> "Material: durability 650, mining speed 6.5, damage bonus +6.0, enchantability 6.\nDamage / attack speed: sword 11.0 / 1.4, pickaxe 9.0 / 1.2, axe 11.0 / 0.9, hoe 6.0 / 3.0, shovel 9.0 / 1.0.";
         case "arcanite" -> "Material: durability 2150, mining speed 8.5, damage bonus +11.5, enchantability 32.\nDamage / attack speed: sword 16.5 / 1.4, pickaxe 15.5 / 1.2, axe 17.5 / 1.0, hoe 11.5 / 2.0, shovel 14.5 / 1.0.";
         case "fulgurite" -> "Material: durability 1750, mining speed 7.5, damage bonus +8.5, enchantability 16.\nDamage / attack speed: sword 15.5 / 0.6, pickaxe 14.5 / 1.0, axe 16.5 / 0.4, hoe 8.5 / 2.0, shovel 13.5 / 0.8.";
         case "osmium" -> "Material: durability 1900, mining speed 6.0, damage bonus +8.5, enchantability 22.\nDamage / attack speed: sword 13.5 / 1.6, pickaxe 12.5 / 1.2, axe 14.5 / 1.0, hoe 8.5 / 2.0, shovel 11.5 / 1.0.";
         case "solarite" -> "Material: durability 2400, mining speed 15.0, damage bonus +12.0, enchantability 24.\nDamage / attack speed: sword 17.0 / 1.8, pickaxe 16.0 / 1.4, axe 18.0 / 1.2, hoe 12.0 / 4.5, shovel 15.0 / 1.2.";
         case "sperrylite" -> "Material: durability 700, mining speed 6.5, damage bonus +7.0, enchantability 29.\nDamage / attack speed: sword 13.0 / 1.6, pickaxe 8.0 / 1.2, axe 11.0 / 1.0, hoe 7.0 / 1.0, shovel 8.0 / 1.0.";
         case "argentite" -> "Material: durability 380, mining speed 5.0, damage bonus +6.5, enchantability 18.\nDamage / attack speed: sword 10.5 / 2.2, pickaxe 9.5 / 1.2, axe 11.5 / 0.9, hoe 6.5 / 3.0, shovel 9.5 / 1.0.";
         case "zephyrite" -> "Material: durability 190, mining speed 5.0, damage bonus +4.0, enchantability 9.\nDamage / attack speed: sword 7.0 / 1.6, pickaxe 6.0 / 1.2, axe 9.0 / 0.8, hoe 4.0 / 2.0, shovel 6.0 / 1.0.";
         case "ilmenite" -> "Material: durability 2600, mining speed 7.0, damage bonus +9.0, enchantability 10.\nDamage / attack speed: sword 15.0 / 1.4, pickaxe 14.0 / 1.2, axe 16.0 / 1.0, hoe 10.0 / 2.0, shovel 13.0 / 1.0.";
         case "scheelite" -> "Material: durability 900, mining speed 6.0, damage bonus +6.5, enchantability 10.\nDamage / attack speed: sword 10.5 / 1.6, pickaxe 9.5 / 1.2, axe 11.5 / 0.9, hoe 6.5 / 3.0, shovel 9.5 / 1.0.";
         case "voidshard" -> "Material: durability 3000, mining speed 25.0, damage bonus +15.0, enchantability 28.\nDamage / attack speed: sword 26.0 / 2.2, pickaxe 24.0 / 2.0, axe 28.0 / 1.8, hoe 15.0 / 5.0, shovel 23.0 / 1.8.";
         case "subspace" -> "Material: durability 6400, mining speed 32.0, damage bonus +16.0, enchantability 34. Highest raw stats of any material in the mod.\nDamage / attack speed: sword 31.0 / 2.6, pickaxe 29.0 / 2.4, axe 33.0 / 2.2, hoe 18.0 / 5.2, shovel 27.0 / 2.2.";
         case "lonsdaleite" -> "Material: durability 6200, mining speed 25.0, damage bonus +15.0, enchantability 28.\nDamage / attack speed: sword 26.0 / 2.2, pickaxe 24.0 / 2.0, axe 28.0 / 1.8, hoe 15.0 / 5.0, shovel 23.0 / 1.8.";
         case "painite" -> "Material: durability 1561, mining speed 8.0, damage bonus +3.0, enchantability 10.\nDamage / attack speed: sword 6.0 / 1.6, pickaxe 4.0 / 1.2, axe 8.0 / 1.0, hoe 0.0 / 4.0, shovel 4.5 / 1.0.";
         default -> "Tool stats come from the material configuration.";
      };
   }

   private static Recipe pickaxeRecipe(String m) {
      String i = "useful_ores:" + m + "_ingot";
      return Recipe.crafting(new String[]{i,i,i,null,"minecraft:stick",null,null,"minecraft:stick",null}, "useful_ores:" + m + "_pickaxe", 1);
   }
   private static Recipe axeRecipe(String m) {
      String i = "useful_ores:" + m + "_ingot";
      return Recipe.crafting(new String[]{i,i,null,i,"minecraft:stick",null,null,"minecraft:stick",null}, "useful_ores:" + m + "_axe", 1);
   }
   private static Recipe hoeRecipe(String m) {
      String i = "useful_ores:" + m + "_ingot";
      return Recipe.crafting(new String[]{i,i,null,null,"minecraft:stick",null,null,"minecraft:stick",null}, "useful_ores:" + m + "_hoe", 1);
   }
   private static Recipe shovelRecipe(String m) {
      String i = "useful_ores:" + m + "_ingot";
      return Recipe.crafting(new String[]{i,null,null,"minecraft:stick",null,null,"minecraft:stick",null,null}, "useful_ores:" + m + "_shovel", 1);
   }

   private static String swordAbility(String material) {
      return switch (material) {
         case "nyxium" -> "On hit: Wither for 3 seconds.";
         case "phosgene" -> "On hit: Poison for 3 seconds.";
         case "fulgurite" -> "On hit: you get Speed for 3 seconds.";
         case "osmium" -> "On hit: Slowness for 3 seconds.";
         case "sperrylite" -> "On hit: Weakness for 3 seconds.";
         case "scheelite" -> "On hit: Slowness II for 2 seconds, which staggers the target.";
         case "painite" -> "Low health: at 5 hearts or below, a held Painite sword, pickaxe, axe, hoe or shovel gets +60% attack damage and +60% block breaking speed.";
         default -> "On hit: no special effect.";
      };
   }

   private static Recipe swordRecipe(String m) {
      String ingot = "useful_ores:" + m + "_ingot";
      if (m.equals("nyxium")) {
         return Recipe.crafting(new String[]{ingot, null, null, "minecraft:netherite_ingot", null, null, "minecraft:stick", null, null}, "useful_ores:" + m + "_sword", 1);
      }
      return Recipe.crafting(new String[]{ingot, null, null, ingot, null, null, "minecraft:stick", null, null}, "useful_ores:" + m + "_sword", 1);
   }

   private static Recipe armorRecipe(String m, String slot) {
      String ingot = "useful_ores:" + m + "_ingot";
      String[] g;
      switch (slot) {
         case "helmet" -> g = new String[]{ingot, ingot, ingot, ingot, null, ingot, null, null, null};
         case "chestplate" -> g = new String[]{ingot, null, ingot, ingot, ingot, ingot, ingot, ingot, ingot};
         case "leggings" -> g = new String[]{ingot, ingot, ingot, ingot, null, ingot, ingot, null, ingot};
         default -> g = new String[]{ingot, null, ingot, ingot, null, ingot, null, null, null};
      }
      return Recipe.crafting(g, "useful_ores:" + m + "_" + slot, 1);
   }

   private static final Chapter REDSTONE = category("Redstone", "useful_ores:wireless_redstone_relay", List.of(

      Page.fromFile("Chromite Piston", "chromite_piston", List.of("useful_ores:chromite_piston", "useful_ores:chromite_sticky_piston").toArray(new String[0]))
         .withRecipes(List.of(
            Recipe.crafting(new String[]{
               "minecraft:oak_planks", "minecraft:oak_planks", "minecraft:oak_planks",
               "minecraft:deepslate", "minecraft:redstone", "minecraft:deepslate",
               "minecraft:deepslate", "useful_ores:chromite_ingot", "minecraft:deepslate"
            }, "useful_ores:chromite_piston", 1),
            Recipe.crafting(new String[]{
               null, "minecraft:slime_ball", null,
               null, "useful_ores:chromite_piston", null,
               null, null, null
            }, "useful_ores:chromite_sticky_piston", 1)
         )),

      Page.fromFile("Wireless Redstone Relay", "wireless_redstone_relay", List.of("useful_ores:wireless_redstone_relay").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:redstone_torch", "minecraft:redstone", "minecraft:redstone_torch",
            "minecraft:copper_ingot", "useful_ores:raw_zephyrite", "minecraft:copper_ingot",
            "minecraft:stone", "minecraft:stone", "minecraft:stone"
         }, "useful_ores:wireless_redstone_relay", 1)),

      Page.fromFile("Redstone Clock", "redstone_clock", List.of("useful_ores:redstone_clock").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:raw_zephyrite", "minecraft:redstone_torch", "useful_ores:raw_zephyrite",
            "minecraft:redstone_torch", "minecraft:clock", "minecraft:redstone_torch",
            "useful_ores:raw_zephyrite", "minecraft:redstone_torch", "useful_ores:raw_zephyrite"
         }, "useful_ores:redstone_clock", 1)),

      Page.fromFile("AND Gate", "and_gate", List.of("useful_ores:and_gate").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:quartz", "minecraft:redstone_torch", "minecraft:quartz",
            "minecraft:redstone_torch", "useful_ores:raw_zephyrite", "minecraft:redstone_torch",
            "minecraft:quartz", "minecraft:redstone_torch", "minecraft:quartz"
         }, "useful_ores:and_gate", 1)),

      Page.fromFile("NAND Gate", "nand_gate", List.of("useful_ores:nand_gate").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:quartz", "minecraft:redstone_torch", "minecraft:quartz",
            "minecraft:redstone_torch", "minecraft:copper_ingot", "minecraft:redstone_torch",
            "minecraft:quartz", "minecraft:redstone_torch", "minecraft:quartz"
         }, "useful_ores:nand_gate", 1)),

      Page.fromFile("OR Gate", "or_gate", List.of("useful_ores:or_gate").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:redstone_torch", "minecraft:quartz", "minecraft:redstone_torch",
            "minecraft:quartz", "useful_ores:raw_zephyrite", "minecraft:quartz",
            "minecraft:redstone_torch", "minecraft:quartz", "minecraft:redstone_torch"
         }, "useful_ores:or_gate", 1)),

      Page.fromFile("NOR Gate", "nor_gate", List.of("useful_ores:nor_gate").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:redstone_torch", "minecraft:quartz", "minecraft:redstone_torch",
            "minecraft:quartz", "minecraft:copper_ingot", "minecraft:quartz",
            "minecraft:redstone_torch", "minecraft:quartz", "minecraft:redstone_torch"
         }, "useful_ores:nor_gate", 1)),

      Page.fromFile("XOR Gate", "xor_gate", List.of("useful_ores:xor_gate").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:copper_ingot", "minecraft:quartz", "minecraft:copper_ingot",
            "minecraft:quartz", "useful_ores:raw_zephyrite", "minecraft:quartz",
            "minecraft:copper_ingot", "minecraft:quartz", "minecraft:copper_ingot"
         }, "useful_ores:xor_gate", 1)),

      Page.fromFile("NOT Gate", "not_gate", List.of("useful_ores:not_gate").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:quartz", null, null,
            "minecraft:redstone_torch", null, null,
            "useful_ores:raw_zephyrite", null, null
         }, "useful_ores:not_gate", 1)),

      Page.fromFile("Argentite Filter", "argentite_filter", List.of("useful_ores:argentite_filter").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:argentite_ingot", null, "useful_ores:argentite_ingot",
            "useful_ores:argentite_ingot", "minecraft:chest", "useful_ores:argentite_ingot",
            null, "useful_ores:argentite_ingot", null
         }, "useful_ores:argentite_filter", 1)),

      Page.fromFile("Fulgurite Electric Trap", "fulgurite_electric_trap", List.of("useful_ores:fulgurite_electric_trap").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:copper_ingot", null, null,
            "useful_ores:fulgurite_ingot", "useful_ores:raw_fulgurite", "useful_ores:fulgurite_ingot",
            "useful_ores:argentite_ingot", "useful_ores:raw_zephyrite", "useful_ores:argentite_ingot"
         }, "useful_ores:fulgurite_electric_trap", 1))
   ));

   private static final Chapter UTILITY = category("Utility", "useful_ores:nyxiumnite_staff", List.of(

      Page.fromFile("Catalytic Vial", "catalytic_vial", List.of("useful_ores:sperrylite_catalytic_vial", "useful_ores:sperrylite_catalytic_vial_splash",
               "useful_ores:sperrylite_catalytic_vial_lingering").toArray(new String[0]))
         .withRecipes(List.of(
            Recipe.crafting(new String[]{
               "useful_ores:sperrylite_nugget", "useful_ores:sperrylite_nugget", "useful_ores:sperrylite_nugget",
               "useful_ores:sperrylite_nugget", "minecraft:glass_bottle", "useful_ores:sperrylite_nugget",
               "useful_ores:sperrylite_nugget", "useful_ores:sperrylite_nugget", "useful_ores:sperrylite_nugget"
            }, "useful_ores:sperrylite_catalytic_vial", 1),
            Recipe.brewing(
               List.of("useful_ores:sperrylite_catalytic_vial", "minecraft:potion", "minecraft:potion"),
               "useful_ores:sperrylite_nugget",
               "useful_ores:sperrylite_catalytic_vial", 1),
            Recipe.brewing(
               List.of("useful_ores:sperrylite_catalytic_vial"),
               "minecraft:gunpowder",
               "useful_ores:sperrylite_catalytic_vial_splash", 1),
            Recipe.brewing(
               List.of("useful_ores:sperrylite_catalytic_vial_splash"),
               "minecraft:dragon_breath",
               "useful_ores:sperrylite_catalytic_vial_lingering", 1)
         )),

      Page.fromFile("Arcanite XP Jar", "arcanite_xp_jar", List.of("useful_ores:arcanite_xp_jar").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            null, "useful_ores:arcanite_ingot", null,
            null, "useful_ores:silver_core_arcanite_shard", null,
            "minecraft:glass", "minecraft:glass", "minecraft:glass"
         }, "useful_ores:arcanite_xp_jar", 1)),

      Page.fromFile("Nyxiumnite Staff", "nyxiumnite_staff", List.of("useful_ores:nyxiumnite_staff").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            null, "useful_ores:volatile_nyxiumnite_core", null,
            null, "useful_ores:argentite_ingot", null,
            null, "useful_ores:argentite_ingot", null
         }, "useful_ores:nyxiumnite_staff", 1)),

      Page.fromFile("Meteor Staff", "meteor_staff", List.of("useful_ores:meteor_staff").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:meteorite_block", "useful_ores:volatile_meteorite_core", "useful_ores:meteorite_block",
            "minecraft:blaze_rod", "minecraft:blaze_rod", "minecraft:blaze_rod",
            null, "minecraft:blaze_rod", null
         }, "useful_ores:meteor_staff", 1)),

      Page.fromFile("Nyxium-Arcanite Core", "nyxium_arcanite_core", List.of("useful_ores:nyxium_arcanite_core").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:nyxium_ingot", "useful_ores:arcanite_ingot", "useful_ores:nyxium_ingot",
            "useful_ores:arcanite_ingot", "minecraft:ender_eye", "useful_ores:arcanite_ingot",
            "useful_ores:voidshard_ingot", "useful_ores:arcanite_ingot", "useful_ores:voidshard_ingot"
         }, "useful_ores:nyxium_arcanite_core", 1)),

      Page.fromFile("Volatile Nyxiumnite Core", "volatile_nyxiumnite_core", List.of("useful_ores:volatile_nyxiumnite_core").toArray(new String[0]))
         .withRecipe(Recipe.shapeless(
            List.of("useful_ores:nyxium_arcanite_core", "minecraft:dragon_breath"),
            "useful_ores:volatile_nyxiumnite_core", 1)),

      Page.fromFile("Meteorite Core Shard", "meteorite_core_shard", List.of("useful_ores:meteorite_core_shard").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:blaze_rod", null, null,
            "useful_ores:meteorite_core", null, null,
            "minecraft:blaze_rod", null, null
         }, "useful_ores:meteorite_core_shard", 1)),

      Page.fromFile("Volatile Meteorite Core", "volatile_meteorite_core", List.of("useful_ores:volatile_meteorite_core").toArray(new String[0]))
         .withRecipe(Recipe.shapeless(
            List.of("useful_ores:meteorite_core_shard", "minecraft:dragon_breath"),
            "useful_ores:volatile_meteorite_core", 1)),

      Page.fromFile("Silver Core Arcanite Shard", "silver_core_arcanite_shard", List.of("useful_ores:silver_core_arcanite_shard").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:arcanite_nugget", "useful_ores:arcanite_nugget", "useful_ores:arcanite_nugget",
            "useful_ores:arcanite_nugget", "useful_ores:argentite_nugget", "useful_ores:arcanite_nugget",
            "useful_ores:arcanite_nugget", "useful_ores:arcanite_nugget", "useful_ores:arcanite_nugget"
         }, "useful_ores:silver_core_arcanite_shard", 1)),

      Page.fromFile("Dark Core", "dark_core", List.of("useful_ores:dark_core").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:nyxium_ingot", "minecraft:ender_pearl", "useful_ores:nyxium_ingot",
            "minecraft:ender_pearl", "minecraft:ender_eye", "minecraft:ender_pearl",
            "useful_ores:voidshard_ingot", "minecraft:ender_pearl", "useful_ores:voidshard_ingot"
         }, "useful_ores:dark_core", 1)),

      Page.fromFile("Farseeker Crystal", "farseeker_crystal", List.of("useful_ores:farseeker_crystal").toArray(new String[0])),

      Page.fromFile("Extruding Crystal Ore", "extruding_crystal_ore", List.of("useful_ores:extruding_crystal_ore").toArray(new String[0])),

      Page.fromFile("Extruding Crystal Nugget", "extruding_crystal_nugget", List.of("useful_ores:extruding_crystal_nugget").toArray(new String[0])),

      Page.fromFile("Extruding Crystal", "extruding_crystal", List.of("useful_ores:extruding_crystal").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget",
            "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget",
            "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget"
         }, "useful_ores:extruding_crystal", 1)),

      Page.fromFile("Extruding Nether Star", "extruding_nether_star", List.of("useful_ores:extruding_nether_star").toArray(new String[0]))
         .withRecipe(Recipe.shapeless(
            List.of("minecraft:nether_star", "useful_ores:extruding_crystal"),
            "useful_ores:extruding_nether_star", 1)),

      Page.fromFile("Heated Shard", "heated_shard", List.of("useful_ores:heated_shard").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:voidshard_ingot", "useful_ores:solarite_ingot", "useful_ores:voidshard_ingot",
            "useful_ores:solarite_ingot", null, "useful_ores:solarite_ingot",
            "useful_ores:voidshard_ingot", "useful_ores:solarite_ingot", "useful_ores:voidshard_ingot"
         }, "useful_ores:heated_shard", 1)),

      Page.fromFile("Meteor Bomb", "meteor_bomb", List.of("useful_ores:meteor_bomb").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:meteor_dust", "useful_ores:meteor_dust", "useful_ores:meteor_dust",
            "useful_ores:meteor_dust", "minecraft:gunpowder", "useful_ores:meteor_dust",
            "useful_ores:meteor_dust", "useful_ores:meteor_dust", "useful_ores:meteor_dust"
         }, "useful_ores:meteor_bomb", 4)),

      Page.fromFile("Phosgene Potion Powders", "phosgene_potion_powders", List.of("useful_ores:phosgene_raw_powder", "useful_ores:phosgene_potion_powder").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:raw_phosgene", "minecraft:redstone", "useful_ores:raw_phosgene",
            "minecraft:redstone", "minecraft:slime_block", "minecraft:blaze_rod",
            "useful_ores:raw_phosgene", "minecraft:redstone", "useful_ores:raw_phosgene"
         }, "useful_ores:phosgene_raw_powder", 16)),
      Page.fromFile("Lonsdaleite Layer Glue", "lonsdaleite_layer_glue", List.of("useful_ores:lonsdaleite_layer_glue").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:slime_ball", "minecraft:slime_ball", "minecraft:slime_ball",
            "minecraft:slime_ball", "useful_ores:raw_lonsdaleite", "minecraft:slime_ball",
            "minecraft:slime_ball", "minecraft:slime_ball", "minecraft:slime_ball"
         }, "useful_ores:lonsdaleite_layer_glue", 1)),

      Page.fromFile("Ancient Pedestal", "ancient_pedestal", List.of("useful_ores:ancient_pedestal").toArray(new String[0])),

      Page.fromFile("Titanium Rail", "titanium_rail", List.of("useful_ores:titanium_rail").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:ilmenite_ingot", null, "useful_ores:ilmenite_ingot",
            "useful_ores:ilmenite_ingot", "minecraft:stick", "useful_ores:ilmenite_ingot",
            "useful_ores:ilmenite_ingot", null, "useful_ores:ilmenite_ingot"
         }, "useful_ores:titanium_rail", 16)),

      Page.fromFile("Titanium Controller Rail", "titanium_controller_rail", List.of("useful_ores:titanium_controller_rail").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:ilmenite_ingot", "minecraft:stick", "useful_ores:ilmenite_ingot",
            "useful_ores:ilmenite_ingot", "useful_ores:raw_zephyrite", "useful_ores:ilmenite_ingot",
            "useful_ores:ilmenite_ingot", "minecraft:redstone", "useful_ores:ilmenite_ingot"
         }, "useful_ores:titanium_controller_rail", 6)),

      Page.fromFile("Enderium Rail", "enderium_rail", List.of("useful_ores:enderium_rail").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:enderium_ingot", "minecraft:stick", "useful_ores:enderium_ingot",
            "useful_ores:enderium_ingot", "minecraft:ender_pearl", "useful_ores:enderium_ingot",
            "useful_ores:enderium_ingot", "minecraft:stick", "useful_ores:enderium_ingot"
         }, "useful_ores:enderium_rail", 8)),

      Page.fromFile("Solarite Rail", "solarite_rail", List.of("useful_ores:solarite_rail").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:solarite_ingot", null, "useful_ores:solarite_ingot",
            "useful_ores:solarite_ingot", "minecraft:stick", "useful_ores:solarite_ingot",
            "useful_ores:solarite_ingot", null, "useful_ores:solarite_ingot"
         }, "useful_ores:solarite_rail", 16)),

      Page.fromFile("Solarite Furnace", "solarite_furnace", List.of("useful_ores:solarite_furnace_component").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:solarite_ingot", "useful_ores:solarite_ingot", "useful_ores:solarite_ingot",
            "useful_ores:solarite_ingot", "minecraft:furnace", "useful_ores:solarite_ingot",
            "minecraft:smooth_stone", "useful_ores:phosgene_block", "minecraft:smooth_stone"
         }, "useful_ores:solarite_furnace_component", 1)),

      Page.fromFile("Solar Battery", "solar_battery", List.of("useful_ores:solar_battery").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:iron_nugget", "useful_ores:phosgene_ingot", "minecraft:iron_nugget",
            "minecraft:iron_nugget", "useful_ores:solarite_ingot", "minecraft:iron_nugget",
            "minecraft:iron_nugget", "useful_ores:solarite_ingot", "minecraft:iron_nugget"
         }, "useful_ores:solar_battery", 1)),

      Page.fromFile("Solarite Battery Minecart", "solarite_battery_minecart", List.of("useful_ores:solarite_battery_minecart").toArray(new String[0]))
         .withRecipe(Recipe.shapeless(
            List.of("minecraft:minecart", "useful_ores:solar_battery", "useful_ores:solarite_ingot"),
            "useful_ores:solarite_battery_minecart", 1)),

      Page.fromFile("Super Beacon", "super_beacon", List.of("useful_ores:super_beacon").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:glass", "minecraft:glass", "minecraft:glass",
            "minecraft:glass", "useful_ores:extruding_nether_star", "minecraft:glass",
            "minecraft:obsidian", "minecraft:obsidian", "minecraft:obsidian"
         }, "useful_ores:super_beacon", 1)),

      Page.fromFile("Nyxium Dark Barrier", "nyxium_dark_barrier", List.of("useful_ores:nyxium_dark_barrier").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:nyxium_ingot", "minecraft:blackstone", "useful_ores:nyxium_ingot",
            "useful_ores:arcanite_ingot", "useful_ores:dark_core", "useful_ores:arcanite_ingot",
            "useful_ores:nyxium_ingot", "minecraft:blackstone", "useful_ores:nyxium_ingot"
         }, "useful_ores:nyxium_dark_barrier", 1)),

      Page.fromFile("Scheelite Chisel", "scheelite_chisel", List.of("useful_ores:scheelite_chisel").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:scheelite_ingot", null, null,
            null, "minecraft:stick", null,
            null, null, "minecraft:stick"
         }, "useful_ores:scheelite_chisel", 1)),

      Page.fromFile("Titanium Lock", "titanium_lock", List.of("useful_ores:titanium_lock").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:ilmenite_nugget", "useful_ores:ilmenite_nugget", "useful_ores:ilmenite_nugget",
            "useful_ores:ilmenite_ingot", "useful_ores:ilmenite_ingot", "useful_ores:ilmenite_ingot",
            "useful_ores:ilmenite_ingot", "useful_ores:ilmenite_ingot", "useful_ores:ilmenite_ingot"
         }, "useful_ores:titanium_lock", 1)),

      Page.fromFile("Titanium Key", "titanium_key", List.of("useful_ores:titanium_key").toArray(new String[0]))
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:ilmenite_nugget", "useful_ores:ilmenite_nugget", null,
            null, null, null,
            null, null, null
         }, "useful_ores:titanium_key", 1))
   ));

   private static final Chapter MISC = category("Misc", "useful_ores:lonsdaleite_campfire", buildMiscPages());

   private static List<Page> buildMiscPages() {
      List<Page> pages = new ArrayList<>();
      pages.add(Page.fromFile("Crashed Meteorite", "crashed_meteorite", List.of("useful_ores:meteorite_core", "useful_ores:meteorite_ash_block", "useful_ores:lonsdaleite_ore").toArray(new String[0])).hideItemNames());

      pages.add(Page.fromFile("Ancient Pedestal Chamber", "ancient_pedestal_chamber", List.of("useful_ores:ancient_pedestal").toArray(new String[0])).hideItemNames());

      pages.addAll(buildLightingPages());
      return pages;
   }

   private static List<Page> buildLightingPages() {
      return individualLightingPages();
   }

   private static List<Page> individualLightingPages() {
      String[] torch = {"arcanite", "argentite", "chromite", "enderium", "fulgurite", "ilmenite", "nyxium", "osmium", "phosgene", "scheelite", "solarite", "sperrylite", "voidshard", "zephyrite"};
      String[] campfire = {"arcanite", "argentite", "chromite", "enderium", "fulgurite", "ilmenite", "lonsdaleite", "nyxium", "osmium", "phosgene", "scheelite", "solarite", "sperrylite", "voidshard", "zephyrite"};
      List<Page> pages = new ArrayList<>();

      List<String> torchIds = new ArrayList<>();
      List<Recipe> torchRecipes = new ArrayList<>();
      List<String> torchText = new ArrayList<>();
      for (String m : torch) {
         torchIds.add("useful_ores:" + m + "_torch");
         torchRecipes.add(Recipe.crafting(new String[]{"useful_ores:" + m + "_nugget", null, null, "minecraft:stick", null, null, null, null, null}, "useful_ores:" + m + "_torch", 4));
         torchText.add(lightingDescription(m, "Torch"));
      }
      pages.add(Page.withItems("Torches", torchIds, torchText.toArray(new String[0]))
         .withRecipes(torchRecipes).hideItemNames());

      List<String> lanternIds = new ArrayList<>();
      List<Recipe> lanternRecipes = new ArrayList<>();
      List<String> lanternText = new ArrayList<>();
      for (String m : torch) {
         lanternIds.add("useful_ores:" + m + "_lantern");
         String i = "useful_ores:" + m + "_nugget";
         lanternRecipes.add(Recipe.crafting(new String[]{i,i,i,i,"useful_ores:" + m + "_torch",i,i,i,i}, "useful_ores:" + m + "_lantern", 1));
         lanternText.add(lightingDescription(m, "Lantern"));
      }
      lanternIds.add("useful_ores:subspace_lantern");
      lanternRecipes.add(Recipe.crafting(new String[]{"useful_ores:subspace_ingot","useful_ores:subspace_ingot","useful_ores:subspace_ingot","useful_ores:subspace_ingot","useful_ores:subspace_torch","useful_ores:subspace_ingot","useful_ores:subspace_ingot","useful_ores:subspace_ingot","useful_ores:subspace_ingot"}, "useful_ores:subspace_lantern", 1));
      lanternText.add(switch (net.minecraft.client.Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase().split("_")[0]) {
         case "es" -> "Farol de subespacio: es el modelo m\u00e1s avanzado de la l\u00ednea y funciona como un farol normal.";
         case "pt" -> "Lanterna de subespa\u00e7o: o modelo mais avan\u00e7ado da linha, com o funcionamento de uma lanterna comum.";
         case "fr" -> "Lanterne de sous-espace : le mod\u00e8le le plus avanc\u00e9 de la gamme, qui fonctionne comme une lanterne classique.";
         case "it" -> "Lanterna del sottospazio: il modello pi\u00f9 avanzato della linea, con il funzionamento di una lanterna normale.";
         case "ru" -> "\u041f\u043e\u0434\u043f\u0440\u043e\u0441\u0442\u0440\u0430\u043d\u0441\u0442\u0432\u0435\u043d\u043d\u044b\u0439 \u0444\u043e\u043d\u0430\u0440\u044c: \u0441\u0430\u043c\u0430\u044f \u043f\u0440\u043e\u0434\u0432\u0438\u043d\u0443\u0442\u0430\u044f \u043c\u043e\u0434\u0435\u043b\u044c \u0432 \u043b\u0438\u043d\u0435\u0439\u043a\u0435. \u0420\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u043a\u0430\u043a \u043e\u0431\u044b\u0447\u043d\u044b\u0439 \u0444\u043e\u043d\u0430\u0440\u044c.";
         case "zh" -> "\u4e9a\u7a7a\u95f4\u706f\u7b3c\uff1a\u8be5\u7cfb\u5217\u4e2d\u6700\u5148\u8fdb\u7684\u706f\u7b3c\uff0c\u529f\u80fd\u4e0e\u666e\u901a\u706f\u7b3c\u76f8\u540c\u3002";
         default -> "Subspace Lantern: the top-tier lantern in the Subspace range. Works like a normal lantern.";
      });
      pages.add(Page.withItems("Lanterns", lanternIds, lanternText.toArray(new String[0]))
         .withRecipes(lanternRecipes).hideItemNames());

      List<String> campfireIds = new ArrayList<>();
      List<Recipe> campfireRecipes = new ArrayList<>();
      List<String> campfireText = new ArrayList<>();
      for (String m : campfire) {
         campfireIds.add("useful_ores:" + m + "_campfire");
         campfireRecipes.add(Recipe.crafting(new String[]{null,"minecraft:stick",null,"minecraft:stick","useful_ores:" + m + "_infused_coal","minecraft:stick","minecraft:oak_log","minecraft:oak_log","minecraft:oak_log"}, "useful_ores:" + m + "_campfire", 1));
         campfireText.add(lightingDescription(m, "Campfire"));
      }
      pages.add(Page.withItems("Campfires", campfireIds, campfireText.toArray(new String[0]))
         .withRecipes(campfireRecipes).hideItemNames());

      return pages;
   }

   private static String lightingDescription(String m, String type) {
      String lang = net.minecraft.client.Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase();
      String base = lang.contains("_") ? lang.substring(0, lang.indexOf('_')) : lang;
      String p = localizedMaterialName(m, base);
      String word = switch (base) {
         case "es" -> type.equals("Torch") ? "antorcha" : type.equals("Lantern") ? "farol" : "fogata";
         case "pt" -> type.equals("Torch") ? "tocha" : type.equals("Lantern") ? "lanterna" : "fogueira";
         case "fr" -> type.equals("Torch") ? "torche" : type.equals("Lantern") ? "lanterne" : "feu de camp";
         case "it" -> type.equals("Torch") ? "torcia" : type.equals("Lantern") ? "lanterna" : "fuoco da campo";
         case "ru" -> type.equals("Torch") ? "\u0444\u0430\u043a\u0435\u043b" : type.equals("Lantern") ? "\u0444\u043e\u043d\u0430\u0440\u044c" : "\u043a\u043e\u0441\u0442\u0451\u0440";
         case "zh" -> type.equals("Torch") ? "\u706b\u628a" : type.equals("Lantern") ? "\u706f\u7b3c" : "\u8425\u706b";
         default -> type;
      };
      if (m.equals("voidshard")) return switch (base) {
         case "es" -> p + " " + word + ": no emite luz (nivel 0). Crea una zona oscura donde pueden aparecer criaturas hostiles. Col\u00f3cala como la versi\u00f3n normal.";
         case "pt" -> p + " " + word + ": n\u00e3o emite luz (n\u00edvel 0). Cria uma \u00e1rea escura onde criaturas hostis podem surgir. Coloque como a vers\u00e3o comum.";
         case "fr" -> p + " " + word + " : n\u2019\u00e9met aucune lumi\u00e8re (niveau 0). Cr\u00e9e une zone sombre o\u00f9 des cr\u00e9atures hostiles peuvent appara\u00eetre. Posez-le comme la version classique.";
         case "it" -> p + " " + word + ": non emette luce (livello 0). Crea un\u2019area buia in cui possono comparire creature ostili. Posizionala come la versione normale.";
         case "ru" -> p + " " + word + ": \u043d\u0435 \u0438\u0437\u043b\u0443\u0447\u0430\u0435\u0442 \u0441\u0432\u0435\u0442 (\u0443\u0440\u043e\u0432\u0435\u043d\u044c 0). \u0421\u043e\u0437\u0434\u0430\u0451\u0442 \u0442\u0451\u043c\u043d\u0443\u044e \u043e\u0431\u043b\u0430\u0441\u0442\u044c, \u0433\u0434\u0435 \u043c\u043e\u0433\u0443\u0442 \u043f\u043e\u044f\u0432\u043b\u044f\u0442\u044c\u0441\u044f \u0432\u0440\u0430\u0436\u0434\u0435\u0431\u043d\u044b\u0435 \u043c\u043e\u0431\u044b. \u0423\u0441\u0442\u0430\u043d\u0430\u0432\u043b\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u043a\u0430\u043a \u043e\u0431\u044b\u0447\u043d\u0430\u044f \u0432\u0435\u0440\u0441\u0438\u044f.";
         case "zh" -> p + word + "\uff1a\u4e0d\u53d1\u5149\uff08\u4eae\u5ea6\u7b49\u7ea7 0\uff09\uff0c\u53ef\u5236\u9020\u654c\u5bf9\u751f\u7269\u80fd\u591f\u751f\u6210\u7684\u9ed1\u6697\u533a\u57df\u3002\u653e\u7f6e\u65b9\u5f0f\u4e0e\u539f\u7248\u76f8\u540c\u3002";
         default -> p + " " + type + ": gives off no light (light level 0). It creates a dark area where hostile mobs can spawn. Place it like the vanilla version.";
      };
      if (m.equals("lonsdaleite")) return switch (base) {
         case "es" -> p + " " + word + ": funciona como una fogata normal. No hay antorchas ni faroles de lonsdale\u00edta.";
         case "pt" -> p + " " + word + ": funciona como uma fogueira comum. N\u00e3o h\u00e1 tochas nem lanternas de lonsdale\u00edta.";
         case "fr" -> p + " " + word + " : fonctionne comme un feu de camp classique. Il n\u2019existe ni torche ni lanterne en lonsdal\u00e9ite.";
         case "it" -> p + " " + word + ": funziona come un normale fuoco da campo. Non esistono torce o lanterne in lonsdaleite.";
         case "ru" -> p + " " + word + ": \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u043a\u0430\u043a \u043e\u0431\u044b\u0447\u043d\u044b\u0439 \u043a\u043e\u0441\u0442\u0451\u0440. \u0424\u0430\u043a\u0435\u043b\u043e\u0432 \u0438 \u0444\u043e\u043d\u0430\u0440\u0435\u0439 \u0438\u0437 \u043b\u043e\u043d\u0441\u0434\u0435\u0439\u043b\u0438\u0442\u0430 \u043d\u0435\u0442.";
         case "zh" -> p + word + "\uff1a\u529f\u80fd\u4e0e\u666e\u901a\u8425\u706b\u76f8\u540c\u3002\u6ca1\u6709\u6717\u65af\u4ee3\u5c14\u77f3\u706b\u628a\u6216\u706f\u7b3c\u3002";
         default -> p + " " + type + ": works like the vanilla campfire. Lonsdaleite has no torch or lantern.";
      };
      return switch (base) {
         case "es" -> p + " " + word + ": funciona como el bloque normal y emite luz del color de " + p + ".";
         case "pt" -> p + " " + word + ": funciona como o bloco comum e emite luz na cor de " + p + ".";
         case "fr" -> p + " " + word + " : fonctionne comme le bloc classique et \u00e9met une lumi\u00e8re de la couleur de " + p + ".";
         case "it" -> p + " " + word + ": funziona come il blocco normale ed emette luce del colore di " + p + ".";
         case "ru" -> p + " " + word + ": \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u043a\u0430\u043a \u043e\u0431\u044b\u0447\u043d\u044b\u0439 \u0431\u043b\u043e\u043a \u0438 \u0441\u0432\u0435\u0442\u0438\u0442\u0441\u044f \u0446\u0432\u0435\u0442\u043e\u043c \u043c\u0430\u0442\u0435\u0440\u0438\u0430\u043b\u0430 " + p + ".";
         case "zh" -> p + word + "\uff1a\u529f\u80fd\u4e0e\u539f\u7248\u65b9\u5757\u76f8\u540c\uff0c\u5e76\u53d1\u51fa " + p + " \u989c\u8272\u7684\u5149\u3002";
         default -> p + " " + type + ": works like the vanilla block and glows in " + p + "'s color.";
      };
   }

   private static String localizedMaterialName(String material, String base) {
      return switch (base) {
         case "es" -> switch (material.toLowerCase()) { case "arcanite" -> "arcanita"; case "argentite" -> "argentita"; case "chromite" -> "cromita"; case "enderium" -> "enderium"; case "fulgurite" -> "fulgurita"; case "ilmenite" -> "ilmenita"; case "lonsdaleite" -> "lonsdale\u00edta"; case "nyxium" -> "nyxium"; case "osmium" -> "osmio"; case "painite" -> "painita"; case "phosgene" -> "fosgeno"; case "scheelite" -> "scheelita"; case "solarite" -> "solarita"; case "sperrylite" -> "sperrylita"; case "subspace" -> "subespacio"; case "voidshard" -> "esquirla del vac\u00edo"; case "zephyrite" -> "zefirita"; default -> pretty(material); };
         case "pt" -> switch (material.toLowerCase()) { case "arcanite" -> "arcanita"; case "argentite" -> "argentita"; case "chromite" -> "cromita"; case "enderium" -> "enderium"; case "fulgurite" -> "fulgurita"; case "ilmenite" -> "ilmenita"; case "lonsdaleite" -> "lonsdale\u00edta"; case "nyxium" -> "nyxium"; case "osmium" -> "\u00f3smio"; case "painite" -> "painita"; case "phosgene" -> "fosg\u00eanio"; case "scheelite" -> "scheelita"; case "solarite" -> "solarita"; case "sperrylite" -> "sperrylita"; case "subspace" -> "subespa\u00e7o"; case "voidshard" -> "estilha\u00e7o do vazio"; case "zephyrite" -> "zefirita"; default -> pretty(material); };
         case "fr" -> switch (material.toLowerCase()) { case "arcanite" -> "arcanite"; case "argentite" -> "argentite"; case "chromite" -> "chromite"; case "enderium" -> "enderium"; case "fulgurite" -> "fulgurite"; case "ilmenite" -> "ilm\u00e9nite"; case "lonsdaleite" -> "lonsdal\u00e9ite"; case "nyxium" -> "nyxium"; case "osmium" -> "osmium"; case "painite" -> "painite"; case "phosgene" -> "phosg\u00e8ne"; case "scheelite" -> "scheelite"; case "solarite" -> "solarite"; case "sperrylite" -> "sperrylite"; case "subspace" -> "sous-espace"; case "voidshard" -> "\u00e9clat du vide"; case "zephyrite" -> "z\u00e9phyrite"; default -> pretty(material); };
         case "it" -> switch (material.toLowerCase()) { case "arcanite" -> "arcanite"; case "argentite" -> "argentite"; case "chromite" -> "cromite"; case "enderium" -> "enderium"; case "fulgurite" -> "fulgurite"; case "ilmenite" -> "ilmenite"; case "lonsdaleite" -> "lonsdaleite"; case "nyxium" -> "nyxium"; case "osmium" -> "osmio"; case "painite" -> "painite"; case "phosgene" -> "fosgene"; case "scheelite" -> "scheelite"; case "solarite" -> "solarite"; case "sperrylite" -> "sperrylite"; case "subspace" -> "sottospazio"; case "voidshard" -> "scheggia del vuoto"; case "zephyrite" -> "zefirite"; default -> pretty(material); };
         case "ru" -> switch (material.toLowerCase()) { case "arcanite" -> "\u0430\u0440\u043a\u0430\u043d\u0438\u0442"; case "argentite" -> "\u0430\u0440\u0433\u0435\u043d\u0442\u0438\u0442"; case "chromite" -> "\u0445\u0440\u043e\u043c\u0438\u0442"; case "enderium" -> "\u044d\u043d\u0434\u0435\u0440\u0438\u0443\u043c"; case "fulgurite" -> "\u0444\u0443\u043b\u044c\u0433\u0443\u0440\u0438\u0442"; case "ilmenite" -> "\u0438\u043b\u044c\u043c\u0435\u043d\u0438\u0442"; case "lonsdaleite" -> "\u043b\u043e\u043d\u0441\u0434\u0435\u0439\u043b\u0438\u0442"; case "nyxium" -> "\u043d\u0438\u043a\u0441\u0438\u0443\u043c"; case "osmium" -> "\u043e\u0441\u043c\u0438\u0439"; case "painite" -> "\u043f\u0435\u0439\u043d\u0438\u0442"; case "phosgene" -> "\u0444\u043e\u0441\u0433\u0435\u043d"; case "scheelite" -> "\u0448\u0435\u0435\u043b\u0438\u0442"; case "solarite" -> "\u0441\u043e\u043b\u044f\u0440\u0438\u0442"; case "sperrylite" -> "\u0441\u043f\u0435\u0440\u0440\u0438\u043b\u0438\u0442"; case "subspace" -> "\u043f\u043e\u0434\u043f\u0440\u043e\u0441\u0442\u0440\u0430\u043d\u0441\u0442\u0432\u043e"; case "voidshard" -> "\u043e\u0441\u043a\u043e\u043b\u043e\u043a \u043f\u0443\u0441\u0442\u043e\u0442\u044b"; case "zephyrite" -> "\u0437\u0435\u0444\u0438\u0440\u0438\u0442"; default -> pretty(material); };
         case "zh" -> switch (material.toLowerCase()) { case "arcanite" -> "\u5965\u672f\u77f3"; case "argentite" -> "\u8f89\u94f6\u77ff"; case "chromite" -> "\u94ec\u94c1\u77ff"; case "enderium" -> "\u672b\u5f71\u7d20"; case "fulgurite" -> "\u96f7\u51fb\u77f3"; case "ilmenite" -> "\u949b\u94c1\u77ff"; case "lonsdaleite" -> "\u516d\u65b9\u91d1\u521a\u77f3"; case "nyxium" -> "\u6697\u591c\u7d20"; case "osmium" -> "\u9507"; case "painite" -> "\u7ea2\u787c\u9499\u94dd\u77f3"; case "phosgene" -> "\u5149\u6c14"; case "scheelite" -> "\u767d\u94a8\u77ff"; case "solarite" -> "\u65e5\u8000\u77f3"; case "sperrylite" -> "\u7837\u94c2\u77ff"; case "subspace" -> "\u4e9a\u7a7a\u95f4"; case "voidshard" -> "\u865a\u7a7a\u788e\u7247"; case "zephyrite" -> "\u548c\u98ce\u77f3"; default -> pretty(material); };
         default -> pretty(material);
      };
   }

   private static String pretty(String material) {
      if (material == null || material.isEmpty()) return material;
      return Character.toUpperCase(material.charAt(0)) + material.substring(1);
   }

   public static final List<Chapter> CHAPTERS = new ArrayList<>();

   static {

      CHAPTERS.addAll(List.of(ORES_AND_MATERIALS, TOOLS_AND_COMBAT, REDSTONE, UTILITY, MISC));
   }
}

