package com.neutrinodust.useful_ores.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/** Dedicated spear sounds for the Minecraft 1.21.5 backport, using the supplied recordings. */
public final class ModSpearSounds {
    private static final String MODID = "useful_ores";

    public static final SoundEvent SPEAR_USE = register("item.spear.use");
    public static final SoundEvent SPEAR_ATTACK = register("item.spear.attack");
    public static final SoundEvent SPEAR_ATTACK_HIT = register("item.spear.attack_hit");
    public static final SoundEvent SPEAR_HIT = register("item.spear.hit");
    public static final SoundEvent SPEAR_LUNGE = register("item.spear.lunge");

    private ModSpearSounds() {}

    private static SoundEvent register(String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MODID, path);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void init() {}
}
