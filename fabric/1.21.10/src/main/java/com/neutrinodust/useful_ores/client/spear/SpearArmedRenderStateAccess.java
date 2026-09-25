package com.neutrinodust.useful_ores.client.spear;

import net.minecraft.world.item.ItemStack;







public interface SpearArmedRenderStateAccess {
    ItemStack usefulOres$getRightHandStack();
    ItemStack usefulOres$getLeftHandStack();
    void usefulOres$setRightHandStack(ItemStack stack);
    void usefulOres$setLeftHandStack(ItemStack stack);
}
