package com.neutrinodust.useful_ores.mixin;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;






@Mixin(AbstractMinecart.class)
public interface AbstractMinecartAccessor {
    @Accessor("flipped")
    boolean useful_ores$isFlipped();

    @Accessor("flipped")
    void useful_ores$setFlipped(boolean flipped);
}
