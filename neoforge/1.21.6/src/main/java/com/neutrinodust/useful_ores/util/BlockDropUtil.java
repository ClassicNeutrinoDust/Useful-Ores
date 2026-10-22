package com.neutrinodust.useful_ores.util;

import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class BlockDropUtil {
    private BlockDropUtil() {}

    public static boolean survivesExplosion(LootParams.Builder params) {
        Float radius = params.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        return radius == null || params.getLevel().getRandom().nextFloat() <= 1.0F / radius;
    }
}
