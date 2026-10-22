package com.neutrinodust.useful_ores.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.neutrinodust.useful_ores.init.ModConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ModConditions {

    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS =
        DeferredRegister.create(NeoForgeRegistries.CONDITION_SERIALIZERS, "useful_ores");

    public static final Supplier<MapCodec<ItemEnabled>> ITEM_ENABLED =
        CONDITIONS.register("item_enabled", () -> ItemEnabled.CODEC);
    public static final Supplier<MapCodec<OreEnabled>> ORE_ENABLED =
        CONDITIONS.register("ore_enabled", () -> OreEnabled.CODEC);

    private ModConditions() {}

    public static void init(IEventBus bus) {
        CONDITIONS.register(bus);
    }

    public record ItemEnabled(String item) implements ICondition {
        public static final MapCodec<ItemEnabled> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            com.mojang.serialization.Codec.STRING.fieldOf("item").forGetter(ItemEnabled::item)
        ).apply(i, ItemEnabled::new));

        @Override public boolean test(IContext context) { return ModConfig.isItemEnabled(item); }
        @Override public MapCodec<? extends ICondition> codec() { return CODEC; }
    }

    public record OreEnabled(String ore) implements ICondition {
        public static final MapCodec<OreEnabled> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            com.mojang.serialization.Codec.STRING.fieldOf("ore").forGetter(OreEnabled::ore)
        ).apply(i, OreEnabled::new));

        @Override public boolean test(IContext context) { return ModConfig.isOreEnabled(ore); }
        @Override public MapCodec<? extends ICondition> codec() { return CODEC; }
    }
}
