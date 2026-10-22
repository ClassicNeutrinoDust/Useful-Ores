package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.painite.PainitePower;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityPainite {
    @Inject(method = "tick", at = @At("TAIL"))
    private void usefulOres$painiteTick(CallbackInfo ci) {
        PainitePower.update((LivingEntity)(Object)this);
    }
}
