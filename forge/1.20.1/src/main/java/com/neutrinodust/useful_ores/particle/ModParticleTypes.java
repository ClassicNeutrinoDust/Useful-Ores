package com.neutrinodust.useful_ores.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class ModParticleTypes {
 public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE,"useful_ores");
 public static final RegistryObject<ParticleType<ColoredFlameOptions>> COLORED_FLAME = PARTICLE_TYPES.register("colored_flame",()->new ParticleType<ColoredFlameOptions>(false,ColoredFlameOptions.DESERIALIZER){ @Override public com.mojang.serialization.Codec<ColoredFlameOptions> codec(){ return ColoredFlameOptions.CODEC; } });
 public static final RegistryObject<SimpleParticleType> WIFI_RING = PARTICLE_TYPES.register("wifi_ring",()->new SimpleParticleType(false));
 public static final RegistryObject<ParticleType<PhosgeneEffectParticleOptions>> PHOSGENE_MIST = PARTICLE_TYPES.register("phosgene_mist",()->new ParticleType<PhosgeneEffectParticleOptions>(false,PhosgeneEffectParticleOptions.DESERIALIZER){ @Override public com.mojang.serialization.Codec<PhosgeneEffectParticleOptions> codec(){ return PhosgeneEffectParticleOptions.CODEC; } });
 public static final RegistryObject<ParticleType<PhosgeneBubbleParticleOptions>> PHOSGENE_BUBBLE = PARTICLE_TYPES.register("phosgene_bubble",()->new ParticleType<PhosgeneBubbleParticleOptions>(false,PhosgeneBubbleParticleOptions.DESERIALIZER){ @Override public com.mojang.serialization.Codec<PhosgeneBubbleParticleOptions> codec(){ return PhosgeneBubbleParticleOptions.CODEC; } });
 private ModParticleTypes() {}
 public static void init(IEventBus bus){PARTICLE_TYPES.register(bus);}
}
