package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import net.minecraft.client.resources.sounds.RidingMinecartSoundInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RidingMinecartSoundInstance.class)
public abstract class MixinRidingMinecartSoundInstanceSolarite {
    private MixinAbstractSoundInstanceAccessor usefulOres$soundState() {
        return (MixinAbstractSoundInstanceAccessor) (Object) this;
    }

    @Shadow @Final private AbstractMinecart minecart;

    @Inject(method = "tick", at = @At("TAIL"))
    private void usefulOres$speedBasedRidingSound(CallbackInfo ci) {
        if (!(minecart instanceof SolariteBatteryMinecartEntity solariteMinecart)) return;

        float speed = (float) solariteMinecart.getDeltaMovement().horizontalDistance();
        float normalized = Mth.clamp(speed / 1.6F, 0.0F, 1.0F);

        if (speed < 0.005F) {
            usefulOres$soundState().usefulOres$setVolume(0.0F);
            usefulOres$soundState().usefulOres$setPitch(0.0F);
            return;
        }

        usefulOres$soundState().usefulOres$setVolume(0.75F * normalized);
        usefulOres$soundState().usefulOres$setPitch(0.55F + 0.65F * normalized);
    }
}

