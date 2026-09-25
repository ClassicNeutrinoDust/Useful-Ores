package com.neutrinodust.useful_ores.mixin;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public abstract class MixinAbstractMinecartSolariteBehavior {

    @Shadow @Final @Mutable
    private MinecartBehavior behavior;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void usefulOres$useModernBehavior(CallbackInfo ci) {
        this.behavior = new NewMinecartBehavior((AbstractMinecart) (Object) this);
    }
}

