package com.neutrinodust.useful_ores.client;

import net.minecraft.world.item.ItemStack;

/**
 * Transient bridge from the 1.21.10 ItemModelResolver input stack to
 * ItemInHandLayer. 1.21.10's ItemInHandLayer does not receive the held
 * ItemStack argument that 1.21.11 added, so we associate it with the
 * ItemStackRenderState for the duration of the render-state build.
 */
public interface SpearItemRenderStateAccess {
    void usefulOres$setItemStack(ItemStack stack);
    ItemStack usefulOres$getItemStack();

    default boolean usefulOres$isSpear() {
        ItemStack stack = usefulOres$getItemStack();
        return !stack.isEmpty() && stack.getItem() instanceof com.neutrinodust.useful_ores.item.BackportSpearItem;
    }
}
