package com.neutrinodust.useful_ores.farseeker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FarseekerBlockEvents {

    public static final ThreadLocal<Entity> CURRENT_BREAKER = new ThreadLocal<>();

    public static boolean tryAbsorb(ItemStack stack) {
        Entity breaker = CURRENT_BREAKER.get();
        if (!(breaker instanceof Player player) || player.level().isClientSide()) return false;
        if (stack.isEmpty()) return false;

        ItemStack tool = player.getMainHandItem();
        if (!ModFarseekerComponents.has(tool)) return false;

        boolean added = player.getInventory().add(stack);
        return added && stack.isEmpty();
    }
}

