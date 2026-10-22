package com.neutrinodust.useful_ores.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.neutrinodust.useful_ores.init.ModConfig;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBiomeModifierTypes {

    private static final DeferredRegister<Codec<? extends BiomeModifier>> SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, "useful_ores");

    public static final RegistryObject<Codec<ConfigAddFeatures>> CONFIG_ADD_FEATURES =
        SERIALIZERS.register("config_add_features", () -> RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("ore").forGetter(ConfigAddFeatures::ore),
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ConfigAddFeatures::biomes),
            PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(ConfigAddFeatures::features),
            GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(ConfigAddFeatures::step)
        ).apply(i, ConfigAddFeatures::new)));

    private ModBiomeModifierTypes() {}

    public static void init(IEventBus bus) {
        SERIALIZERS.register(bus);
    }

    public record ConfigAddFeatures(String ore, HolderSet<Biome> biomes, HolderSet<PlacedFeature> features,
                                    GenerationStep.Decoration step) implements BiomeModifier {
        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD && ModConfig.isOreEnabled(ore) && biomes.contains(biome)) {
                features.forEach(holder -> builder.getGenerationSettings().addFeature(step, holder));
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return CONFIG_ADD_FEATURES.get();
        }
    }
}
