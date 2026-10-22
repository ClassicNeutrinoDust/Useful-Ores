package com.neutrinodust.useful_ores.farseeker;

import com.mojang.serialization.Codec;
import com.neutrinodust.useful_ores.util.ModComponent;
import net.minecraft.world.item.ItemStack;

public class ModFarseekerComponents {
    public static final ModComponent<Boolean> FARSEEKER = ModComponent.of("farseeker", Codec.BOOL);

    public static boolean has(ItemStack stack) {
        return Boolean.TRUE.equals(FARSEEKER.get(stack));
    }

    public static void init() {
    }
}
