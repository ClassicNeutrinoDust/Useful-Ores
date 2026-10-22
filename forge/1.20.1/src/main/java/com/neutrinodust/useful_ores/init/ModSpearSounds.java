package com.neutrinodust.useful_ores.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;

public final class ModSpearSounds {
    private static final String MODID = "useful_ores";
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);
    public static final net.minecraftforge.registries.RegistryObject<SoundEvent> SPEAR_USE = register("item.spear.use");
    public static final net.minecraftforge.registries.RegistryObject<SoundEvent> SPEAR_ATTACK = register("item.spear.attack");
    public static final net.minecraftforge.registries.RegistryObject<SoundEvent> SPEAR_ATTACK_HIT = register("item.spear.attack_hit");
    public static final net.minecraftforge.registries.RegistryObject<SoundEvent> SPEAR_HIT = register("item.spear.hit");
    public static final net.minecraftforge.registries.RegistryObject<SoundEvent> SPEAR_LUNGE = register("item.spear.lunge");
    private ModSpearSounds() {}
    private static net.minecraftforge.registries.RegistryObject<SoundEvent> register(String path) {
        return SOUNDS.register(path, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, path)));
    }
    public static void init(net.minecraftforge.eventbus.api.IEventBus bus) { SOUNDS.register(bus); }
}
