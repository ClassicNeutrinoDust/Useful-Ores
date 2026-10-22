package com.neutrinodust.useful_ores.attribution;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AttributedIngotRecipe extends CustomRecipe {

    private final CraftingBookCategory category;

    public AttributedIngotRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
        this.category = category;
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    private String findMatch(CraftingContainer input) {
        int ingotCount = 0;
        String matched = null;

        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(com.neutrinodust.useful_ores.init.ModItems.SUBSPACE_INGOT.get())) {
                ingotCount++;
                continue;
            }

            net.minecraft.world.item.alchemy.Potion potion = com.neutrinodust.useful_ores.util.PotionStackData.potion(stack);
            if (com.neutrinodust.useful_ores.util.PotionStackData.hasContents(stack)) {
                if (matched != null) return null;

                AttributedEffects effect = AttributedEffects.fromPotion(potion);
                if (effect == null) return null;
                matched = effect.id();
                continue;
            }

            return null;
        }

        if (ingotCount != 1 || matched == null) return null;
        return matched;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width > 0 && height > 0;
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        return findMatch(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        String attribute = findMatch(input);
        if (attribute == null) return ItemStack.EMPTY;
        ItemStack result = new ItemStack(
                com.neutrinodust.useful_ores.attribution.ModAttributedItems.ATTRIBUTED_SUBSPACE_INGOT.get());
        ModDataComponents.setAttributedEffect(result, attribute);
        return result;
    }

    @Override
    public RecipeSerializer<AttributedIngotRecipe> getSerializer() {
        return ModAttributionRecipes.ATTRIBUTED_INGOT_SERIALIZER.get();
    }
}

