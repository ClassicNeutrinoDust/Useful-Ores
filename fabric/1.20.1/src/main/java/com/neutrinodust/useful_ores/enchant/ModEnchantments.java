package com.neutrinodust.useful_ores.enchant;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantments {
    private ModEnchantments() {}

    public static final Enchantment LUNGE = Registry.register(BuiltInRegistries.ENCHANTMENT,
            new ResourceLocation("useful_ores", "lunge"), new LungeEnchantment());

    public static void init() {
    }
}
