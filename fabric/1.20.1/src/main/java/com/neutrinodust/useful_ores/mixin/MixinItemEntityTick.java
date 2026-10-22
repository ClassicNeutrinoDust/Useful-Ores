package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.solar.SolarBatteryEvents;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class MixinItemEntityTick {

    @Inject(method = "tick", at = @At("TAIL"))
    private void useful_ores$onTick(CallbackInfo ci) {
        SolarBatteryEvents.INSTANCE.onItemEntityTick((ItemEntity) (Object) this);
    }
}

