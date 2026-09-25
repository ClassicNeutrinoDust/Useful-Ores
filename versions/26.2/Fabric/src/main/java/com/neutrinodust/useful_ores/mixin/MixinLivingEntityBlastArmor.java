package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.armor.LonsdaleiteArmorEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class MixinLivingEntityBlastArmor {

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true)
    private float usefulOres$reduceLonsdaleiteBlastDamage(float amount, ServerLevel level, DamageSource source) {
        if (!source.is(DamageTypeTags.IS_EXPLOSION)) return amount;

        LivingEntity self = (LivingEntity) (Object) this;
        return amount * LonsdaleiteArmorEvents.blastDamageMultiplier(self);
    }
}

