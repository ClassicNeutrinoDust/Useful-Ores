package com.neutrinodust.useful_ores.pedestal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class AncientPedestalMapLootFunction implements LootItemFunction {
    private final boolean excludeNearest;

    public AncientPedestalMapLootFunction(boolean excludeNearest) {
        this.excludeNearest = excludeNearest;
    }

    public static final LootItemFunctionType TYPE = new LootItemFunctionType(
            new Serializer<AncientPedestalMapLootFunction>() {
                @Override
                public void serialize(JsonObject json, AncientPedestalMapLootFunction value, JsonSerializationContext context) {
                }

                @Override
                public AncientPedestalMapLootFunction deserialize(JsonObject json, JsonDeserializationContext context) {
                    return new AncientPedestalMapLootFunction(false);
                }
            });

    public static void init() {
        Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE,
                new ResourceLocation("useful_ores", "ancient_pedestal_map"), TYPE);
    }

    @Override
    public LootItemFunctionType getType() {
        return TYPE;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        var origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (origin == null) return ItemStack.EMPTY;
        return AncientPedestalMapLogic.createMap(
                context.getLevel(),
                (int) Math.floor(origin.x()),
                (int) Math.floor(origin.z()),
                excludeNearest
        );
    }

    public static final class Builder implements LootItemFunction.Builder {
        private final boolean excludeNearest;

        public Builder(boolean excludeNearest) {
            this.excludeNearest = excludeNearest;
        }

        @Override
        public LootItemFunction build() {
            return new AncientPedestalMapLootFunction(excludeNearest);
        }
    }
}
