package com.neutrinodust.useful_ores.attribution;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import java.util.ArrayList;
import java.util.List;

public class ModAttributionRecipes {

    public static final RecipeSerializer<AttributedIngotRecipe> ATTRIBUTED_INGOT_SERIALIZER =
            register("attributed_ingot", new SimpleCraftingRecipeSerializer<>(AttributedIngotRecipe::new));

    public static final RecipeSerializer<AttributedGearRecipe> ATTRIBUTED_GEAR_SERIALIZER =
            register("attributed_gear", new RecipeSerializer<AttributedGearRecipe>() {
                @Override
                public AttributedGearRecipe fromJson(ResourceLocation id, JsonObject json) {
                    int ingots = GsonHelper.getAsInt(json, "required_ingots");
                    int sticks = GsonHelper.getAsInt(json, "required_sticks", 0);
                    boolean weapon = GsonHelper.getAsBoolean(json, "is_weapon", false);
                    ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(json, "result"));
                    Item item = BuiltInRegistries.ITEM.getOptional(itemId)
                            .orElseThrow(() -> new JsonSyntaxException("Unknown item '" + itemId + "' in " + id));
                    CraftingBookCategory category = parseCategory(
                            GsonHelper.getAsString(json, "category", "equipment"), CraftingBookCategory.EQUIPMENT);
                    List<String> pattern = new ArrayList<>();
                    if (json.has("pattern")) {
                        JsonArray rows = GsonHelper.getAsJsonArray(json, "pattern");
                        for (JsonElement row : rows) pattern.add(GsonHelper.convertToString(row, "pattern row"));
                    }
                    boolean mirrored = GsonHelper.getAsBoolean(json, "mirrored", false);
                    return new AttributedGearRecipe(id, ingots, sticks, weapon, () -> item, category, pattern, mirrored);
                }

                @Override
                public AttributedGearRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
                    int ingots = buf.readVarInt();
                    int sticks = buf.readVarInt();
                    boolean weapon = buf.readBoolean();
                    Item item = BuiltInRegistries.ITEM.get(buf.readResourceLocation());
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
                    int size = buf.readVarInt();
                    if (size < 0 || size > 1024) throw new IllegalArgumentException("Invalid pattern list size: " + size);
                    List<String> pattern = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) pattern.add(buf.readUtf());
                    boolean mirrored = buf.readBoolean();
                    return new AttributedGearRecipe(id, ingots, sticks, weapon, () -> item, category, pattern, mirrored);
                }

                @Override
                public void toNetwork(FriendlyByteBuf buf, AttributedGearRecipe recipe) {
                    buf.writeVarInt(recipe.getRequiredIngots());
                    buf.writeVarInt(recipe.getRequiredSticks());
                    buf.writeBoolean(recipe.isWeapon());
                    buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.getOutput()));
                    buf.writeEnum(recipe.category());
                    buf.writeVarInt(recipe.getPattern().size());
                    for (String row : recipe.getPattern()) buf.writeUtf(row);
                    buf.writeBoolean(recipe.isMirrored());
                }
            });

    static CraftingBookCategory parseCategory(String name, CraftingBookCategory fallback) {
        for (CraftingBookCategory c : CraftingBookCategory.values()) {
            if (c.getSerializedName().equals(name)) return c;
        }
        return fallback;
    }

    private static <T extends RecipeSerializer<?>> T register(String name, T serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                new ResourceLocation("useful_ores", name), serializer);
    }

    public static void init() {
    }
}
