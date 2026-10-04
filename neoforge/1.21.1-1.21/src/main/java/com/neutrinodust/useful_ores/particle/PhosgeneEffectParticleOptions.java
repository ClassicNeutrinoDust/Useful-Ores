package com.neutrinodust.useful_ores.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PhosgeneEffectParticleOptions(int color, float scale) implements ParticleOptions {
    public static final MapCodec<PhosgeneEffectParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(PhosgeneEffectParticleOptions::color),
            Codec.FLOAT.fieldOf("scale").forGetter(PhosgeneEffectParticleOptions::scale)
    ).apply(instance, PhosgeneEffectParticleOptions::new));

    public static final StreamCodec<ByteBuf, PhosgeneEffectParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PhosgeneEffectParticleOptions::color,
            ByteBufCodecs.FLOAT, PhosgeneEffectParticleOptions::scale,
            PhosgeneEffectParticleOptions::new);

    @Override
    public ParticleType<PhosgeneEffectParticleOptions> getType() {
        return ModParticleTypes.PHOSGENE_MIST.get();
    }
}

