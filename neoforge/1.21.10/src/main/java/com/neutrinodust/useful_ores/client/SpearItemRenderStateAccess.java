package com.neutrinodust.useful_ores.client;

import net.minecraft.world.item.ItemStack;







public interface SpearItemRenderStateAccess {
    void usefulOres$setItemStack(ItemStack stack);
    ItemStack usefulOres$getItemStack();

    default boolean usefulOres$isSpear() {
        ItemStack stack = usefulOres$getItemStack();
        return !stack.isEmpty() && stack.getItem() instanceof com.neutrinodust.useful_ores.item.BackportSpearItem;
    }
}
