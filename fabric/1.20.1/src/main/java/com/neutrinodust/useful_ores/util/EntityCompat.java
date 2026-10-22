package com.neutrinodust.useful_ores.util;

import net.minecraft.world.entity.Entity;

public final class EntityCompat {
    private EntityCompat() {}

    public static void setOldPosAndRot(Entity entity) {
        entity.xo = entity.getX();
        entity.yo = entity.getY();
        entity.zo = entity.getZ();
        entity.xOld = entity.getX();
        entity.yOld = entity.getY();
        entity.zOld = entity.getZ();
        entity.yRotO = entity.getYRot();
        entity.xRotO = entity.getXRot();
    }
}
