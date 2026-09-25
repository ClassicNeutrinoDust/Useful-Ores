package com.neutrinodust.useful_ores.particle;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, "useful_ores");

    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredFlameOptions>> COLORED_FLAME =
            PARTICLE_TYPES.register("colored_flame", () -> new ParticleType<ColoredFlameOptions>(false) {
                @Override
                public MapCodec<ColoredFlameOptions> codec() {
                    return ColoredFlameOptions.CODEC;
                }

                @Override
                public StreamCodec<? super ByteBuf, ColoredFlameOptions> streamCodec() {
                    return ColoredFlameOptions.STREAM_CODEC;
                }
            });

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WIFI_RING =
            PARTICLE_TYPES.register("wifi_ring", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, ParticleType<PhosgeneEffectParticleOptions>> PHOSGENE_MIST =
            PARTICLE_TYPES.register("phosgene_mist", () -> new ParticleType<PhosgeneEffectParticleOptions>(false) {
                @Override
                public MapCodec<PhosgeneEffectParticleOptions> codec() {
                    return PhosgeneEffectParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super ByteBuf, PhosgeneEffectParticleOptions> streamCodec() {
                    return PhosgeneEffectParticleOptions.STREAM_CODEC;
                }
            });

    public static final DeferredHolder<ParticleType<?>, ParticleType<PhosgeneBubbleParticleOptions>> PHOSGENE_BUBBLE =
            PARTICLE_TYPES.register("phosgene_bubble", () -> new ParticleType<PhosgeneBubbleParticleOptions>(false) {
                @Override
                public MapCodec<PhosgeneBubbleParticleOptions> codec() {
                    return PhosgeneBubbleParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super ByteBuf, PhosgeneBubbleParticleOptions> streamCodec() {
                    return PhosgeneBubbleParticleOptions.STREAM_CODEC;
                }
            });

    public static void init(IEventBus bus) {
        PARTICLE_TYPES.register(bus);
    }
}

