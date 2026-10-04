package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import com.neutrinodust.useful_ores.minecart.SolariteMinecartCollisionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;






@Mixin(CollisionContext.class)
public interface MixinCollisionContextSolariteMinecart {
    @Inject(
            method = "of(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/shapes/CollisionContext;",
            at = @At("HEAD"),
            cancellable = true)
    private static void usefulOres$solariteMinecartContext(
            Entity entity, CallbackInfoReturnable<CollisionContext> cir) {
        if (entity instanceof SolariteBatteryMinecartEntity cart) {
            cir.setReturnValue(new SolariteMinecartCollisionContext(cart, false));
        }
    }
}
