package com.neutrinodust.useful_ores.solar;

import com.mojang.serialization.Codec;
import com.neutrinodust.useful_ores.util.ModComponent;

public class ModSolarComponents {
    public static final ModComponent<Integer> BATTERY_ENERGY = ModComponent.of("battery_energy", Codec.INT);

    public static void init() {
    }
}
