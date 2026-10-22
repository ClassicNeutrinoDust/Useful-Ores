package com.neutrinodust.useful_ores.blastproof;

public final class ExplosionContext {

    private ExplosionContext() {}

    private static boolean active = false;

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean value) {
        active = value;
    }
}

