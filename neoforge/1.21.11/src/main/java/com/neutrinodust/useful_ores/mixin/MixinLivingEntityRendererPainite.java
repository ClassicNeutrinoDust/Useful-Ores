package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.painite.PainiteRenderStateAccess;

import com.neutrinodust.useful_ores.painite.PainitePower;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRendererPainite {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void usefulOres$extractPainiteFury(LivingEntity entity, LivingEntityRenderState state, float partialTick, CallbackInfo ci) {
        ((PainiteRenderStateAccess) state).usefulOres$setPainiteFury(PainitePower.isFuryActive(entity));
    }
}
