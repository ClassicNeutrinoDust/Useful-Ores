package com.neutrinodust.useful_ores.config;

import com.google.gson.JsonObject;
import com.neutrinodust.useful_ores.init.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public final class ModConditions {

    public static final ResourceLocation ITEM_ENABLED_ID = new ResourceLocation("useful_ores", "item_enabled");

    private ModConditions() {}

    public static void init() {
        CraftingHelper.register(ItemEnabled.SERIALIZER);
    }

    public record ItemEnabled(String item) implements ICondition {
        public static final IConditionSerializer<ItemEnabled> SERIALIZER = new IConditionSerializer<>() {
            @Override
            public void write(JsonObject json, ItemEnabled value) {
                json.addProperty("item", value.item());
            }

            @Override
            public ItemEnabled read(JsonObject json) {
                return new ItemEnabled(GsonHelper.getAsString(json, "item"));
            }

            @Override
            public ResourceLocation getID() {
                return ITEM_ENABLED_ID;
            }
        };

        @Override
        public ResourceLocation getID() {
            return ITEM_ENABLED_ID;
        }

        @Override
        public boolean test(IContext context) {
            return ModConfig.isItemEnabled(item);
        }
    }
}
