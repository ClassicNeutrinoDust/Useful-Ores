package com.neutrinodust.useful_ores.phosgene;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ModPhosgeneRecipes {
    private ModPhosgeneRecipes() {}

    private static final MapCodec<PhosgenePotionPowderRecipe> POTION_POWDER_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                            .forGetter(r -> CraftingBookCategory.MISC)
            ).apply(instance, PhosgenePotionPowderRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, PhosgenePotionPowderRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.idMapper(i -> CraftingBookCategory.values()[i], Enum::ordinal),
                    r -> CraftingBookCategory.MISC,
                    (cat) -> new PhosgenePotionPowderRecipe(cat)
            );

    public static final RecipeSerializer<PhosgenePotionPowderRecipe> POTION_POWDER_SERIALIZER =
            register("phosgene_potion_powder",
                    new RecipeSerializer<>(POTION_POWDER_CODEC, STREAM_CODEC));

    private static <T extends RecipeSerializer<?>> T register(String name, T serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                Identifier.fromNamespaceAndPath("useful_ores", name), serializer);
    }

    public static void init() {}
}

