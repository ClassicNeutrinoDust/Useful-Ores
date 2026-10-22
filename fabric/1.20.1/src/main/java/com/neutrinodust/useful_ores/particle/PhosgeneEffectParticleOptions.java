package com.neutrinodust.useful_ores.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public record PhosgeneEffectParticleOptions(int color, float scale) implements ParticleOptions {

    public static final Codec<PhosgeneEffectParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(PhosgeneEffectParticleOptions::color),
            Codec.FLOAT.fieldOf("scale").forGetter(PhosgeneEffectParticleOptions::scale)
    ).apply(instance, PhosgeneEffectParticleOptions::new));

    public static final ParticleOptions.Deserializer<PhosgeneEffectParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public PhosgeneEffectParticleOptions fromCommand(ParticleType<PhosgeneEffectParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int color = reader.readInt();
            reader.expect(' ');
            float scale = reader.readFloat();
            return new PhosgeneEffectParticleOptions(color, scale);
        }

        @Override
        public PhosgeneEffectParticleOptions fromNetwork(ParticleType<PhosgeneEffectParticleOptions> type, FriendlyByteBuf buf) {
            return new PhosgeneEffectParticleOptions(buf.readInt(), buf.readFloat());
        }
    };

    @Override
    public ParticleType<PhosgeneEffectParticleOptions> getType() {
        return ModParticleTypes.PHOSGENE_MIST;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeInt(color);
        buf.writeFloat(scale);
    }

    @Override
    public String writeToString() {
        return net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE.getKey(getType()) + " " + color + " " + scale;
    }
}
