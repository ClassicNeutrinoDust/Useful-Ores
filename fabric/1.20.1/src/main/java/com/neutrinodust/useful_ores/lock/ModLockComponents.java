package com.neutrinodust.useful_ores.lock;

import com.mojang.serialization.Codec;
import com.neutrinodust.useful_ores.util.ModComponent;

public class ModLockComponents {
    public static final ModComponent<String> BOUND_LOCK_ID = ModComponent.of("bound_lock_id", Codec.STRING);

    public static void init() {
    }
}
