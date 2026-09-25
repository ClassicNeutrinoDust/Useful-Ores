package com.neutrinodust.useful_ores.farseeker;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ModFarseekerComponents {

    public static final DataComponentType<Boolean> FARSEEKER = register("farseeker",
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    private static <T> DataComponentType<T> register(String name, DataComponentType<T> type) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath("useful_ores", name), type);
    }

    public static boolean has(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(FARSEEKER));
    }

    public static void init() {
    }
}

