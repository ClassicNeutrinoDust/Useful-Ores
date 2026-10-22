package com.neutrinodust.useful_ores.attribution;

import com.mojang.serialization.Codec;
import com.neutrinodust.useful_ores.util.ModComponent;

public class ModDataComponents {

    public static final ModComponent<String> ATTRIBUTED_EFFECT = ModComponent.of("attributed_effect", Codec.STRING);

    public static final ModComponent<Boolean> PAINITE_FURY_TOOL = ModComponent.of("painite_fury_tool", Codec.BOOL);

    public static ModComponent<Boolean> painiteFuryToolType() { return PAINITE_FURY_TOOL; }

    public static void init() {
    }
}
