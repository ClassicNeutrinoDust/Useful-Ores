package com.neutrinodust.useful_ores.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PhosgeneBubbleParticleOptions(int color, float scale) implements ParticleOptions {
    public static final MapCodec<PhosgeneBubbleParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(PhosgeneBubbleParticleOptions::color),
            Codec.FLOAT.fieldOf("scale").forGetter(PhosgeneBubbleParticleOptions::scale)
    ).apply(instance, PhosgeneBubbleParticleOptions::new));

    public static final StreamCodec<ByteBuf, PhosgeneBubbleParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PhosgeneBubbleParticleOptions::color,
            ByteBufCodecs.FLOAT, PhosgeneBubbleParticleOptions::scale,
            PhosgeneBubbleParticleOptions::new);

    @Override
    public ParticleType<PhosgeneBubbleParticleOptions> getType() {
        return ModParticleTypes.PHOSGENE_BUBBLE.get();
    }
}

