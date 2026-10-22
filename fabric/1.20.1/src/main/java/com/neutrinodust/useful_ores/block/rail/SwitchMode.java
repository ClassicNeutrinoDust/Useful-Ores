package com.neutrinodust.useful_ores.block.rail;

import net.minecraft.util.StringRepresentable;

public enum SwitchMode implements StringRepresentable {
    LEFT("left"),
    STRAIGHT("straight"),
    RIGHT("right");

    private final String name;

    SwitchMode(String name) {
        this.name = name;
    }

    public SwitchMode next() {
        return switch (this) {
            case LEFT -> STRAIGHT;
            case STRAIGHT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}

