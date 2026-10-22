package com.neutrinodust.useful_ores.phosgene;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class ModPhosgeneRecipes {

    public static final RecipeSerializer<PhosgenePotionPowderRecipe> POTION_POWDER_SERIALIZER =
            register("phosgene_potion_powder", new SimpleCraftingRecipeSerializer<>(PhosgenePotionPowderRecipe::new));

    private static <T extends RecipeSerializer<?>> T register(String name, T serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                new ResourceLocation("useful_ores", name), serializer);
    }

    public static void init() {
    }
}
