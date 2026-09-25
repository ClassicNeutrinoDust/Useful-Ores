package com.neutrinodust.useful_ores.attribution;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public class ModDataComponents {

    public static final DataComponentType<String> ATTRIBUTED_EFFECT = register("attributed_effect",
            DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());

    public static final DataComponentType<Boolean> PAINITE_FURY_TOOL = register("painite_fury_tool",
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    private static <T> DataComponentType<T> register(String name, DataComponentType<T> type) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath("useful_ores", name), type);
    }

    public static DataComponentType<Boolean> painiteFuryToolType() { return PAINITE_FURY_TOOL; }

    public static void init() {
    }
}

