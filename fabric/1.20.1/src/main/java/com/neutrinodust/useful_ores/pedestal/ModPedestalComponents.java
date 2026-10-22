package com.neutrinodust.useful_ores.pedestal;

import com.neutrinodust.useful_ores.util.ModComponent;
import net.minecraft.core.GlobalPos;

public class ModPedestalComponents {
    public static final ModComponent<GlobalPos> LINKED_PEDESTAL = ModComponent.of("linked_pedestal", GlobalPos.CODEC);

    public static void init() {
    }
}
