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
         Page.withItems(
            "Special Arrows",
            List.of("useful_ores:scheelite_arrow", "useful_ores:arcanite_arrow", "useful_ores:nyxium_arrow"),
            "What they do: three arrow types built on the same plain vanilla Arrow entity as a normal arrow, but each with its own on-hit behavior instead of (or alongside) normal arrow damage.",
            "Scheelite Arrow: functionally a heavier vanilla arrow. It deals 1.5x the damage of a normal unenchanted bow shot and otherwise flies and drops exactly like a vanilla arrow.",
            "Arcanite Arrow: deals no direct damage of its own. Instead, whatever it hits is granted one persistent golden absorption heart that stays until it is spent soaking up damage.",
            "Nyxium Arrow: steals up to a full heart of the target's current health (it will never take a target below half a heart) and hands that health to the shooter as a golden absorption heart - a life-drain shot rather than a raw-damage one.",
            "How to use: nock and fire any of the three from any bow, exactly like a vanilla arrow - no special draw or charge-up is required.",
            "How to make: each arrow uses the same shape - one ingot, a stick, and a feather in a single column - swapping the ingot for that arrow's material. The recipe below shows the Nyxium Arrow; the Scheelite and Arcanite Arrows use the same layout with a Scheelite or Arcanite Ingot in place of the Nyxium Ingot."
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
      return Page.withItems(
            "Pickaxe Tier List",
            ids,
            "What this shows: every pickaxe in the mod, grouped by mining tier, from weakest to strongest. A pickaxe can mine anything at its own tier or below; it cannot mine anything above it.",
            "Tier 1 - Wood/Gold equivalent: Wooden Pickaxe, Golden Pickaxe. Mines coal, stone-type blocks and any block with no minimum tier.",
            "Tier 2 - Stone equivalent: Stone Pickaxe, Zephyrite Pickaxe, Argentite Pickaxe. Adds iron ore/block and the mod's stone-tier ores and blocks (Zephyrite, Argentite).",
            "Tier 3 - Iron equivalent: Iron Pickaxe, Phosgene Pickaxe, Fulgurite Pickaxe, Osmium Pickaxe, Sperrylite Pickaxe, Scheelite Pickaxe. Adds diamond ore/block, redstone, emerald, gold, and the mod's iron-tier ores and blocks (Phosgene, Fulgurite, Osmium, Sperrylite, Scheelite).",
            "Tier 4 - Diamond equivalent: Diamond Pickaxe, Chromite Pickaxe, Ilmenite Pickaxe. Adds obsidian, ancient debris, and the mod's diamond-tier ores and blocks (Chromite, Ilmenite).",
            "Tier 5 - Netherite/top tier: Netherite Pickaxe, Nyxium Pickaxe, Arcanite Pickaxe, Solarite Pickaxe, Enderium Pickaxe, Voidshard Pickaxe, Subspace Pickaxe, Lonsdaleite Pickaxe. This tier mines everything in the game, including the mod's diamond-tier ores/blocks and the special Nyxium, Arcanite, Solarite, Enderium, Voidshard, Lonsdaleite and Subspace ores/blocks.",
            "Special case: the Ancient Pedestal ignores this whole tier chart. It can only be broken by a Voidshard Pickaxe specifically - every other pickaxe, including Netherite, Nyxium, Arcanite, Solarite, Enderium, Subspace and Lonsdaleite, has the break cancelled outright. See the Ancient Pedestal page in the Utility chapter."
         ).hideItemNames();
   }

   private static Page attributedSubspaceGearPage() {
      return Page.withItems(
            "Attributed Subspace Gear",
            List.of("useful_ores:attributed_subspace_sword", "useful_ores:attributed_subspace_helmet",
               "useful_ores:attributed_subspace_pickaxe"),
            "What it does: an upgraded version of Subspace gear that permanently carries one vanilla potion effect, built in two steps - first attribute an ingot, then craft that ingot into the gear piece.",
            "Step 1 - attribute an ingot: place a Subspace Ingot and any filled vanilla potion together in a crafting grid (anywhere, everything else empty) to get one Attributed Subspace Ingot carrying that potion's effect. Every vanilla potion is supported, including Instant Health/Instant Damage, Fire Resistance, Water Breathing, Night Vision and every other brewable effect - not just a handful.",
            "Step 2 - craft the gear: attributed gear uses the exact same shapes as plain Subspace gear (see the Subspace Weapons & Tools and Armor Set pages), but every ingot slot must be filled with Attributed Subspace Ingots that all carry the SAME effect - mixing two different attributed effects in one craft fails. Weapons and tools also need their usual stick(s) in the usual spots; armor does not.",
            "Armor effect: wearing a complete four-piece attributed set (all four pieces sharing the same attribute) grants that potion effect permanently while worn - no duration to manage, it simply stays active as long as the full set is on.",
            "Weapon/tool effect: every successful hit with an attributed Sword, Axe, Pickaxe, Shovel or Hoe applies that effect for 10 seconds. Buffing effects (Strength, Speed, Regeneration and similar) are applied to you, the attacker; debuffing effects (Poison, Weakness, Slowness and similar) are applied to the target instead. Instant Health and Instant Damage are special-cased as an immediate heal or hit rather than a lingering effect.",
            "Note: instant effects (Instant Health, Instant Damage) can be attributed onto weapons/tools but are excluded from armor, since a permanent instant heal/damage while worn wouldn't make sense as a passive effect."
         )
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
      return Page.withItems(
         "Painite Fury",
         List.of(
            "useful_ores:painite_helmet", "useful_ores:painite_chestplate",
            "useful_ores:painite_leggings", "useful_ores:painite_boots",
            "useful_ores:painite_sword", "useful_ores:painite_pickaxe",
            "useful_ores:painite_axe", "useful_ores:painite_hoe",
            "useful_ores:painite_shovel", "useful_ores:painite_spear"
         ),
         "Health sacrifice: every worn Painite armor piece removes 1 maximum-health point (half a heart). Four pieces therefore leave a player with a maximum of 16 HP (8 hearts). If equipping armor would leave you above the new maximum, your current health is clamped to that cap; removing the armor restores the cap without free healing.",
         "Fury trigger: for players, Fury begins at 10 HP or less (5 hearts). While active, every worn Painite armor piece adds an extra 60% of that piece's normal defense. Painite's existing 60% low-health tool bonus remains active for the sword, pickaxe, axe, hoe and shovel, increasing their attack damage and block breaking speed. The spear keeps its vanilla spear mechanics; in Fury it also gains +60% attack damage and a 60% shorter attack cooldown, while using the Fury hand/inventory appearance.",
         "Visual change: at 5 hearts or below, the Painite armor swaps to its blue-crimson Fury material and the chest displays a small animated molten radial crack/core. In the player inventory, Painite armor items and all Painite weapons (including the spear) switch to matching blue-crimson Fury coloration in the player inventory and held-item rendering while the player is in Fury.",
         "Recovery: rising above 5 hearts immediately returns the armor and inventory tools to their normal Painite appearance. The health sacrifice is independent of the Fury trigger, so a full set still caps maximum health at 8 hearts even when Fury is inactive."
      ).hideItemNames();
   }

   private static List<Page> materialArmorGroupPages() {
      String[] materials = MATERIALS;
      List<Page> pages = new ArrayList<>();
      for (String m : materials) {
         String p = pretty(m);
         List<String> ids = List.of("useful_ores:" + m + "_helmet", "useful_ores:" + m + "_chestplate",
            "useful_ores:" + m + "_leggings", "useful_ores:" + m + "_boots");
         pages.add(Page.withItems(p + " Armor Set", ids,
            armorStats(m),
            "The four pieces above share the same material durability multiplier, enchantability, toughness and knockback resistance. Defense and durability are listed per piece.",
            armorSpecial(m))
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
         pages.add(Page.withItems(p + " Weapons & Tools", ids,
            toolStats(m),
            swordAbility(m))
            .withRecipes(List.of(swordRecipe(m), pickaxeRecipe(m), axeRecipe(m), hoeRecipe(m), shovelRecipe(m)))
            .hideItemNames());
      }
      return pages;
   }

   private static String armorStats(String m) {
      return switch (m) {
         case "chromite" -> "Helmet: 2 defense, 297 durability. Chestplate: 7 defense, 432 durability. Leggings: 5 defense, 405 durability. Boots: 2 defense, 351 durability. Enchantability 12; toughness 1.2; knockback resistance 15%.";
         case "enderium" -> "Helmet: 4 defense, 308 durability. Chestplate: 11 defense, 448 durability. Leggings: 7 defense, 420 durability. Boots: 3 defense, 364 durability. Enchantability 24; toughness 3.4; knockback resistance 18%.";
         case "nyxium" -> "Helmet: 3 defense, 374 durability. Chestplate: 8 defense, 544 durability. Leggings: 5 defense, 510 durability. Boots: 2 defense, 442 durability. Enchantability 15; toughness 2.2; knockback resistance 10%.";
         case "phosgene" -> "Helmet: 2 defense, 198 durability. Chestplate: 5 defense, 288 durability. Leggings: 3 defense, 270 durability. Boots: 1 defense, 234 durability. Enchantability 14; toughness 0.4; knockback resistance 10%.";
         case "arcanite" -> "Helmet: 2 defense, 319 durability. Chestplate: 7 defense, 464 durability. Leggings: 5 defense, 435 durability. Boots: 2 defense, 377 durability. Enchantability 32; toughness 2.2; knockback resistance 0%.";
         case "fulgurite" -> "Helmet: 2 defense, 286 durability. Chestplate: 7 defense, 416 durability. Leggings: 5 defense, 390 durability. Boots: 2 defense, 338 durability. Enchantability 16; toughness 1.8; knockback resistance 20%.";
         case "osmium" -> "Helmet: 2 defense, 330 durability. Chestplate: 8 defense, 480 durability. Leggings: 5 defense, 450 durability. Boots: 2 defense, 390 durability. Enchantability 20; toughness 1.5; knockback resistance 35%.";
         case "solarite" -> "Helmet: 3 defense, 330 durability. Chestplate: 10 defense, 480 durability. Leggings: 7 defense, 450 durability. Boots: 3 defense, 390 durability. Enchantability 20; toughness 3.0; knockback resistance 15%.";
         case "sperrylite" -> "Helmet: 2 defense, 231 durability. Chestplate: 6 defense, 336 durability. Leggings: 4 defense, 315 durability. Boots: 2 defense, 273 durability. Enchantability 29; toughness 0.3; knockback resistance 5%.";
         case "argentite" -> "Helmet: 2 defense, 209 durability. Chestplate: 5 defense, 304 durability. Leggings: 3 defense, 285 durability. Boots: 1 defense, 247 durability. Enchantability 18; toughness 0.2; knockback resistance 0%.";
         case "zephyrite" -> "Helmet: 1 defense, 154 durability. Chestplate: 3 defense, 224 durability. Leggings: 2 defense, 210 durability. Boots: 1 defense, 182 durability. Enchantability 8; toughness 0; knockback resistance 0%.";
         case "ilmenite" -> "Helmet: 3 defense, 418 durability. Chestplate: 8 defense, 608 durability. Leggings: 5 defense, 570 durability. Boots: 2 defense, 494 durability. Enchantability 10; toughness 3.2; knockback resistance 20%.";
         case "scheelite" -> "Helmet: 2 defense, 209 durability. Chestplate: 5 defense, 304 durability. Leggings: 3 defense, 285 durability. Boots: 1 defense, 247 durability. Enchantability 10; toughness 0.1; knockback resistance 15%.";
         case "voidshard" -> "Helmet: 4 defense, 330 durability. Chestplate: 13 defense, 480 durability. Leggings: 9 defense, 450 durability. Boots: 3 defense, 390 durability. Enchantability 27; toughness 3.8; knockback resistance 22%.";
         case "subspace" -> "Helmet: 5 defense, 682 durability. Chestplate: 15 defense, 992 durability. Leggings: 10 defense, 930 durability. Boots: 4 defense, 806 durability. Enchantability 34; toughness 4.0; knockback resistance 40%.";
         case "lonsdaleite" -> "Helmet: 4 defense, 638 durability. Chestplate: 13 defense, 928 durability. Leggings: 9 defense, 870 durability. Boots: 3 defense, 754 durability. Enchantability 27; toughness 3.8; knockback resistance 22%.";
         case "painite" -> "Helmet: 3 defense, 363 durability. Chestplate: 8 defense, 528 durability. Leggings: 6 defense, 495 durability. Boots: 3 defense, 429 durability. Enchantability 10; toughness 2.0; knockback resistance 0%.";
         default -> "Armor statistics are defined by the material configuration.";
      };
   }

   private static String armorSpecial(String m) {
      if (m.equals("osmium")) return "Special effect: the complete Osmium set pushes water away while submerged.";
      if (m.equals("lonsdaleite")) return "Special effect: each worn Lonsdaleite piece reduces explosion damage taken by 17.5%, stacking up to a 70% reduction with the full four-piece set. Losing a piece proportionally weakens the effect. All Lonsdaleite armor and tools are fully enchantable.";
      if (m.equals("painite")) return "Special effect: each worn piece reduces maximum health by 0.5 heart (1 HP), down to 8 hearts with the full set. At 5 hearts or below, Painite Fury activates: each worn piece adds 60% of that piece's defense again, the armor changes to its blue-crimson Fury appearance, and the chest shows an animated molten radial crack/core. The spear also gains 60% extra attack damage and 60% reduced attack cooldown while Fury is active.";
      return "Special effect: no additional full-set gameplay effect is registered for this armor material beyond its normal armor statistics.";
   }

   private static String toolStats(String m) {
      return switch (m) {
         case "chromite" -> "Material: 1650 durability, 8.0 damage bonus, 7.0 mining speed, enchantability 12. Sword: 15.0 damage, 1.0 attack speed. Pickaxe: 14.0 damage, 1.2 speed. Axe: 16.0 damage, 1.0 speed. Hoe: 8.0 damage, 2.0 speed. Shovel: 13.0 damage, 1.0 speed.";
         case "enderium" -> "Material: 2700 durability, 13.5 damage bonus, 20.0 mining speed, enchantability 26. Sword: 22.5 damage, 2.0 attack speed. Pickaxe: 21.5 damage, 1.8 speed. Axe: 23.5 damage, 1.6 speed. Hoe: 14.5 damage, 4.8 speed. Shovel: 19.5 damage, 1.6 speed.";
         case "nyxium" -> "Material: 2200 durability, 10.0 damage bonus, 8.0 mining speed, enchantability 15. Sword: 16.0 damage, 1.8 attack speed. Pickaxe: 15.0 damage, 1.2 speed. Axe: 17.0 damage, 1.2 speed. Hoe: 10.0 damage, 2.0 speed. Shovel: 14.0 damage, 1.0 speed.";
         case "phosgene" -> "Material: 650 durability, 6.0 damage bonus, 6.5 mining speed, enchantability 6. Sword: 11.0 damage, 1.4 attack speed. Pickaxe: 9.0 damage, 1.2 speed. Axe: 11.0 damage, 0.9 speed. Hoe: 6.0 damage, 3.0 speed. Shovel: 9.0 damage, 1.0 speed.";
         case "arcanite" -> "Material: 2150 durability, 11.5 damage bonus, 8.5 mining speed, enchantability 32. Sword: 16.5 damage, 1.4 attack speed. Pickaxe: 15.5 damage, 1.2 speed. Axe: 17.5 damage, 1.0 speed. Hoe: 11.5 damage, 2.0 speed. Shovel: 14.5 damage, 1.0 speed.";
         case "fulgurite" -> "Material: 1750 durability, 8.5 damage bonus, 7.5 mining speed, enchantability 16. Sword: 15.5 damage, 0.6 attack speed. Pickaxe: 14.5 damage, 1.0 speed. Axe: 16.5 damage, 0.4 speed. Hoe: 8.5 damage, 2.0 speed. Shovel: 13.5 damage, 0.8 speed.";
         case "osmium" -> "Material: 1900 durability, 8.5 damage bonus, 6.0 mining speed, enchantability 22. Sword: 13.5 damage, 1.6 attack speed. Pickaxe: 12.5 damage, 1.2 speed. Axe: 14.5 damage, 1.0 speed. Hoe: 8.5 damage, 2.0 speed. Shovel: 11.5 damage, 1.0 speed.";
         case "solarite" -> "Material: 2400 durability, 12.0 damage bonus, 15.0 mining speed, enchantability 24. Sword: 17.0 damage, 1.8 attack speed. Pickaxe: 16.0 damage, 1.4 speed. Axe: 18.0 damage, 1.2 speed. Hoe: 12.0 damage, 4.5 speed. Shovel: 15.0 damage, 1.2 speed.";
         case "sperrylite" -> "Material: 700 durability, 7.0 damage bonus, 6.5 mining speed, enchantability 29. Sword: 13.0 damage, 1.6 attack speed. Pickaxe: 8.0 damage, 1.2 speed. Axe: 11.0 damage, 1.0 speed. Hoe: 7.0 damage, 1.0 speed. Shovel: 8.0 damage, 1.0 speed.";
         case "argentite" -> "Material: 380 durability, 6.5 damage bonus, 5.0 mining speed, enchantability 18. Sword: 10.5 damage, 2.2 attack speed. Pickaxe: 9.5 damage, 1.2 speed. Axe: 11.5 damage, 0.9 speed. Hoe: 6.5 damage, 3.0 speed. Shovel: 9.5 damage, 1.0 speed.";
         case "zephyrite" -> "Material: 190 durability, 4.0 damage bonus, 5.0 mining speed, enchantability 9. Sword: 7.0 damage, 1.6 attack speed. Pickaxe: 6.0 damage, 1.2 speed. Axe: 9.0 damage, 0.8 speed. Hoe: 4.0 damage, 2.0 speed. Shovel: 6.0 damage, 1.0 speed.";
         case "ilmenite" -> "Material: 2600 durability, 9.0 damage bonus, 7.0 mining speed, enchantability 10. Sword: 15.0 damage, 1.4 attack speed. Pickaxe: 14.0 damage, 1.2 speed. Axe: 16.0 damage, 1.0 speed. Hoe: 10.0 damage, 2.0 speed. Shovel: 13.0 damage, 1.0 speed.";
         case "scheelite" -> "Material: 900 durability, 6.5 damage bonus, 6.0 mining speed, enchantability 10. Sword: 10.5 damage, 1.6 attack speed. Pickaxe: 9.5 damage, 1.2 speed. Axe: 11.5 damage, 0.9 speed. Hoe: 6.5 damage, 3.0 speed. Shovel: 9.5 damage, 1.0 speed.";
         case "voidshard" -> "Material: 3000 durability, 15.0 damage bonus, 25.0 mining speed, enchantability 28. Sword: 26.0 damage, 2.2 attack speed. Pickaxe: 24.0 damage, 2.0 speed. Axe: 28.0 damage, 1.8 speed. Hoe: 15.0 damage, 5.0 speed. Shovel: 23.0 damage, 1.8 speed.";
         case "subspace" -> "Material: 6400 durability, 16.0 damage bonus, 32.0 mining speed, enchantability 34 - the highest raw stats of any material in the mod. Sword: 31.0 damage, 2.6 attack speed. Pickaxe: 29.0 damage, 2.4 speed. Axe: 33.0 damage, 2.2 speed. Hoe: 18.0 damage, 5.2 speed. Shovel: 27.0 damage, 2.2 speed.";
         case "lonsdaleite" -> "Material: 6200 durability, 15.0 damage bonus, 25.0 mining speed, enchantability 28. Sword: 26.0 damage, 2.2 attack speed. Pickaxe: 24.0 damage, 2.0 speed. Axe: 28.0 damage, 1.8 speed. Hoe: 15.0 damage, 5.0 speed. Shovel: 23.0 damage, 1.8 speed.";
         case "painite" -> "Material: 1561 durability, 3.0 damage bonus, 8.0 mining speed, enchantability 10. Sword: 6.0 damage, 1.6 attack speed. Pickaxe: 4.0 damage, 1.2 speed. Axe: 8.0 damage, 1.0 speed. Hoe: 0.0 damage, 4.0 speed. Shovel: 4.5 damage, 1.0 speed.";
         default -> "Tool statistics are defined by the material configuration.";
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
         case "nyxium" -> "Special ability: every successful sword hit applies Wither for 3 seconds.";
         case "phosgene" -> "Special ability: every successful sword hit applies Poison for 3 seconds.";
         case "fulgurite" -> "Special ability: a successful sword hit grants the attacker a brief Speed effect.";
         case "osmium" -> "Special ability: every successful sword hit applies Slowness to the target.";
         case "sperrylite" -> "Special ability: every successful sword hit applies Weakness to the target.";
         case "scheelite" -> "Special ability: successful hits apply strong Slowness briefly, staggering the target.";
         case "painite" -> "Special ability: when your health is at or below 5 hearts, holding a Painite tool increases its attack damage and block breaking speed by 60%. This bonus applies to Painite swords, pickaxes, axes, hoes and shovels.";
         default -> "Special ability: no additional on-hit status effect is registered for this sword.";
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

      Page.withItems(
            "Chromite Piston",
            List.of("useful_ores:chromite_piston", "useful_ores:chromite_sticky_piston"),
            "What it does: a Chromite-reinforced piston and sticky piston that behave exactly like their vanilla counterparts, with one key upgrade - they can push (and, for the sticky version, pull back) Obsidian and Crying Obsidian, which vanilla pistons can never move.",
            "How to use: place and wire it up like a normal piston or sticky piston. Everything about its extend/retract timing, redstone triggering and structure limits is identical to vanilla, except that Obsidian and Crying Obsidian are treated as pushable blocks instead of being hard-blocked.",
            "Limits: this exception is specific to Obsidian and Crying Obsidian. Blocks that are unpushable for other reasons (such as Respawn Anchors and Reinforced Deepslate) remain unpushable, and all of vanilla's other piston rules - block count limits, unmovable block entities, and so on - still apply in full.",
            "How to make: craft the base piston from Planks, Deepslate, Redstone and a Chromite Ingot in the shape below. Combine a Chromite Piston with a Slime Ball to upgrade it into the sticky variant."
         )
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

      Page.withItems(
            "Wireless Redstone Relay",
            List.of("useful_ores:wireless_redstone_relay"),
            "What it does: The Wireless Redstone Relay lets you move a redstone signal between separate locations without running a redstone-dust line between them. Each relay has an input side and an output side. The input is read from the side opposite the relay's facing direction, while the facing side can emit a full-strength redstone signal when the relay is powered. Relays can work as simple pairs or as part of larger relay networks.",
            "Basic linking: Place the relays on solid blocks and make sure both are in the same dimension and no more than 100 blocks apart. Right-click the first relay with an empty hand to select it. Then right-click the second relay to complete the link. The relay's linking feedback shows when a relay is waiting for a second selection. In the simplest setup, the second relay follows the first relay's redstone input, so you can put a redstone switch, torch or other source at one end and use the remote relay as the output at the other end.",
            "Canceling a selection: If you selected a relay but changed your mind, press C to cancel the pending selection. This only cancels the current linking selection; it does not remove an existing connection. C is the dedicated shortcut for leaving the selection step. Shift-right-click is used for changing relay modes or mesh settings, so use C when you only want to cancel a pending link.",
            "SINGLE mode: SINGLE is the straightforward one-to-one mode. Use it when one relay should follow another relay. A typical example is a hidden redstone input in a control room connected to a lamp, piston door or machine somewhere else. Select the first relay, then the second relay to pair them. When you want another separate pair, start a new selection with the next relay.",
            "MULTI mode: MULTI turns a relay into a hub that can send its signal to several receivers. First, shift-right-click the relay until it is in MULTI mode. Then right-click the hub to select it, followed by a receiver relay. The receiver is added to the hub. After adding one receiver, the hub remains the active selection, so you can right-click more receivers one after another. Right-clicking an already connected receiver toggles that receiver off, making it easy to build or edit a fan-out network.",
            "CONNECTION mode: CONNECTION is designed for chaining and bus-style layouts where relays act as connection points between parts of a larger redstone system. It uses the same hub-and-receiver style of linking, but is useful when you want an intermediate relay to connect one part of a larger build to another. For example, a central control relay can feed an intermediate relay, and that relay can then feed several remote parts of the build. Keep every individual connection within the 100-block, same-dimension limit.",
            "Multiple upstreams: A receiver can have more than one upstream hub. When it has at least two upstream sources, shift-right-clicking the receiver cycles the way their signals are combined. OR means the receiver turns on when ANY upstream relay is on. AND means the receiver turns on only when EVERY upstream relay is on. LATEST follows the upstream whose signal changed most recently. For example, OR lets two separate switches control one lamp, AND makes two conditions work together, and LATEST lets the most recently changed controller take priority.",
            "Changing settings: Shift-right-click a relay to cycle SINGLE → MULTI → CONNECTION. When a relay has at least two upstream hubs, shift-right-clicking it can instead cycle the mesh rule OR → AND → LATEST. A pending link selection takes priority for cancellation: press C to cancel it. After cancelling, you can use shift-right-click normally to change the relay's mode or mesh rule.",
            "Useful examples: (1) Remote switch: place Relay A near a switch and Relay B near a lamp, then link A to B in SINGLE mode. (2) One switch, many outputs: put a source on a MULTI hub and connect several receivers so one signal can control multiple locations. (3) Two-condition system: connect two upstream relays to one receiver and choose AND so both inputs must be active. (4) Multiple controllers: connect two upstream relays and choose LATEST when you want the most recently changed controller to determine the receiver's state. Larger builds can combine branches and chains.",
            "Limits and tips: Relay connections must be in the same dimension and within 100 blocks of each other. Relays do not require a visible redstone line between connected locations, but each relay still needs normal redstone input/output wiring for the part of the circuit it controls. The input side is opposite the facing direction, and the output comes from the facing side. If a network is not behaving as expected, check the relay mode first, then check its upstream connections, and finally check OR/AND/LATEST when multiple upstream sources are involved.",
            "Obtaining: Craft the Wireless Redstone Relay with the recipe shown below. It is a placeable utility block and does not require a special world-generation condition to obtain or use."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:redstone_torch", "minecraft:redstone", "minecraft:redstone_torch",
            "minecraft:copper_ingot", "useful_ores:raw_zephyrite", "minecraft:copper_ingot",
            "minecraft:stone", "minecraft:stone", "minecraft:stone"
         }, "useful_ores:wireless_redstone_relay", 1)),

      Page.withItems(
            "Redstone Clock",
            List.of("useful_ores:redstone_clock"),
            "What it does: A self-contained redstone oscillator. It repeatedly toggles its output between OFF and full-strength 15, without needing a feedback loop of repeaters or torches.",
            "How to use: Place it on a solid block. The arrow/facing side is the output. Right-click with an empty hand to cycle its period; the selected period restarts from OFF. The eight periods are 2, 4, 10, 20, 40, 60, 100 and 200 game ticks (0.1, 0.2, 0.5, 1, 2, 3, 5 and 10 seconds per complete ON/OFF cycle).",
            "Control and automation: The clock does not require a redstone input. Use its output to drive dust, lamps, gates, relays or other redstone machines. Right-clicking is the only mode control; its current period is also represented by the block state/model.",
            "Obtaining: Craft it using the recipe below. It is not a naturally generated block."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:raw_zephyrite", "minecraft:redstone_torch", "useful_ores:raw_zephyrite",
            "minecraft:redstone_torch", "minecraft:clock", "minecraft:redstone_torch",
            "useful_ores:raw_zephyrite", "minecraft:redstone_torch", "useful_ores:raw_zephyrite"
         }, "useful_ores:redstone_clock", 1)),

      Page.withItems(
            "AND Gate",
            List.of("useful_ores:and_gate"),
            "What it does: A two-input Boolean gate. Output is ON only when input A AND input B are both powered. It has a one-tick response delay.",
            "How to use: Place the gate on a solid block. Its facing direction is the output direction. The two inputs are the left and right sides relative to that facing direction. Feed either redstone dust or another redstone source into those sides.",
            "Logic: 0/0 → 0, 0/1 → 0, 1/0 → 0, 1/1 → 1. The output is a full-strength redstone signal, so it can directly drive downstream components.",
            "Obtaining: Craft the gate with the recipe below. It is a crafted logic component rather than a naturally generated block."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:quartz", "minecraft:redstone_torch", "minecraft:quartz",
            "minecraft:redstone_torch", "useful_ores:raw_zephyrite", "minecraft:redstone_torch",
            "minecraft:quartz", "minecraft:redstone_torch", "minecraft:quartz"
         }, "useful_ores:and_gate", 1)),

      Page.withItems(
            "NAND Gate",
            List.of("useful_ores:nand_gate"),
            "What it does: The inverse of AND. It stays ON except when both input A and input B are powered.",
            "How to use: Place it on a solid block. The facing side is the output; the two lateral sides are inputs A and B. Changes are evaluated with a one-tick response delay.",
            "Logic: 0/0 → 1, 0/1 → 1, 1/0 → 1, 1/1 → 0. NAND is functionally complete, so complex logic can be constructed from NAND gates alone.",
            "Obtaining: Craft it with the recipe below."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:quartz", "minecraft:redstone_torch", "minecraft:quartz",
            "minecraft:redstone_torch", "minecraft:copper_ingot", "minecraft:redstone_torch",
            "minecraft:quartz", "minecraft:redstone_torch", "minecraft:quartz"
         }, "useful_ores:nand_gate", 1)),

      Page.withItems(
            "OR Gate",
            List.of("useful_ores:or_gate"),
            "What it does: A two-input gate whose output is ON whenever at least one input is powered.",
            "How to use: Place it on a solid block. The facing side emits the output; the left and right sides relative to facing are inputs A and B. It updates one game tick after an input change.",
            "Logic: 0/0 → 0, 0/1 → 1, 1/0 → 1, 1/1 → 1. Use it when several independent conditions should activate the same machine.",
            "Obtaining: Craft it with the recipe below."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:redstone_torch", "minecraft:quartz", "minecraft:redstone_torch",
            "minecraft:quartz", "useful_ores:raw_zephyrite", "minecraft:quartz",
            "minecraft:redstone_torch", "minecraft:quartz", "minecraft:redstone_torch"
         }, "useful_ores:or_gate", 1)),

      Page.withItems(
            "NOR Gate",
            List.of("useful_ores:nor_gate"),
            "What it does: The inverse of OR. It produces a signal only when both inputs are OFF.",
            "How to use: Place it on a solid block with its output facing the next circuit stage. Feed the two lateral input sides from redstone sources. The gate responds one game tick after a change.",
            "Logic: 0/0 → 1, 0/1 → 0, 1/0 → 0, 1/1 → 0. It is useful for detecting the condition that none of two inputs is active.",
            "Obtaining: Craft it with the recipe below."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:redstone_torch", "minecraft:quartz", "minecraft:redstone_torch",
            "minecraft:quartz", "minecraft:copper_ingot", "minecraft:quartz",
            "minecraft:redstone_torch", "minecraft:quartz", "minecraft:redstone_torch"
         }, "useful_ores:nor_gate", 1)),

      Page.withItems(
            "XOR Gate",
            List.of("useful_ores:xor_gate"),
            "What it does: A two-input exclusive-OR gate. It outputs ON when exactly one input is powered.",
            "How to use: Place it on a solid block; the facing side is output and the two lateral sides are inputs. It updates after a one-tick logic delay.",
            "Logic: 0/0 → 0, 0/1 → 1, 1/0 → 1, 1/1 → 0. This makes XOR useful for comparing two conditions or building selector/control circuits.",
            "Obtaining: Craft it with the recipe below."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:copper_ingot", "minecraft:quartz", "minecraft:copper_ingot",
            "minecraft:quartz", "useful_ores:raw_zephyrite", "minecraft:quartz",
            "minecraft:copper_ingot", "minecraft:quartz", "minecraft:copper_ingot"
         }, "useful_ores:xor_gate", 1)),

      Page.withItems(
            "NOT Gate",
            List.of("useful_ores:not_gate"),
            "What it does: A one-input inverter. It outputs ON when its input is OFF and outputs OFF when its input is ON.",
            "How to use: Place it on a solid block. The single input is the left side relative to the gate's facing direction; the facing side is the output. It responds one game tick after an input change.",
            "Logic: 0 → 1 and 1 → 0. Because it inverts a redstone condition, it is useful for creating active-low control signals and negating sensor/clock states.",
            "Obtaining: Craft it with the recipe below."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:quartz", null, null,
            "minecraft:redstone_torch", null, null,
            "useful_ores:raw_zephyrite", null, null
         }, "useful_ores:not_gate", 1)),

      Page.withItems(
            "Argentite Filter",
            List.of("useful_ores:argentite_filter"),
            "What it does: A five-slot filtered hopper. Unlike a normal hopper, an empty filter slot accepts nothing; each non-empty slot defines the exact item and component combination that may enter that slot.",
            "How to use: Open the filter and place one or more reference stacks into its five slots. Those placed items become the whitelist. The reference quantity acts as a protected reserve: automated extraction can move only surplus items above that reserved amount. Items placed manually can still be removed by the player.",
            "Automation behavior: It pulls matching items from above and pushes surplus matching items toward the block it faces, using hopper-like transfer timing. A hopper or compatible inventory can therefore feed it from above and receive sorted output from the facing side. Use separate slots for separate allowed item types.",
            "Obtaining: Craft it with the recipe below. It is a crafted automation block and does not require world generation."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:argentite_ingot", null, "useful_ores:argentite_ingot",
            "useful_ores:argentite_ingot", "minecraft:chest", "useful_ores:argentite_ingot",
            null, "useful_ores:argentite_ingot", null
         }, "useful_ores:argentite_filter", 1)),

      Page.withItems(
            "Fulgurite Electric Trap",
            List.of("useful_ores:fulgurite_electric_trap"),
            "What it does: A dormant electric defense field that must first be awakened by lightning during a thunderstorm. Once unlocked, it damages living entities in a square field and pulls them toward the center.",
            "How to activate: Place it on a solid block with sky access above it and wait for a thunderstorm. During a storm the trap has a chance each tick to receive a lightning strike; the strike unlocks the trap permanently. Breaking and re-placing an unlocked trap preserves its unlocked state, but its range resets to 1.",
            "Range and control: The initial range is 1, covering a 3×3 area. Right-click with a Fulgurite Ingot to increase the range to 2 (5×5) and then 3 (7×7); each upgrade consumes one ingot unless in Creative. Range 3 is the maximum. Any redstone signal touching the trap suppresses the damaging field while preserving the unlocked state. Right-click without an ingot reports its status.",
            "Effect: The field periodically deals 2 damage to players and 4 damage to mobs, with mobs additionally having their horizontal movement suppressed. Victims are pulled toward the trap, and electric particles show the active area.",
            "Obtaining: Craft it with the recipe below. Its lightning activation is a gameplay mechanic, not a crafting ingredient."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:copper_ingot", null, null,
            "useful_ores:fulgurite_ingot", "useful_ores:raw_fulgurite", "useful_ores:fulgurite_ingot",
            "useful_ores:argentite_ingot", "useful_ores:raw_zephyrite", "useful_ores:argentite_ingot"
         }, "useful_ores:fulgurite_electric_trap", 1))
   ));

   private static final Chapter UTILITY = category("Utility", "useful_ores:nyxiumnite_staff", List.of(

      Page.withItems(
            "Catalytic Vial",
            List.of("useful_ores:sperrylite_catalytic_vial", "useful_ores:sperrylite_catalytic_vial_splash",
               "useful_ores:sperrylite_catalytic_vial_lingering"),
            "What it does: a reusable brewing vessel that fuses two separate potions' effects into one vial, then can be upgraded through the same splash/lingering chain vanilla potions use.",
            "How to brew the base vial: place one empty Catalytic Vial and two filled vanilla potions (any combination, any order) in the three bottle slots of a brewing stand, put a Sperrylite Nugget in the ingredient slot, and fuel the stand with blaze powder as normal. Brewing takes 20 seconds, the same as a vanilla potion. When it finishes, both potions are drained to empty glass bottles, the nugget is consumed, and the vial is filled with the combined effects of both potions at once - it is not limited to copying a single potion.",
            "How to upgrade to splash/lingering: brew a filled vial with gunpowder in the ingredient slot to convert it into a Splash Catalytic Vial, then brew that with dragon's breath to convert it into a Lingering Catalytic Vial - exactly the same two-step upgrade path as vanilla splash and lingering potions. Any bottle slot holding a convertible vial converts; slots holding something else are left alone.",
            "How to use: drink the base vial like a potion for its combined effects on yourself, throw the splash vial to apply the combined effects to entities in the area, or throw the lingering vial to leave a lingering cloud - the vial's icon and tooltip automatically show every effect it carries.",
            "Example combinations: brew a Potion of Fire Resistance with a Potion of Water Breathing for one vial that keeps you safe crossing lava AND diving lava-adjacent caves at once. Brew a Potion of Strength with a Potion of Swiftness for a melee-rush vial that hits harder and closes distance faster. Brew a Potion of Regeneration with a Potion of Absorption for a tanky vial to drink before a boss fight. Any two potions can be paired this way - these are just starting points.",
            "How to make: craft an empty vial from a Glass Bottle surrounded by eight Sperrylite Nuggets, shown below, then brew it in a brewing stand as shown in the brewing diagrams."
         )
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

      Page.withItems(
            "Arcanite XP Jar",
            List.of("useful_ores:arcanite_xp_jar"),
            "What it does: a placeable glass jar that stores up to 1395 experience points (about 30 vanilla levels) as a portable, bankable XP reserve instead of losing it all on death.",
            "How to use: place the jar on top of a block, slab, or closed trapdoor. Shift-right-click it to pull experience from you into the jar (filling it up to its remaining room, or until you run out of XP). Right-click it with an empty interaction to drain its stored XP back into you - unless you are holding or wearing a item with Mending, in which case draining the jar repairs that item instead of granting levels.",
            "Passive fill: standing near vanilla experience orbs while the jar has room lets it intercept some of that XP automatically, so it can top itself up passively while you grind mobs or smelt ore nearby.",
            "Durability and drops: the jar has no inventory GUI and is fragile like glass - breaking it (or removing its support block) drops it as an item that keeps whatever XP was stored inside, so moving a charged jar between bases is safe.",
            "How to make: craft it from an Arcanite Ingot, a Silver Core Arcanite Shard, and three Glass in the shaped recipe below."
         )
         .withRecipe(Recipe.crafting(new String[]{
            null, "useful_ores:arcanite_ingot", null,
            null, "useful_ores:silver_core_arcanite_shard", null,
            "minecraft:glass", "minecraft:glass", "minecraft:glass"
         }, "useful_ores:arcanite_xp_jar", 1)),

      Page.withItems(
            "Nyxiumnite Staff",
            List.of("useful_ores:nyxiumnite_staff"),
            "What it does: A high-tier staff that links to an Ancient Pedestal and launches a wormhole-opening projectile. The projectile is the staff's ranged action; the pedestal link is its special targeting state.",
            "How to use: First find an Ancient Pedestal. Right-click the pedestal with the staff to bind that staff to the pedestal. Right-click in the air to fire the projectile toward your crosshair. The staff has a 30-tick (1.5 second) firing cooldown.",
            "Control and linking: The linked pedestal is stored on the staff, so different staffs can hold different links. If the linked pedestal no longer exists, the staff cannot use that stored destination. The item tooltip reports whether it is linked and gives the stored coordinates.",
            "Obtaining: Craft the staff from the Volatile Nyxiumnite Core and Argentite shown below. The core itself is built in two crafting steps - Nyxium-Arcanite Core, then Volatile Nyxiumnite Core - see those two pages in this chapter for their exact recipes."
         )
         .withRecipe(Recipe.crafting(new String[]{
            null, "useful_ores:volatile_nyxiumnite_core", null,
            null, "useful_ores:argentite_ingot", null,
            null, "useful_ores:argentite_ingot", null
         }, "useful_ores:nyxiumnite_staff", 1)),

      Page.withItems(
            "Meteor Staff",
            List.of("useful_ores:meteor_staff"),
            "What it does: A ranged meteor launcher. Using it fires a meteorite projectile in the direction you are looking; the projectile is explosive and is intended for long-range combat or demolition.",
            "How to use: Hold the staff and right-click/use it. Aim before firing because the projectile inherits the player's view direction. Each shot applies a 100-tick (5 second) cooldown, so it is a deliberate heavy attack rather than a rapid-fire weapon.",
            "Progression: The staff is built around the Meteorite Core → Meteorite Core Shard → Volatile Meteorite Core chain. Dragon's Breath is used in the volatile upgrade stage.",
            "Obtaining: Craft the completed staff with the recipe below after making its volatile core."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:meteorite_block", "useful_ores:volatile_meteorite_core", "useful_ores:meteorite_block",
            "minecraft:blaze_rod", "minecraft:blaze_rod", "minecraft:blaze_rod",
            null, "minecraft:blaze_rod", null
         }, "useful_ores:meteor_staff", 1)),

      Page.withItems(
            "Nyxium-Arcanite Core",
            List.of("useful_ores:nyxium_arcanite_core"),
            "What it does: An intermediate crafting core that combines Nyxium, Arcanite, Voidshard and an Ender Eye into a compact high-energy component.",
            "How to obtain: It is crafted, not mined or found as a loose world item. Use the exact arrangement shown in the recipe below.",
            "Use: This core is the precursor to the Volatile Nyxiumnite Core - it has no direct use of its own, and is never right-click activated.",
            "Next step: Combine it with Dragon's Breath as shown on the Volatile Nyxiumnite Core page to create the staff's active power core."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:nyxium_ingot", "useful_ores:arcanite_ingot", "useful_ores:nyxium_ingot",
            "useful_ores:arcanite_ingot", "minecraft:ender_eye", "useful_ores:arcanite_ingot",
            "useful_ores:voidshard_ingot", "useful_ores:arcanite_ingot", "useful_ores:voidshard_ingot"
         }, "useful_ores:nyxium_arcanite_core", 1)),

      Page.withItems(
            "Volatile Nyxiumnite Core",
            List.of("useful_ores:volatile_nyxiumnite_core"),
            "What it does: The energized form of the Nyxium-Arcanite Core. It is the active power component used to assemble the Nyxiumnite Staff.",
            "How to make: Combine a Nyxium-Arcanite Core with Dragon's Breath using the shapeless recipe below. There is no world-generation source for this item.",
            "Use: Insert it into the Nyxiumnite Staff recipe. It is consumed during crafting and has no separate right-click action of its own.",
            "Progression tip: Treat the volatile core as the final intermediate stage of the Nyxiumnite crafting chain."
         )
         .withRecipe(Recipe.shapeless(
            List.of("useful_ores:nyxium_arcanite_core", "minecraft:dragon_breath"),
            "useful_ores:volatile_nyxiumnite_core", 1)),

      Page.withItems(
            "Meteorite Core Shard",
            List.of("useful_ores:meteorite_core_shard"),
            "What it does: The first crafted core component in the Meteor Staff crafting chain. It packages a Meteorite Core with Blaze Rods so the core can be energized later.",
            "How to make: Place a Meteorite Core between two Blaze Rods in the vertical arrangement shown below. The recipe yields one shard.",
            "Use: The shard is an intermediate component. Upgrade it with Dragon's Breath to obtain the Volatile Meteorite Core, then use that volatile core in the Meteor Staff recipe.",
            "Where to get the ingredients: Meteorite Core is dug out of a Crashed Meteorite (see that page in the Misc chapter for what the structure looks like and how to find one); Blaze Rods and Dragon's Breath are vanilla Nether resources."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:blaze_rod", null, null,
            "useful_ores:meteorite_core", null, null,
            "minecraft:blaze_rod", null, null
         }, "useful_ores:meteorite_core_shard", 1)),

      Page.withItems(
            "Volatile Meteorite Core",
            List.of("useful_ores:volatile_meteorite_core"),
            "What it does: An energized Meteorite Core Shard used as the power source of the Meteor Staff.",
            "How to make: Upgrade the Meteorite Core Shard with Dragon's Breath using the recipe shown on this page. The transformation is shapeless.",
            "Use: It is consumed in the Meteor Staff crafting recipe and is not itself a placeable or activatable machine.",
            "Progression: Make the Meteorite Core Shard first, then perform this volatile upgrade, then assemble the completed staff."
         )
         .withRecipe(Recipe.shapeless(
            List.of("useful_ores:meteorite_core_shard", "minecraft:dragon_breath"),
            "useful_ores:volatile_meteorite_core", 1)),

      Page.withItems(
            "Silver Core Arcanite Shard",
            List.of("useful_ores:silver_core_arcanite_shard"),
            "What it does: An intermediate Arcanite shard used as an ingredient in the Arcanite XP Jar recipe (see that page in this chapter) - not a standalone tool.",
            "How to make: Follow the shaped recipe shown below. The ingredients and their exact positions matter.",
            "Use: Keep it as a crafting intermediate; it has no separate right-click mode or GUI.",
            "Obtaining: It is crafted from the mod's materials rather than generated as an ore block."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:arcanite_nugget", "useful_ores:arcanite_nugget", "useful_ores:arcanite_nugget",
            "useful_ores:arcanite_nugget", "useful_ores:argentite_nugget", "useful_ores:arcanite_nugget",
            "useful_ores:arcanite_nugget", "useful_ores:arcanite_nugget", "useful_ores:arcanite_nugget"
         }, "useful_ores:silver_core_arcanite_shard", 1)),

      Page.withItems(
            "Dark Core",
            List.of("useful_ores:dark_core"),
            "What it does: A dark-energy crafting core consumed by the Nyxium Dark Barrier recipe (see that page in this chapter).",
            "How to make: Craft it using the recipe shown below. The recipe grid is the authoritative arrangement for the current mod version.",
            "Use: The Dark Core is consumed in the Nyxium Dark Barrier recipe. It is a component, not a placeable block or interactive machine.",
            "Obtaining: Crafted from the mod materials; there is no separate world-generation location required for the finished core."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:nyxium_ingot", "minecraft:ender_pearl", "useful_ores:nyxium_ingot",
            "minecraft:ender_pearl", "minecraft:ender_eye", "minecraft:ender_pearl",
            "useful_ores:voidshard_ingot", "minecraft:ender_pearl", "useful_ores:voidshard_ingot"
         }, "useful_ores:dark_core", 1)),

      Page.withItems(
            "Farseeker Crystal",
            List.of("useful_ores:farseeker_crystal"),
            "What it does: A rare crystal used to upgrade Subspace tools so they auto-collect mined blocks straight into your inventory (see the Subspace Weapons & Tools page).",
            "Where to find it: NOT craftable - it is a chest-loot-only item. It has a 7% chance to appear in an End City Treasure chest (the loot chests found on End Ship / End City structures), added as an extra pool alongside that chest's normal loot rather than replacing anything.",
            "How to get one: locate an End City (generates in the outer End islands, usually reachable by Elytra/bridging from the main island) and open its End Ship treasure chest. Keep opening End City chests if you don't get one on the first try - there's no guaranteed source, only the repeated 7% roll.",
            "Use: Treat it as an upgrade material for Subspace tools; it has no separate GUI or right-click activation of its own."
         ),

      Page.withItems(
            "Extruding Crystal Ore",
            List.of("useful_ores:extruding_crystal_ore"),
            "What it does: the ore block that drops Extruding Crystal Nuggets, the raw material behind the Extruding Crystal / Extruding Nether Star / Super Beacon chain.",
            "Where to find it: generates as small Overworld ore veins (size 4) from Y -64 up to Y 16, roughly one vein attempt per 2 chunks. There is no surface tell - dig or cave-explore through that height range like any other ore.",
            "Drops: breaking it drops Extruding Crystal Nuggets (quantity boosted by Fortune); mining with Silk Touch drops the ore block itself instead.",
            "How to use: mine the nuggets, then see the Extruding Crystal Nugget page for how to turn them into a full Extruding Crystal."
         ),

      Page.withItems(
            "Extruding Crystal Nugget",
            List.of("useful_ores:extruding_crystal_nugget"),
            "What it does: A small crystal component that drops from mining Extruding Crystal Ore. Nine of them compress into one Extruding Crystal.",
            "Where to find it: dropped by breaking Extruding Crystal Ore (see that page for where the ore generates); Fortune can increase the quantity obtained according to the ore drop system.",
            "How to use: Nine Extruding Crystal Nuggets can be compressed into one Extruding Crystal. The reverse is not an activation mechanic; the nugget is primarily a crafting material.",
            "Obtaining: Mine Extruding Crystal Ore with the appropriate tool, then use the nuggets in the 3×3 recipe shown on the Extruding Crystal page."
         ),

      Page.withItems(
            "Extruding Crystal",
            List.of("useful_ores:extruding_crystal"),
            "What it does: A complete crystal formed by compressing nine Extruding Crystal Nuggets. It is a key component of the Super Beacon progression.",
            "How to make: Fill the 3×3 crafting grid with nine Extruding Crystal Nuggets. The recipe yields one crystal.",
            "Use: Combine the Extruding Crystal with a vanilla Nether Star to create an Extruding Nether Star. That upgraded star is then used to power the Super Beacon.",
            "Obtaining: Compressed from Extruding Crystal Nuggets (see that page) rather than found directly as a finished crystal."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget",
            "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget",
            "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget", "useful_ores:extruding_crystal_nugget"
         }, "useful_ores:extruding_crystal", 1)),

      Page.withItems(
            "Extruding Nether Star",
            List.of("useful_ores:extruding_nether_star"),
            "What it does: A modified Nether Star carrying the Extruding Crystal upgrade required by the Super Beacon.",
            "How to make: Combine one vanilla Nether Star and one Extruding Crystal shapelessly, as shown below.",
            "Use: It is consumed as the central ingredient of the Super Beacon recipe. It has no separate right-click action.",
            "Obtaining: The vanilla Nether Star comes from the Wither; the Extruding Crystal is compressed from Extruding Crystal Nuggets mined from Extruding Crystal Ore (see those two pages in this chapter)."
         )
         .withRecipe(Recipe.shapeless(
            List.of("minecraft:nether_star", "useful_ores:extruding_crystal"),
            "useful_ores:extruding_nether_star", 1)),

      Page.withItems(
            "Heated Shard",
            List.of("useful_ores:heated_shard"),
            "What it does: A high-energy fusion material made by combining Voidshard and Solarite. It is an intermediate ingredient for advanced crafting and is fire-resistant.",
            "How to make: Arrange Voidshard Ingots and Solarite Ingots in the 3×3 pattern shown below. The center is intentionally empty; the exact pattern is shaped.",
            "Use: It is a material component rather than a machine or weapon, so there is no activation or mode control.",
            "Obtaining: Crafted from refined Voidshard and Solarite materials; obtain those from their respective ore/material chains."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:voidshard_ingot", "useful_ores:solarite_ingot", "useful_ores:voidshard_ingot",
            "useful_ores:solarite_ingot", null, "useful_ores:solarite_ingot",
            "useful_ores:voidshard_ingot", "useful_ores:solarite_ingot", "useful_ores:voidshard_ingot"
         }, "useful_ores:heated_shard", 1)),

      Page.withItems(
            "Meteor Bomb",
            List.of("useful_ores:meteor_bomb"),
            "What it does: A throwable explosive. Each crafting operation yields four bombs, and a thrown bomb produces a creeper-strength explosion capable of breaking blocks even underwater.",
            "How to use: Hold the bomb and use/right-click to throw it. The projectile detonates as an explosive rather than behaving like a placeable block. Treat it as a finite ranged demolition/combat item.",
            "How to make: Fill the surrounding eight crafting slots with Meteor Dust and place Gunpowder in the center, as shown below. The recipe yields four bombs.",
            "Where to get ingredients: Meteor Dust is crafted 4-at-a-time from 1 Meteorite Block (shapeless), and Meteorite Block itself is dug from a Crashed Meteorite structure (see the Misc chapter); Gunpowder is vanilla, dropped by Creepers and found in many dungeon/desert-temple chests."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:meteor_dust", "useful_ores:meteor_dust", "useful_ores:meteor_dust",
            "useful_ores:meteor_dust", "minecraft:gunpowder", "useful_ores:meteor_dust",
            "useful_ores:meteor_dust", "useful_ores:meteor_dust", "useful_ores:meteor_dust"
         }, "useful_ores:meteor_bomb", 4)),

      Page.withItems(
            "Phosgene Potion Powders",
            List.of("useful_ores:phosgene_raw_powder", "useful_ores:phosgene_potion_powder"),
            "What it does: Phosgene powder can be infused with a vanilla potion and spread onto any block as a temporary potion field.",
            "How to make Raw Phosgene Powder: Put Raw Phosgene in the four corners, a Slime Block in the center, Redstone Dust in three of the remaining side slots, and a Blaze Rod in the last side slot. The recipe yields 16 Raw Phosgene Powder.",
            "How to make a potion powder: Put one filled Potion in the center and eight Raw Phosgene Powder around it. The result is 2 Phosgene Potion Powder carrying that potion's effect and color. Each powder slot may contain a stack; one powder is consumed from each of the eight surrounding slots per craft.",
            "How to use: Right-click any block with the infused powder. The block gains a subtle colored hue and rising potion particles. Living entities standing over it receive the potion effect for three minutes. The treatment lasts 1 Minecraft day (24,000 ticks) before the block returns to normal."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:raw_phosgene", "minecraft:redstone", "useful_ores:raw_phosgene",
            "minecraft:redstone", "minecraft:slime_block", "minecraft:blaze_rod",
            "useful_ores:raw_phosgene", "minecraft:redstone", "useful_ores:raw_phosgene"
         }, "useful_ores:phosgene_raw_powder", 16)),
      Page.withItems(
            "Lonsdaleite Layer Glue",
            List.of("useful_ores:lonsdaleite_layer_glue"),
            "What it does: A reusable coating tool for applying the mod's Lonsdaleite layer to compatible blocks. It behaves like a tool with durability rather than a one-use crafting ingredient.",
            "How to make: Surround Raw Lonsdaleite with eight Slime Balls in the 3×3 pattern shown below. The resulting glue has 64 durability.",
            "How to use: Hold the glue and interact with a compatible target block to apply the Lonsdaleite layer. Because the glue has durability, repeated applications consume durability instead of deleting the tool immediately.",
            "Obtaining: Craft it from Raw Lonsdaleite and Slime Balls; there is no separate world-generation form of the glue."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:slime_ball", "minecraft:slime_ball", "minecraft:slime_ball",
            "minecraft:slime_ball", "useful_ores:raw_lonsdaleite", "minecraft:slime_ball",
            "minecraft:slime_ball", "minecraft:slime_ball", "minecraft:slime_ball"
         }, "useful_ores:lonsdaleite_layer_glue", 1)),

      Page.withItems(
            "Ancient Pedestal",
            List.of("useful_ores:ancient_pedestal"),
            "What it does: A special structure/block used by the Nyxiumnite Staff progression. The pedestal itself is not a conventional machine with a GUI; its important role is providing a persistent location that the staff can bind to.",
            "Where to find it: NOT craftable - Ancient Pedestals only generate pre-placed inside the Ancient Pedestal Chamber structure (see that page in this same chapter for exactly what the structure looks like, how rare it is, and what's in its loot chest). There is no recipe for the pedestal itself.",
            "How to use: Hold a Nyxiumnite Staff and right-click the pedestal. The staff records the pedestal's dimension and coordinates. The staff can then use that link for its wormhole-related interaction. A pedestal does not need to be manually configured with a menu.",
            "Breaking it: the pedestal cannot be broken by any tool except a Voidshard Pickaxe - every other tool has the break cancelled outright, even in Survival with a full-tier tool."
         ),

      Page.withItems(
            "Titanium Rail",
            List.of("useful_ores:titanium_rail"),
            "What it does: A reinforced ordinary minecart rail made from the mod's Titanium/Ilmenite material. It behaves as track infrastructure and supports higher-speed travel more safely than ordinary rail when used by the Solarite Battery Minecart.",
            "How to use: Place it like a normal rail and connect it into your track network. It can form straight and curved rail paths. The Solarite Battery Minecart has a special safe curve cap of 0.8 blocks/tick (16 blocks/s) on titanium curves, while its general cruise speed can be higher on straight sections.",
            "Control: Titanium Rail itself has no GUI or mode. Use normal minecart track geometry and, where a junction is required, place a Titanium Controller Rail to choose the branch.",
            "How to make: The shaped recipe below yields 16 rails. It uses Ilmenite Ingots and a Stick. Titanium is the refined material used by the mod for this rail family."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:ilmenite_ingot", null, "useful_ores:ilmenite_ingot",
            "useful_ores:ilmenite_ingot", "minecraft:stick", "useful_ores:ilmenite_ingot",
            "useful_ores:ilmenite_ingot", null, "useful_ores:ilmenite_ingot"
         }, "useful_ores:titanium_rail", 16)),

      Page.withItems(
            "Titanium Controller Rail",
            List.of("useful_ores:titanium_controller_rail"),
            "What it does: A three-way switch that directs a minecart toward LEFT, STRAIGHT or RIGHT. The controller is paired with the rail immediately ahead of it; the controller tile itself remains straight while the following rail is shaped for the selected branch.",
            "Placement: Place the controller so its facing direction is the straight-through direction. Build the left, straight and right branch rails from the controller. The switch can be used at an intersection to route carts without physically rebuilding the track.",
            "Control modes: Right-click the controller to toggle CYCLE and TABLE modes. In CYCLE mode, a redstone OFF→ON transition advances LEFT → STRAIGHT → RIGHT → LEFT. In TABLE mode, the switch follows redstone strength: 15=L, 14=S, 13=R, then the pattern repeats down through 1, with 0=L.",
            "Redstone control: A button, lever or dust line can provide the signal. CYCLE is edge-triggered, so holding a lever ON does not repeatedly advance the switch. TABLE reads the current 0–15 strength directly. Use the recipe below to craft six controllers."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:ilmenite_ingot", "minecraft:stick", "useful_ores:ilmenite_ingot",
            "useful_ores:ilmenite_ingot", "useful_ores:raw_zephyrite", "useful_ores:ilmenite_ingot",
            "useful_ores:ilmenite_ingot", "minecraft:redstone", "useful_ores:ilmenite_ingot"
         }, "useful_ores:titanium_controller_rail", 6)),

      Page.withItems(
            "Enderium Rail",
            List.of("useful_ores:enderium_rail"),
            "What it does: A paired teleport rail. A minecart that passes over one linked Enderium Rail is transferred to its paired Enderium Rail, allowing long-distance transport without a continuous track between endpoints.",
            "How to link: Place two Enderium Rails in the same dimension within 100 blocks. Right-click the first to select it, then right-click the second to pair them. A rail can have only one partner. If you link an already-linked rail, its previous link is cleared before the new pair is created. Shift-right-click cancels a pending selection.",
            "How to use: Build ordinary/titanium track into each endpoint so the cart can enter the Enderium Rail. When the cart rides across the linked rail, it is teleported to its paired endpoint. Linking is per pair; there is no multi-destination mode.",
            "How to make: Craft eight Enderium Rails using Enderium Ingots, Sticks and an Ender Pearl in the shaped recipe below. The rails are crafted infrastructure, not a naturally generated block."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:enderium_ingot", "minecraft:stick", "useful_ores:enderium_ingot",
            "useful_ores:enderium_ingot", "minecraft:ender_pearl", "useful_ores:enderium_ingot",
            "useful_ores:enderium_ingot", "minecraft:stick", "useful_ores:enderium_ingot"
         }, "useful_ores:enderium_rail", 8)),

      Page.withItems(
            "Solarite Rail",
            List.of("useful_ores:solarite_rail"),
            "What it does: A high-speed Solarite track that is always straight on the horizontal plane and is designed to work with the Solarite Battery Minecart. It has custom acceleration/launch behavior rather than vanilla Powered Rail behavior.",
            "Powering: A direct redstone signal from a neighboring block gives a minecart a controlled launch push. The rail also tracks day/night visually; the Solarite Battery Minecart can draw free solar power in daylight when it has sky access, while battery power is used when free power is unavailable.",
            "How to use: Place it into a rail line like normal track. It does not have a player-facing mode switch. Use a lever/button/dust signal beside the rail when you want the custom launch boost. The Solarite Battery Minecart can travel at its configured cruise speed, up to 1.6 blocks/tick (32 blocks/s), subject to curve safety rules.",
            "How to make: The shaped recipe below yields 16 Solarite Rails from Solarite Ingots and Sticks. It is a crafted rail, not a world-generation block."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:solarite_ingot", null, "useful_ores:solarite_ingot",
            "useful_ores:solarite_ingot", "minecraft:stick", "useful_ores:solarite_ingot",
            "useful_ores:solarite_ingot", null, "useful_ores:solarite_ingot"
         }, "useful_ores:solarite_rail", 16)),

      Page.withItems(
            "Solarite Furnace",
            List.of("useful_ores:solarite_furnace_component"),
            "What it does: A 2×2×2 multiblock smelting furnace designed to process recipes five times faster than a vanilla Blast Furnace. It can use direct sunlight or stored Solar Battery energy.",
            "How to build: Craft one Solarite Furnace Component using the recipe below. Assemble the furnace as a 2×2×2 structure from eight compatible components, then use the multiblock as a single machine. The exact component arrangement is checked by the mod; incomplete structures will not operate as the furnace.",
            "How to use: Put smeltable input into the furnace inventory through its interface. When sunlight is available, the furnace can run from solar power. When sunlight is unavailable, provide a charged Solar Battery through the supported battery interface/slot. The machine handles the accelerated smelting process once its multiblock is valid and powered.",
            "Control and power: There is no separate speed mode—the five-times Blast Furnace speed is built in. Solar Battery energy is the fallback power source for night or blocked-sky operation. Keep the structure exposed to sky if you want direct solar operation.",
            "Obtaining: The furnace component is crafted from Solarite and supporting materials using the recipe shown below; the finished furnace is assembled in-world."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:solarite_ingot", "useful_ores:solarite_ingot", "useful_ores:solarite_ingot",
            "useful_ores:solarite_ingot", "minecraft:furnace", "useful_ores:solarite_ingot",
            "minecraft:smooth_stone", "useful_ores:phosgene_block", "minecraft:smooth_stone"
         }, "useful_ores:solarite_furnace_component", 1)),

      Page.withItems(
            "Solar Battery",
            List.of("useful_ores:solar_battery"),
            "What it does: A rechargeable energy container used by the Solarite Furnace and related Solarite systems. Its stored charge is persistent and affects whether battery-powered machines can operate.",
            "How to use: Charge the battery through the mod's solar charging system and insert/provide it to a compatible Solarite machine. The battery is also used to craft the Solarite Battery Minecart. Batteries with different stored charge values do not stack together, so identical charge levels are required for stacking.",
            "Power behavior: A charged battery supplies stored energy when direct solar power is unavailable. It is therefore useful as an energy buffer for nighttime or enclosed-machine operation.",
            "How to make: Craft one Solar Battery using Iron Nuggets, Phosgene Ingot and Solarite Ingot in the recipe below. It is a crafted rechargeable item rather than a mined resource."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:iron_nugget", "useful_ores:phosgene_ingot", "minecraft:iron_nugget",
            "minecraft:iron_nugget", "useful_ores:solarite_ingot", "minecraft:iron_nugget",
            "minecraft:iron_nugget", "useful_ores:solarite_ingot", "minecraft:iron_nugget"
         }, "useful_ores:solar_battery", 1)),

      Page.withItems(
            "Solarite Battery Minecart",
            List.of("useful_ores:solarite_battery_minecart"),
            "What it does: A rideable, self-propelled minecart with an internal Solar Battery. It can move itself on ordinary rails, powered rails, Titanium Rails and Solarite Rails instead of requiring the player to push it.",
            "How to start and steer: Ride the cart and use W/S input to accelerate or decelerate its cruise speed. Holding the input ramps acceleration over about one second. The cart can reach a configured cruise speed up to 1.6 blocks/tick (32 blocks/s) on straight track.",
            "Speed modes: While riding, hold Shift and interact with the cart to cycle cruise-speed presets: 0.2, 0.5, 0.8, 1.1, 1.4 and 1.6 blocks/tick (4, 10, 16, 22, 28 and 32 blocks/s). Battery drain occurs during normal battery-powered travel.",
            "Free power and charging: In daylight with direct sky access, the battery cart can draw free solar power while it is on rail. A powered vanilla rail can also provide free grid power while powered. The battery can recharge during daylight operation. When the battery reaches zero and no free power source is available, the cart stops.",
            "Safety: Curves are intentionally speed-limited to prevent high-speed derailment: ordinary curved rails are capped around 0.6 blocks/tick, while Titanium Rail curves allow about 0.8 blocks/tick. Use long straight Solarite/Titanium sections for maximum speed.",
            "How to make: Combine a vanilla Minecart, one Solar Battery and one Solarite Ingot using the shapeless recipe below."
         )
         .withRecipe(Recipe.shapeless(
            List.of("minecraft:minecart", "useful_ores:solar_battery", "useful_ores:solarite_ingot"),
            "useful_ores:solarite_battery_minecart", 1)),

      Page.withItems(
            "Super Beacon",
            List.of("useful_ores:super_beacon"),
            "What it does: An enhanced beacon that uses an Extruding Nether Star instead of a normal Nether Star. Its effect radius is much larger than a normal beacon and scales with the pyramid size.",
            "How to build: Craft the Super Beacon with the recipe below, then place it like a normal beacon and construct its valid mineral-block pyramid beneath it. The beacon must have an unobstructed view of the sky to activate, following the normal beacon requirement.",
            "Range and scaling: The base effect radius is 64 blocks. Each additional pyramid layer adds 112 blocks to the radius according to the mod implementation. Configure the pyramid size to trade construction cost for coverage.",
            "Control: Use the beacon interface to select available effects as you would with a vanilla beacon. The special behavior is its upgraded star requirement and expanded range, not a separate custom mode screen.",
            "Obtaining: The final beacon is crafted; the Extruding Nether Star is made from a vanilla Nether Star and an Extruding Crystal."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "minecraft:glass", "minecraft:glass", "minecraft:glass",
            "minecraft:glass", "useful_ores:extruding_nether_star", "minecraft:glass",
            "minecraft:obsidian", "minecraft:obsidian", "minecraft:obsidian"
         }, "useful_ores:super_beacon", 1)),

      Page.withItems(
            "Nyxium Dark Barrier",
            List.of("useful_ores:nyxium_dark_barrier"),
            "What it does: A defensive dark-energy barrier block built around a Dark Core with Nyxium, Arcanite and Blackstone. It is intended for secure structures and high-tier bases.",
            "How to use: Place it as a building/defense block to create walls, chambers and containment structures. It has custom harvesting restrictions, so do not assume ordinary tools can recover it correctly.",
            "Control: The barrier has no redstone GUI or mode switch. Its special behavior is its protected block/harvesting rules and its dark-energy visual/defensive role.",
            "How to make: Craft one barrier with the shaped recipe below using Nyxium Ingots, Blackstone, Arcanite Ingots and a Dark Core. The finished barrier is crafted rather than found as a natural structure."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:nyxium_ingot", "minecraft:blackstone", "useful_ores:nyxium_ingot",
            "useful_ores:arcanite_ingot", "useful_ores:dark_core", "useful_ores:arcanite_ingot",
            "useful_ores:nyxium_ingot", "minecraft:blackstone", "useful_ores:nyxium_ingot"
         }, "useful_ores:nyxium_dark_barrier", 1)),

      Page.withItems(
            "Scheelite Chisel",
            List.of("useful_ores:scheelite_chisel"),
            "What it does: A durable shaping tool with 200 durability for converting compatible blocks into their slab form without consuming the chisel itself.",
            "How to use: Hold the chisel and interact with a compatible full block. The mod converts supported blocks to their corresponding slab form according to its chisel rules. If a block is not supported, the interaction does nothing useful.",
            "Durability: Each successful conversion consumes tool durability, so one chisel can perform many operations. It is a tool, not a recipe ingredient that disappears on use.",
            "How to make: Craft it from a Scheelite Ingot and two Sticks in the diagonal handle pattern shown below."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:scheelite_ingot", null, null,
            null, "minecraft:stick", null,
            null, null, "minecraft:stick"
         }, "useful_ores:scheelite_chisel", 1)),

      Page.withItems(
            "Titanium Lock",
            List.of("useful_ores:titanium_lock"),
            "What it does: A lock item for the mod's chest/container security system. It is paired with a Titanium Key so that access can be controlled without relying on a vanilla redstone lock.",
            "How to use: Apply the lock to a compatible container according to the mod's lock interaction. The lock is the secured-side component; the matching Titanium Key is the access item. Keep the key secure because possession of the matching key is what enables the intended unlock workflow.",
            "Control: The lock has no GUI mode selector. Its state is stored by the mod on the protected container, and the lock/key system handles access checks when the container is interacted with.",
            "How to make: Craft one Titanium Lock from Ilmenite Nuggets and Ingots in the shaped recipe below."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:ilmenite_nugget", "useful_ores:ilmenite_nugget", "useful_ores:ilmenite_nugget",
            "useful_ores:ilmenite_ingot", "useful_ores:ilmenite_ingot", "useful_ores:ilmenite_ingot",
            "useful_ores:ilmenite_ingot", "useful_ores:ilmenite_ingot", "useful_ores:ilmenite_ingot"
         }, "useful_ores:titanium_lock", 1)),

      Page.withItems(
            "Titanium Key",
            List.of("useful_ores:titanium_key"),
            "What it does: The access key paired with the Titanium Lock system. It is a small utility item rather than a combat tool.",
            "How to use: Use the key on the appropriate locked container/lock interaction to perform the mod's unlock/access action. A key does not configure a lock through a separate GUI.",
            "Control and security: The key is intended to be carried by the player who should have access. Because it is the access credential, avoid leaving copies in unsecured storage if the container is meant to remain private.",
            "How to make: Craft one Titanium Key from two Ilmenite Nuggets in the shaped recipe below."
         )
         .withRecipe(Recipe.crafting(new String[]{
            "useful_ores:ilmenite_nugget", "useful_ores:ilmenite_nugget", null,
            null, null, null,
            null, null, null
         }, "useful_ores:titanium_key", 1))
   ));

   private static final Chapter MISC = category("Misc", "useful_ores:lonsdaleite_campfire", buildMiscPages());

   private static List<Page> buildMiscPages() {
      List<Page> pages = new ArrayList<>();
      pages.add(Page.withItems(
            "Crashed Meteorite",
            List.of("useful_ores:meteorite_core", "useful_ores:meteorite_ash_block", "useful_ores:lonsdaleite_ore"),
            "What it is: a rare Overworld surface structure - a scorched impact crater with a buried, lumpy meteorite mass underneath it and pockets of Lonsdaleite ore embedded inside that mass.",
            "Where to find it: generates directly on the surface (never underwater), scattered randomly across the Overworld roughly every 81 chunks on average, with a minimum 20-chunk separation between two crash sites. Look for a burnt, bowl-shaped crater - the surface tell before you ever start digging.",
            "What it looks like: the crater bowl is lined with Scorched Stone, Scorched Dirt and Meteorite Ash Block in random patches, with more ash and scorched dirt scattered near the rim. Buried beneath the crater is an irregular, lumpy sphere (radius 5-10 blocks, lobed rather than perfectly round) built from Meteorite Block, with a denser Meteorite Core at its very center.",
            "The ore: 6 separate Lonsdaleite ore blobs are buried inside the meteorite mass itself, replacing Meteorite Block/Core near the mass's core - you're digging through meteorite rock to reach them, not searching open caves. Lonsdaleite is otherwise unobtainable; this structure is its only source.",
            "Progression: Meteorite Core (dug from the mass) is the seed of the Meteor Staff and Meteor Bomb progression - see the Meteorite Core Shard, Volatile Meteorite Core and Meteor Staff pages in the Utility chapter for the full crafting chain."
         ).hideItemNames());

      pages.add(Page.withItems(
            "Ancient Pedestal Chamber",
            List.of("useful_ores:ancient_pedestal"),
            "What it is: a rare underground Overworld structure - a hollow sphere shelled in Obsidian and speckled with Crying Obsidian, built around a single Ancient Pedestal with a loot chest beside it.",
            "Where to find it: generates underground anywhere in the Overworld, far rarer and more widely spaced than a meteorite - roughly every 300 chunks on average, with a minimum 60-chunk separation between two chambers. There is no surface tell; you'll only find one by exploring caves, digging, or using a locate command/structure-finder tool.",
            "What it looks like: an 8-block-radius hollow sphere with a roughly 1.6-block-thick shell, built from Obsidian speckled with about 1-in-6 Crying Obsidian blocks. The interior is hollow cave air, with a small flattened floor patch at the bottom so the pedestal and chest sit level instead of resting on the sphere's curve.",
            "Loot chest: sits two blocks from the pedestal and rolls a mix of Nyxium, Voidshard, Arcanite and Argentite ingots, Diamonds, Gold Ingots, and a chance each of Ender Eyes, Dragon's Breath, a Nyxium-Arcanite Core, Emeralds, and - rarely - a Nether Star.",
            "The pedestal: the Ancient Pedestal itself is already placed dead-center of the chamber and cannot be broken by anything except a Voidshard Pickaxe (any other tool has the break cancelled outright, even in survival with full tool tier). See the Ancient Pedestal and Nyxiumnite Staff pages in the Utility chapter for what it's used for once you reach it."
         ).hideItemNames());

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
      lanternText.add("Subspace Lantern: the high-tier lantern variant for the Subspace material palette. It is placed like a normal lantern and has no custom mode selector.");
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
      String p = pretty(m);
      if (m.equals("voidshard")) {
         return p + " " + type + ": a dark Voidshard light variant. Its light level is 0 rather than a normal positive light source, and the mod's darkness handler uses the Voidshard light blocks to create the special hostile-mob-spawn darkness effect. Place it normally; there is no custom mode selector. Craft it from the recipe shown in the recipe section.";
      }
      if (m.equals("lonsdaleite")) {
         return p + " " + type + ": a material-themed variant using the Lonsdaleite palette. It follows the normal placement/interaction rules for this lighting type; Lonsdaleite is available for the campfire family but is not part of the regular torch/lantern material list.";
      }
      return p + " " + type + ": a material-themed lighting variant. It uses the normal vanilla placement/interaction behavior for this block type and emits the mod's material color; there is no custom mode selector.";
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

