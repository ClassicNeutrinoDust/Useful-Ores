package com.neutrinodust.useful_ores.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class ModBiomeModifiers {

    private static final TagKey<Biome> END_BIOMES = TagKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath("useful_ores", "generates_in/end_biomes"));

    private ModBiomeModifiers() {}

    public static void init() {

        addOverworld("arcanite_ore_placed");
        addOverworld("argentite_ore_placed");
        addOverworld("chromite_ore_placed");
        addOverworld("extruding_crystal_ore_placed");
        addOverworld("fulgurite_ore_placed");
        addOverworld("ilmenite_ore_placed");
        addOverworld("osmium_ore_placed");
        addOverworld("phosgene_ore_placed");
        addOverworld("scheelite_ore_placed");
        addOverworld("sperrylite_ore_placed");
        addOverworld("zephyrite_ore_placed");

        addOverworld("nyxium_ore_placed", "nyxium_ore_large_placed", "nyxium_ore_small_placed");

        addNether("solarite_ore_placed", "painite_ore_placed");

        addEnd("enderium_ore_placed");
        addEnd("voidshard_ore_placed");
    }

    private static void addOverworld(String... placedFeatureNames) {
        add(BiomeSelectors.tag(BiomeTags.IS_OVERWORLD), placedFeatureNames);
    }

    private static void addNether(String... placedFeatureNames) {
        add(BiomeSelectors.tag(BiomeTags.IS_NETHER), placedFeatureNames);
    }

    private static void addEnd(String... placedFeatureNames) {
        add(BiomeSelectors.tag(END_BIOMES), placedFeatureNames);
    }

    private static void add(java.util.function.Predicate<net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext> selector,
                             String... placedFeatureNames) {
        for (String name : placedFeatureNames) {
            ResourceKey<PlacedFeature> key = ResourceKey.create(
                    Registries.PLACED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath("useful_ores", name));
            BiomeModifications.addFeature(
                    selector,
                    GenerationStep.Decoration.UNDERGROUND_ORES,
                    key);
        }
    }
}

