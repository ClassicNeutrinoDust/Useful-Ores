package com.neutrinodust.useful_ores.client.spear;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;






public final class SpearRenderContext {
    private static final ThreadLocal<Deque<Context>> STACK =
            ThreadLocal.withInitial(ArrayDeque::new);

    private SpearRenderContext() {}

    public static void push(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, boolean leftHanded) {
        STACK.get().push(new Context(entity, stack, displayContext,
                leftHanded ? HumanoidArm.LEFT : HumanoidArm.RIGHT));
    }

    public static void pop() {
        Deque<Context> stack = STACK.get();
        if (!stack.isEmpty()) stack.pop();
        if (stack.isEmpty()) STACK.remove();
    }

    public static Context current() {
        Deque<Context> stack = STACK.get();
        return stack.isEmpty() ? null : stack.peek();
    }

    public record Context(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, HumanoidArm arm) {}
}
