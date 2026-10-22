package com.neutrinodust.useful_ores.attribution;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public class AttributedGearRecipe extends CustomRecipe {

    private final int  requiredIngots;
    private final int  requiredSticks;
    private final boolean isWeapon;
    private final Supplier<net.minecraft.world.item.Item> output;
    private final CraftingBookCategory category;

    private final List<String> pattern;
    private final boolean mirrored;

    public AttributedGearRecipe(ResourceLocation id, int requiredIngots, int requiredSticks, boolean isWeapon,
                                 Supplier<net.minecraft.world.item.Item> output,
                                 CraftingBookCategory category,
                                 List<String> pattern, boolean mirrored) {
        super(id, category);
        this.requiredIngots = requiredIngots;
        this.requiredSticks = requiredSticks;
        this.isWeapon       = isWeapon;
        this.output         = output;
        this.category       = category;
        this.pattern        = pattern;
        this.mirrored       = mirrored;
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    private static char cellAt(CraftingContainer input, int col, int row) {
        ItemStack stack = input.getItem(row * input.getWidth() + col);
        if (stack.isEmpty()) return '.';
        if (stack.is(ModAttributedItems.ATTRIBUTED_SUBSPACE_INGOT.get())) return 'I';
        if (stack.is(Items.STICK)) return 'S';
        return '?';
    }

    private static String mirrorRow(String row) {
        return new StringBuilder(row).reverse().toString();
    }

    private static int[] boundingBox(CraftingContainer input) {
        int minCol = Integer.MAX_VALUE, maxCol = -1, minRow = Integer.MAX_VALUE, maxRow = -1;
        for (int r = 0; r < input.getHeight(); r++) {
            for (int c = 0; c < input.getWidth(); c++) {
                if (!input.getItem(r * input.getWidth() + c).isEmpty()) {
                    minCol = Math.min(minCol, c);
                    maxCol = Math.max(maxCol, c);
                    minRow = Math.min(minRow, r);
                    maxRow = Math.max(maxRow, r);
                }
            }
        }
        if (maxCol < 0) return null;
        return new int[]{minCol, maxCol, minRow, maxRow};
    }

    private boolean shapeMatches(CraftingContainer input) {
        int[] box = boundingBox(input);
        if (box == null) return false;
        int width = box[1] - box[0] + 1;
        int height = box[3] - box[2] + 1;

        List<String> candidate = pattern;
        int patHeight = candidate.size();
        int patWidth = candidate.isEmpty() ? 0 : candidate.get(0).length();
        if (width != patWidth || height != patHeight) return false;

        boolean plainMatch = true, mirroredMatch = mirrored;
        for (int r = 0; r < height && (plainMatch || mirroredMatch); r++) {
            String row = candidate.get(r);
            String mirroredRow = mirrored ? mirrorRow(row) : null;
            for (int c = 0; c < width; c++) {
                char actual = cellAt(input, box[0] + c, box[2] + r);
                char wanted = row.charAt(c);
                if (wanted == '.') {
                    if (actual != '.') { plainMatch = false; }
                } else if (actual != wanted) {
                    plainMatch = false;
                }
                if (mirrored) {
                    char wantedMirror = mirroredRow.charAt(c);
                    if (wantedMirror == '.') {
                        if (actual != '.') mirroredMatch = false;
                    } else if (actual != wantedMirror) {
                        mirroredMatch = false;
                    }
                }
            }
        }
        return plainMatch || mirroredMatch;
    }

    private String findAttribute(CraftingContainer input) {
        if (pattern != null && !pattern.isEmpty() && !shapeMatches(input)) return null;

        int ingots = 0;
        int sticks = 0;
        String attribute = null;

        for (int i = 0; i < input.getContainerSize(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModAttributedItems.ATTRIBUTED_SUBSPACE_INGOT.get())) {
                ingots++;
                String id = ModDataComponents.ATTRIBUTED_EFFECT.get(stack);
                if (id == null) return null;
                if (attribute == null) {
                    attribute = id;
                } else if (!attribute.equals(id)) {
                    return null;
                }
            } else if (requiredSticks > 0 && stack.is(Items.STICK)) {
                sticks++;
            } else {
                return null;
            }
        }

        if (ingots != requiredIngots) return null;
        if (sticks != requiredSticks)  return null;
        if (attribute == null)         return null;

        AttributedEffects effect = AttributedEffects.byId(attribute);
        if (effect == null) return null;
        if (!isWeapon && !effect.armorEligible()) return null;

        return attribute;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width > 0 && height > 0;
    }

    @Override
    public boolean matches(CraftingContainer input, Level level) {
        return findAttribute(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        String attribute = findAttribute(input);
        if (attribute == null) return ItemStack.EMPTY;
        ItemStack result = new ItemStack(output.get());
        ModDataComponents.ATTRIBUTED_EFFECT.set(result, attribute);
        return result;
    }

    @Override
    public RecipeSerializer<AttributedGearRecipe> getSerializer() {
        return ModAttributionRecipes.ATTRIBUTED_GEAR_SERIALIZER;
    }

    public int     getRequiredIngots() { return requiredIngots; }
    public int     getRequiredSticks() { return requiredSticks; }
    public boolean isWeapon()          { return isWeapon; }
    public net.minecraft.world.item.Item getOutput() { return output.get(); }
    public List<String> getPattern()   { return pattern; }
    public boolean isMirrored()        { return mirrored; }
}

