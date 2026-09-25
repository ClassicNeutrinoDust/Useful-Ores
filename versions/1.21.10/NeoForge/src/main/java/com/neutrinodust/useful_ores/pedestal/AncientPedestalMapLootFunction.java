package com.neutrinodust.useful_ores.pedestal;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Runtime loot function so map target selection sees the real chest origin. */
public final class AncientPedestalMapLootFunction implements LootItemFunction {
    private final boolean excludeNearest;

    public AncientPedestalMapLootFunction(boolean excludeNearest) {
        this.excludeNearest = excludeNearest;
    }

    private static final MapCodec<AncientPedestalMapLootFunction> CODEC = MapCodec.unit(
            new AncientPedestalMapLootFunction(false));

    private static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_TYPES =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, "useful_ores");

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<AncientPedestalMapLootFunction>> TYPE =
            LOOT_FUNCTION_TYPES.register("ancient_pedestal_map", () -> new LootItemFunctionType<>(CODEC));

    public static void init(IEventBus eventBus) {
        LOOT_FUNCTION_TYPES.register(eventBus);
    }

    @Override
    public LootItemFunctionType<? extends LootItemFunction> getType() {
        return TYPE.get();
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        var origin = context.getOptionalParameter(LootContextParams.ORIGIN);
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
