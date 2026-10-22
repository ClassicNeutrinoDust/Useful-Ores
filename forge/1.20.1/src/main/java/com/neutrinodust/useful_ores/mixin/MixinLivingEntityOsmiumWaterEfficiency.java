package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.armor.OsmiumArmorEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityOsmiumWaterEfficiency {
    @Inject(method = "getWaterSlowDown()F", at = @At("HEAD"), cancellable = true)
    private void usefulOres$osmiumWaterFriction(CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof Player player) || !player.isInWater()
                || !OsmiumArmorEvents.hasFullOsmiumSet(player)) return;

        cir.setReturnValue(player.onGround() ? 0.54600006F : 0.67300003F);
    }
}
