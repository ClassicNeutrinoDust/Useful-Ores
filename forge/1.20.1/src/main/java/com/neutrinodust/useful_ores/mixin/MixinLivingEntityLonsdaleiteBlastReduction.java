package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.armor.LonsdaleiteArmorEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityLonsdaleiteBlastReduction {
    @Shadow
    protected abstract float getDamageAfterArmorAbsorb(DamageSource source, float amount);

    @Redirect(
            method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"),
            require = 1)
    private float usefulOres$reduceExplosionBeforeArmor(LivingEntity entity, DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            float reduction = LonsdaleiteArmorEvents.blastDamageReduction(entity);
            if (reduction > 0.0F) amount *= 1.0F - reduction;
        }
        return ((MixinLivingEntityLonsdaleiteBlastReduction) (Object) entity)
                .getDamageAfterArmorAbsorb(source, amount);
    }
}
