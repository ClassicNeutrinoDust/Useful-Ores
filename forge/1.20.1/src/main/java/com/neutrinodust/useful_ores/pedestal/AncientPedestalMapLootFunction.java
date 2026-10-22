package com.neutrinodust.useful_ores.pedestal;

import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class AncientPedestalMapLootFunction extends LootItemConditionalFunction {
    private final boolean excludeNearest;

    public AncientPedestalMapLootFunction(LootItemCondition[] conditions, boolean excludeNearest) {
        super(conditions);
        this.excludeNearest = excludeNearest;
    }

    private static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTION_TYPES =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, "useful_ores");

    public static final RegistryObject<LootItemFunctionType> TYPE =
            LOOT_FUNCTION_TYPES.register("ancient_pedestal_map", () -> new LootItemFunctionType(new Serializer()));

    public static void init(IEventBus eventBus) {
        LOOT_FUNCTION_TYPES.register(eventBus);
    }

    @Override
    public LootItemFunctionType getType() {
        return TYPE.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        var origin = context.getParamOrNull(net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN);
        if (origin == null) return ItemStack.EMPTY;
        return AncientPedestalMapLogic.createMap(
                context.getLevel(), (int) Math.floor(origin.x()), (int) Math.floor(origin.z()), excludeNearest);
    }

    public static final class Builder implements LootItemFunction.Builder {
        private final boolean excludeNearest;
        public Builder(boolean excludeNearest) { this.excludeNearest = excludeNearest; }
        @Override public LootItemFunction build() {
            return new AncientPedestalMapLootFunction(new LootItemCondition[0], excludeNearest);
        }
    }

    public static final class Serializer extends LootItemConditionalFunction.Serializer<AncientPedestalMapLootFunction> {
        @Override
        public AncientPedestalMapLootFunction deserialize(
                JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            return new AncientPedestalMapLootFunction(
                    conditions, GsonHelper.getAsBoolean(json, "exclude_nearest", false));
        }

        @Override
        public void serialize(JsonObject json, AncientPedestalMapLootFunction function,
                              JsonSerializationContext context) {
            super.serialize(json, function, context);
            json.addProperty("exclude_nearest", function.excludeNearest);
        }
    }
}
