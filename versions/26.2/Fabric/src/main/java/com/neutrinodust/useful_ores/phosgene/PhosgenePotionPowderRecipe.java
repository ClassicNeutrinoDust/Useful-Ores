package com.neutrinodust.useful_ores.phosgene;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class PhosgenePotionPowderRecipe extends CustomRecipe {
    public PhosgenePotionPowderRecipe(CraftingBookCategory category) {
        super();
    }

    private ItemStack findPotion(CraftingInput input) {
        int powderCount = 0;
        ItemStack potion = ItemStack.EMPTY;

        if (input.width() != 3 || input.height() != 3) return ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) return ItemStack.EMPTY;

            if (i == 4) {
                if (!stack.is(Items.POTION) || stack.getCount() != 1) return ItemStack.EMPTY;
                PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
                if (contents == null || !contents.hasEffects()) return ItemStack.EMPTY;
                potion = stack;
            } else {
                if (!stack.is(com.neutrinodust.useful_ores.init.ModItems.PHOSGENE_RAW_POWDER.get())) return ItemStack.EMPTY;
                powderCount++;
            }
        }

        return powderCount == 8 ? potion : ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return !findPotion(input).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack potion = findPotion(input);
        if (potion.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = new ItemStack(com.neutrinodust.useful_ores.init.ModItems.PHOSGENE_POTION_POWDER.get(), 2);
        PotionContents contents = potion.get(DataComponents.POTION_CONTENTS);
        if (contents != null) result.set(DataComponents.POTION_CONTENTS, contents);
        return result;
    }

    @Override
    public RecipeSerializer<PhosgenePotionPowderRecipe> getSerializer() {
        return com.neutrinodust.useful_ores.phosgene.ModPhosgeneRecipes.POTION_POWDER_SERIALIZER;
    }
}

