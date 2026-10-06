package com.neutrinodust.useful_ores.painite;

/**
 * Accessor interface added to LivingEntityRenderState by MixinLivingEntityRenderStatePainite.
 * Kept outside the configured mixin package so normal game classes can safely reference it.
 */
public interface PainiteRenderStateAccess {
    boolean usefulOres$isPainiteFury();
    void usefulOres$setPainiteFury(boolean value);
}
