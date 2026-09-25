package com.neutrinodust.useful_ores.farseeker;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModFarseekerRecipes {

    private static final MapCodec<FarseekerToolRecipe> FARSEEKER_TOOL_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    CraftingBookCategory.CODEC.optionalFieldOf(
                            "category", CraftingBookCategory.EQUIPMENT
                    ).forGetter(FarseekerToolRecipe::category)
            ).apply(instance, FarseekerToolRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, FarseekerToolRecipe> FARSEEKER_TOOL_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.idMapper(i -> CraftingBookCategory.values()[i], Enum::ordinal),
                    FarseekerToolRecipe::category,
                    FarseekerToolRecipe::new
            );

    public static final RecipeSerializer<FarseekerToolRecipe> FARSEEKER_TOOL_SERIALIZER =
            register("farseeker_tool", serializer(FARSEEKER_TOOL_CODEC, FARSEEKER_TOOL_STREAM_CODEC));

    private static <T extends net.minecraft.world.item.crafting.Recipe<?>> RecipeSerializer<T> serializer(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> packetCodec) {
        return new RecipeSerializer<>() {
            @Override public MapCodec<T> codec() { return codec; }
            @Override public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() { return packetCodec; }
        };
    }

    private static <T extends RecipeSerializer<?>> T register(String name, T serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                Identifier.fromNamespaceAndPath("useful_ores", name), serializer);
    }

    public static void init() {
    }
}

