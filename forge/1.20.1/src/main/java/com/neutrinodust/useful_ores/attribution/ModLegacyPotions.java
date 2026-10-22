package com.neutrinodust.useful_ores.attribution;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModLegacyPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, "useful_ores");
    public static final RegistryObject<Potion> WIND_CHARGED = POTIONS.register("wind_charged", () -> new Potion("wind_charged", new MobEffectInstance(ModLegacyEffects.WIND_CHARGED.get(), 3600)));
    public static final RegistryObject<Potion> OOZING = POTIONS.register("oozing", () -> new Potion("oozing", new MobEffectInstance(ModLegacyEffects.OOZING.get(), 3600)));
    public static final RegistryObject<Potion> INFESTED = POTIONS.register("infested", () -> new Potion("infested", new MobEffectInstance(ModLegacyEffects.INFESTED.get(), 3600)));
    public static final RegistryObject<Potion> WEAVING = POTIONS.register("weaving", () -> new Potion("weaving", new MobEffectInstance(ModLegacyEffects.WEAVING.get(), 3600)));
    private ModLegacyPotions() {}
    public static void init(IEventBus bus) { POTIONS.register(bus); }
}
