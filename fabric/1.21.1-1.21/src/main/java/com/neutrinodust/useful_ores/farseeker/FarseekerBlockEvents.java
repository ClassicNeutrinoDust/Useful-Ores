package com.neutrinodust.useful_ores.farseeker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class FarseekerBlockEvents {

    private static final ThreadLocal<ArrayList<Entity>> BREAKER_STACK = ThreadLocal.withInitial(ArrayList::new);

    public static void pushBreaker(Entity breaker) {
        BREAKER_STACK.get().add(breaker);
    }

    public static void popBreaker() {
        ArrayList<Entity> stack = BREAKER_STACK.get();
        if (!stack.isEmpty()) stack.remove(stack.size() - 1);
        if (stack.isEmpty()) BREAKER_STACK.remove();
    }

    private static Entity currentBreaker() {
        ArrayList<Entity> stack = BREAKER_STACK.get();
        return stack.isEmpty() ? null : stack.get(stack.size() - 1);
    }

    public static boolean tryAbsorb(ItemStack stack) {
        Entity breaker = currentBreaker();
        if (!(breaker instanceof Player player) || player.level().isClientSide()) return false;
        if (stack.isEmpty()) return false;

        ItemStack tool = player.getMainHandItem();
        if (!ModFarseekerComponents.has(tool)) return false;

        boolean added = player.getInventory().add(stack);
        return added && stack.isEmpty();
    }
}

