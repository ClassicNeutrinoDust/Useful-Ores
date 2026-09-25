package com.neutrinodust.useful_ores.client.spear;

import net.minecraft.world.item.ItemStack;

/**
 * Ordinary runtime bridge implemented by the ArmedEntityRenderState mixin.
 * This interface deliberately lives outside the mixin-owned package: Mixin's
 * class loader forbids direct references to classes under the configured
 * mixin package from normal runtime code.
 */
public interface SpearArmedRenderStateAccess {
    ItemStack usefulOres$getRightHandStack();
    ItemStack usefulOres$getLeftHandStack();
    void usefulOres$setRightHandStack(ItemStack stack);
    void usefulOres$setLeftHandStack(ItemStack stack);
}
