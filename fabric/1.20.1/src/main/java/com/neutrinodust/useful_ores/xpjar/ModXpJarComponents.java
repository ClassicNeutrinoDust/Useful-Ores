package com.neutrinodust.useful_ores.xpjar;

import com.mojang.serialization.Codec;
import com.neutrinodust.useful_ores.util.ModComponent;

public class ModXpJarComponents {
    public static final ModComponent<Integer> JAR_XP = ModComponent.of("jar_xp", Codec.INT);

    public static void init() {
    }
}
