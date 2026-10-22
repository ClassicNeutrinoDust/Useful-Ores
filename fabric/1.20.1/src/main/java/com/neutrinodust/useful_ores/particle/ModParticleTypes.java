package com.neutrinodust.useful_ores.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModParticleTypes {

    private static <T extends ParticleType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation("useful_ores", name), type);
    }

    public static final ParticleType<ColoredFlameOptions> COLORED_FLAME =
            register("colored_flame", new ParticleType<ColoredFlameOptions>(false, ColoredFlameOptions.DESERIALIZER) {
                @Override
                public Codec<ColoredFlameOptions> codec() {
                    return ColoredFlameOptions.CODEC;
                }
            });

    public static final SimpleParticleType WIFI_RING =
            register("wifi_ring", net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple());

    public static final ParticleType<PhosgeneEffectParticleOptions> PHOSGENE_MIST =
            register("phosgene_mist", new ParticleType<PhosgeneEffectParticleOptions>(false, PhosgeneEffectParticleOptions.DESERIALIZER) {
                @Override
                public Codec<PhosgeneEffectParticleOptions> codec() {
                    return PhosgeneEffectParticleOptions.CODEC;
                }
            });

    public static final ParticleType<PhosgeneBubbleParticleOptions> PHOSGENE_BUBBLE =
            register("phosgene_bubble", new ParticleType<PhosgeneBubbleParticleOptions>(false, PhosgeneBubbleParticleOptions.DESERIALIZER) {
                @Override
                public Codec<PhosgeneBubbleParticleOptions> codec() {
                    return PhosgeneBubbleParticleOptions.CODEC;
                }
            });

    public static void init() {
    }
}
