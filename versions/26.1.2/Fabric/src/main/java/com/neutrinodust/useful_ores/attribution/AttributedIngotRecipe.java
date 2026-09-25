package com.neutrinodust.useful_ores.attribution;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AttributedIngotRecipe extends CustomRecipe {

    private final CraftingBookCategory category;

    public AttributedIngotRecipe(CraftingBookCategory category) {
        super();
        this.category = category;
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    private String findMatch(CraftingInput input) {
        int ingotCount = 0;
        String matched = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(com.neutrinodust.useful_ores.init.ModItems.SUBSPACE_INGOT.get())) {
                ingotCount++;
                continue;
            }

            PotionContents contents = stack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
            if (contents != null && contents.potion().isPresent()) {
                if (matched != null) return null;

                AttributedEffects effect = AttributedEffects.fromPotion(contents.potion().get());
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
    public boolean matches(CraftingInput input, Level level) {
        return findMatch(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        String attribute = findMatch(input);
        if (attribute == null) return ItemStack.EMPTY;
        ItemStack result = new ItemStack(
                com.neutrinodust.useful_ores.attribution.ModAttributedItems.ATTRIBUTED_SUBSPACE_INGOT.get());
        result.set(ModDataComponents.ATTRIBUTED_EFFECT, attribute);
        return result;
    }

    @Override
    public RecipeSerializer<AttributedIngotRecipe> getSerializer() {
        return ModAttributionRecipes.ATTRIBUTED_INGOT_SERIALIZER;
    }
}

