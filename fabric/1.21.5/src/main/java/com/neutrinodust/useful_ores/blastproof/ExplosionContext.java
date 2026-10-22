package com.neutrinodust.useful_ores.blastproof;

public final class ExplosionContext {

    private ExplosionContext() {}

    private static int depth = 0;

    public static boolean isActive() {
        return depth > 0;
    }

    public static void setActive(boolean value) {
        depth = value ? depth + 1 : Math.max(0, depth - 1);
    }
}

