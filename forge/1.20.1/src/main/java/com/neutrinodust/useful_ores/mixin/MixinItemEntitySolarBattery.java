package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.solar.SolarBatteryEvents;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntitySolarBattery {
    @Inject(method = "tick", at = @At("HEAD"))
    private void usefulOres$tickSolarBattery(CallbackInfo ci) {
        SolarBatteryEvents.tick((ItemEntity) (Object) this);
    }
}
