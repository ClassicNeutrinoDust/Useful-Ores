package com.neutrinodust.useful_ores.farseeker;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFarseekerRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER,
                    "useful_ores");

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

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FarseekerToolRecipe>>
            FARSEEKER_TOOL_SERIALIZER = SERIALIZERS.register("farseeker_tool",
            () -> serializer(FARSEEKER_TOOL_CODEC, FARSEEKER_TOOL_STREAM_CODEC));

    private static <T extends net.minecraft.world.item.crafting.Recipe<?>> RecipeSerializer<T> serializer(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new RecipeSerializer<>() {
            @Override public MapCodec<T> codec() { return codec; }
            @Override public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() { return streamCodec; }
        };
    }

    public static void init(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}

