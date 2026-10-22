package com.neutrinodust.useful_ores.worldgen;

import com.neutrinodust.useful_ores.init.ModConfig;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class ModBiomeModifiers {

    private static final TagKey<Biome> END_BIOMES = TagKey.create(
            Registries.BIOME, Identifier.fromNamespaceAndPath("useful_ores", "generates_in/end_biomes"));

    private ModBiomeModifiers() {}

    public static void init() {

        addOverworld("arcanite", "arcanite_ore_placed");
        addOverworld("argentite", "argentite_ore_placed");
        addOverworld("chromite", "chromite_ore_placed");
        addOverworld("extruding_crystal", "extruding_crystal_ore_placed");
        addOverworld("fulgurite", "fulgurite_ore_placed");
        addOverworld("ilmenite", "ilmenite_ore_placed");
        addOverworld("osmium", "osmium_ore_placed");
        addOverworld("phosgene", "phosgene_ore_placed");
        addOverworld("scheelite", "scheelite_ore_placed");
        addOverworld("sperrylite", "sperrylite_ore_placed");
        addOverworld("zephyrite", "zephyrite_ore_placed");

        addOverworld("nyxium", "nyxium_ore_placed", "nyxium_ore_large_placed", "nyxium_ore_small_placed");

        addNether("solarite", "solarite_ore_placed");
        addNether("painite", "painite_ore_placed");

        addEnd("enderium", "enderium_ore_placed");
        addEnd("voidshard", "voidshard_ore_placed");
    }

    private static void addOverworld(String ore, String... placedFeatureNames) {
        if (ModConfig.isOreEnabled(ore)) add(BiomeSelectors.tag(BiomeTags.IS_OVERWORLD), placedFeatureNames);
    }

    private static void addNether(String ore, String... placedFeatureNames) {
        if (ModConfig.isOreEnabled(ore)) add(BiomeSelectors.tag(BiomeTags.IS_NETHER), placedFeatureNames);
    }

    private static void addEnd(String ore, String... placedFeatureNames) {
        if (ModConfig.isOreEnabled(ore)) add(BiomeSelectors.tag(END_BIOMES), placedFeatureNames);
    }

    private static void add(java.util.function.Predicate<net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext> selector,
                             String... placedFeatureNames) {
        for (String name : placedFeatureNames) {
            ResourceKey<PlacedFeature> key = ResourceKey.create(
                    Registries.PLACED_FEATURE,
                    Identifier.fromNamespaceAndPath("useful_ores", name));
            BiomeModifications.addFeature(
                    selector,
                    GenerationStep.Decoration.UNDERGROUND_ORES,
                    key);
        }
    }
}

