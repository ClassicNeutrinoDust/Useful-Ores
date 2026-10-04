package com.neutrinodust.useful_ores.particle;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ModParticleTypes {

    private static <T extends ParticleType<?>> T register(String name, T type) {
        ResourceKey<ParticleType<?>> key = ResourceKey.create(Registries.PARTICLE_TYPE,
                ResourceLocation.fromNamespaceAndPath("useful_ores", name));
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, key, type);
    }

    public static final ParticleType<ColoredFlameOptions> COLORED_FLAME =
            register("colored_flame", new ParticleType<ColoredFlameOptions>(false) {
                @Override
                public MapCodec<ColoredFlameOptions> codec() {
                    return ColoredFlameOptions.CODEC;
                }

                @Override
                public StreamCodec<? super ByteBuf, ColoredFlameOptions> streamCodec() {
                    return ColoredFlameOptions.STREAM_CODEC;
                }
            });

    public static final SimpleParticleType WIFI_RING =
            register("wifi_ring", net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple());

    public static final ParticleType<PhosgeneEffectParticleOptions> PHOSGENE_MIST =
            register("phosgene_mist", new ParticleType<PhosgeneEffectParticleOptions>(false) {
                @Override
                public MapCodec<PhosgeneEffectParticleOptions> codec() {
                    return PhosgeneEffectParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super ByteBuf, PhosgeneEffectParticleOptions> streamCodec() {
                    return PhosgeneEffectParticleOptions.STREAM_CODEC;
                }
            });

    public static final ParticleType<PhosgeneBubbleParticleOptions> PHOSGENE_BUBBLE =
            register("phosgene_bubble", new ParticleType<PhosgeneBubbleParticleOptions>(false) {
                @Override
                public MapCodec<PhosgeneBubbleParticleOptions> codec() {
                    return PhosgeneBubbleParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super ByteBuf, PhosgeneBubbleParticleOptions> streamCodec() {
                    return PhosgeneBubbleParticleOptions.STREAM_CODEC;
                }
            });

    public static void init() {

    }
}

