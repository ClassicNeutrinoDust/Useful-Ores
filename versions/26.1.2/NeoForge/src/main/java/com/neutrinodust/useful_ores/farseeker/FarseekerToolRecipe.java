package com.neutrinodust.useful_ores.farseeker;

import com.neutrinodust.useful_ores.attribution.ModAttributedItems;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Set;

public class FarseekerToolRecipe extends CustomRecipe {

    private final CraftingBookCategory category;

    public FarseekerToolRecipe(CraftingBookCategory category) {
        super();
        this.category = category;
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    private static boolean isEligibleTool(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        Set<Item> eligible = Set.of(
                ModItems.SUBSPACE_ITEMS.get(1).get(),
                ModItems.SUBSPACE_ITEMS.get(2).get(),
                ModItems.SUBSPACE_ITEMS.get(3).get(),
                ModItems.SUBSPACE_ITEMS.get(4).get(),
                ModAttributedItems.ATTRIBUTED_SUBSPACE_PICKAXE.get(),
                ModAttributedItems.ATTRIBUTED_SUBSPACE_AXE.get(),
                ModAttributedItems.ATTRIBUTED_SUBSPACE_HOE.get(),
                ModAttributedItems.ATTRIBUTED_SUBSPACE_SHOVEL.get()
        );
        return eligible.contains(item);
    }

    private ItemStack findMatch(CraftingInput input) {
        ItemStack tool = ItemStack.EMPTY;
        int crystals = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModItems.FARSEEKER_CRYSTAL.get())) {
                if (stack.getCount() != 1) return ItemStack.EMPTY;
                crystals++;
                continue;
            }

            if (isEligibleTool(stack)) {
                if (!tool.isEmpty()) return ItemStack.EMPTY;
                if (stack.getCount() != 1) return ItemStack.EMPTY;
                if (ModFarseekerComponents.has(stack)) return ItemStack.EMPTY;
                tool = stack;
                continue;
            }

            return ItemStack.EMPTY;
        }

        if (crystals != 1 || tool.isEmpty()) return ItemStack.EMPTY;
        return tool;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return !findMatch(input).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack tool = findMatch(input);
        if (tool.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = tool.copy();
        result.setCount(1);
        result.set(ModFarseekerComponents.FARSEEKER.get(), true);
        return result;
    }

    @Override
    public RecipeSerializer<FarseekerToolRecipe> getSerializer() {
        return ModFarseekerRecipes.FARSEEKER_TOOL_SERIALIZER.get();
    }
}

