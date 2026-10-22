package com.neutrinodust.useful_ores.attribution;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModLegacyEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "useful_ores");
    private static final class LegacyEffect extends MobEffect { LegacyEffect(MobEffectCategory category, int color) { super(category, color); } }
    public static final RegistryObject<MobEffect> WIND_CHARGED = EFFECTS.register("wind_charged", () -> new LegacyEffect(MobEffectCategory.HARMFUL, 0xA8D9E8));
    public static final RegistryObject<MobEffect> OOZING = EFFECTS.register("oozing", () -> new LegacyEffect(MobEffectCategory.HARMFUL, 0x8DBD55));
    public static final RegistryObject<MobEffect> INFESTED = EFFECTS.register("infested", () -> new LegacyEffect(MobEffectCategory.HARMFUL, 0x6C8B4A));
    public static final RegistryObject<MobEffect> WEAVING = EFFECTS.register("weaving", () -> new LegacyEffect(MobEffectCategory.HARMFUL, 0xD6D6D6));
    private ModLegacyEffects() {}
    public static void init(IEventBus bus) { EFFECTS.register(bus); }
}
