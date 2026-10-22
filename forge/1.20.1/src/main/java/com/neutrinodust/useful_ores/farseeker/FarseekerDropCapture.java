package com.neutrinodust.useful_ores.farseeker;

import net.minecraft.world.entity.player.Player;

public final class FarseekerDropCapture {
    private static final ThreadLocal<Player> CURRENT_PLAYER = new ThreadLocal<>();

    private FarseekerDropCapture() {}

    public static void begin(Player player) {
        if (player != null && !player.level().isClientSide
                && ModFarseekerComponents.has(player.getMainHandItem())) {
            CURRENT_PLAYER.set(player);
        } else {
            CURRENT_PLAYER.remove();
        }
    }

    public static void end() {
        CURRENT_PLAYER.remove();
    }

    public static Player currentPlayer() {
        Player player = CURRENT_PLAYER.get();
        return player != null && !player.isRemoved() && !player.level().isClientSide ? player : null;
    }
}
