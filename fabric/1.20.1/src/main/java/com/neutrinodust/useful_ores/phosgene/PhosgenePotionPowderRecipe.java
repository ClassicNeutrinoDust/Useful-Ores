package com.neutrinodust.useful_ores.phosgene;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.neutrinodust.useful_ores.util.ModPotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class PhosgenePotionPowderRecipe extends CustomRecipe {
    public PhosgenePotionPowderRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    private ItemStack findPotion(CraftingContainer input) {
        int powderCount = 0;
        ItemStack potion = ItemStack.EMPTY;

        if (input.getWidth() != 3 || input.getHeight() != 3) return ItemStack.EMPTY;

        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) return ItemStack.EMPTY;

            if (i == 4) {
                if (!stack.is(Items.POTION) || stack.getCount() != 1) return ItemStack.EMPTY;
                ModPotionContents contents = com.neutrinodust.useful_ores.util.ModPotionContents.get(stack);
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
    public boolean canCraftInDimensions(int width, int height) {
        return width > 0 && height > 0;
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        return !findPotion(input).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        ItemStack potion = findPotion(input);
        if (potion.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = new ItemStack(com.neutrinodust.useful_ores.init.ModItems.PHOSGENE_POTION_POWDER.get(), 2);
        ModPotionContents contents = com.neutrinodust.useful_ores.util.ModPotionContents.get(potion);
        if (contents != null) com.neutrinodust.useful_ores.util.ModPotionContents.set(result, contents);
        return result;
    }

    @Override
    public RecipeSerializer<PhosgenePotionPowderRecipe> getSerializer() {
        return com.neutrinodust.useful_ores.phosgene.ModPhosgeneRecipes.POTION_POWDER_SERIALIZER;
    }
}

