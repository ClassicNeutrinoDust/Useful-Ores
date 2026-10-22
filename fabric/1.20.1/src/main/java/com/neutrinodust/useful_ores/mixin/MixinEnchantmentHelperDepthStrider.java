package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.armor.OsmiumArmorEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class MixinEnchantmentHelperDepthStrider {
    @Inject(method = "getDepthStrider", at = @At("RETURN"), cancellable = true)
    private static void usefulOres$osmiumWaterWalk(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (OsmiumArmorEvents.isWaterWalking(entity)) {
            cir.setReturnValue(Math.max(cir.getReturnValueI(), 3));
        }
    }
}
