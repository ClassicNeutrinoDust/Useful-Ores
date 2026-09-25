package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.painite.PainiteRenderStateAccess;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public abstract class MixinLivingEntityRenderStatePainite implements PainiteRenderStateAccess {
    @Unique
    private boolean usefulOres$painiteFury;

    @Override
    public boolean usefulOres$isPainiteFury() {
        return usefulOres$painiteFury;
    }

    @Override
    public void usefulOres$setPainiteFury(boolean value) {
        usefulOres$painiteFury = value;
    }
}
