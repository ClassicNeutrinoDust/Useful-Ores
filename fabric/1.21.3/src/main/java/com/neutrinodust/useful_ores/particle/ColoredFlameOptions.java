package com.neutrinodust.useful_ores.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ColoredFlameOptions(int color, float scale) implements ParticleOptions {

    public static final MapCodec<ColoredFlameOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(ColoredFlameOptions::color),
            Codec.FLOAT.fieldOf("scale").forGetter(ColoredFlameOptions::scale)
    ).apply(instance, ColoredFlameOptions::new));

    public static final StreamCodec<ByteBuf, ColoredFlameOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ColoredFlameOptions::color,
            ByteBufCodecs.FLOAT, ColoredFlameOptions::scale,
            ColoredFlameOptions::new
    );

    @Override
    public ParticleType<ColoredFlameOptions> getType() {
        return ModParticleTypes.COLORED_FLAME;
    }
}

