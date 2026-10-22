package com.neutrinodust.useful_ores.phosgene;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import com.neutrinodust.useful_ores.util.PotionStackData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class PhosgenePotionPowderRecipe extends CustomRecipe {
    private final CraftingBookCategory category;
    public PhosgenePotionPowderRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
        this.category = category;
    }
    @Override public CraftingBookCategory category() { return category; }

    private ItemStack findPotion(CraftingContainer input) {
        int powderCount = 0;
        ItemStack potion = ItemStack.EMPTY;

        if (input.getWidth() != 3 || input.getHeight() != 3) return ItemStack.EMPTY;

        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) return ItemStack.EMPTY;

            if (i == 4) {

                if (!(stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION))
                        || stack.getCount() != 1) return ItemStack.EMPTY;
                if (!PotionStackData.hasContents(stack) || !PotionStackData.isVanillaEffectPotion(stack)
                        || PotionStackData.effects(stack).isEmpty()) return ItemStack.EMPTY;
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
        return width >= 3 && height >= 3;
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
        PotionStackData.copyContents(potion, result);
        return result;
    }

    @Override
    public RecipeSerializer<PhosgenePotionPowderRecipe> getSerializer() {
        return com.neutrinodust.useful_ores.phosgene.ModPhosgeneRecipes.POTION_POWDER_SERIALIZER.get();
    }
}

