package com.neutrinodust.useful_ores.farseeker;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class ModFarseekerRecipes {

    public static final RecipeSerializer<FarseekerToolRecipe> FARSEEKER_TOOL_SERIALIZER =
            register("farseeker_tool", new SimpleCraftingRecipeSerializer<>(FarseekerToolRecipe::new));

    private static <T extends RecipeSerializer<?>> T register(String name, T serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                new ResourceLocation("useful_ores", name), serializer);
    }

    public static void init() {
    }
}
