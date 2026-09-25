package com.neutrinodust.useful_ores.pedestal;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Runtime loot function so map target selection sees the real chest origin. */
public final class AncientPedestalMapLootFunction implements LootItemFunction {
    private final boolean excludeNearest;

    public AncientPedestalMapLootFunction(boolean excludeNearest) {
        this.excludeNearest = excludeNearest;
    }

    private static final MapCodec<AncientPedestalMapLootFunction> CODEC = MapCodec.unit(
            new AncientPedestalMapLootFunction(false));

    public static final LootItemFunctionType<AncientPedestalMapLootFunction> TYPE = new LootItemFunctionType<>(CODEC);

    public static void init() {
        Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE,
                ResourceLocation.fromNamespaceAndPath("useful_ores", "ancient_pedestal_map"), TYPE);
    }

    @Override
    public LootItemFunctionType<? extends LootItemFunction> getType() {
        return TYPE;
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
