package com.neutrinodust.useful_ores.item;

import com.neutrinodust.useful_ores.lock.ModLockComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.UUID;
import java.util.function.Consumer;

public class TitaniumKeyItem extends Item {

    public TitaniumKeyItem(Properties props) {
        super(props);
    }

    public static UUID getBoundLockId(ItemStack stack) {
        String raw = stack.get(ModLockComponents.BOUND_LOCK_ID);
        if (raw == null || raw.isEmpty()) return null;
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isBound(ItemStack stack) {
        return getBoundLockId(stack) != null;
    }

    public static void bind(ItemStack stack, UUID lockId) {
        stack.set(ModLockComponents.BOUND_LOCK_ID, lockId.toString());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> tooltip, TooltipFlag flag) {
        if (isBound(stack)) {
            tooltip.add(Component.translatable("item.useful_ores.titanium_key.bound").withStyle(ChatFormatting.GOLD));
        } else {
            tooltip.add(Component.translatable("item.useful_ores.titanium_key.unbound").withStyle(ChatFormatting.GRAY));
        }
    }
}

