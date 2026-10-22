package com.neutrinodust.useful_ores.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public record ColoredFlameOptions(int color, float scale) implements ParticleOptions {

    public static final Codec<ColoredFlameOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("color").forGetter(ColoredFlameOptions::color),
            Codec.FLOAT.fieldOf("scale").forGetter(ColoredFlameOptions::scale)
    ).apply(instance, ColoredFlameOptions::new));

    public static final ParticleOptions.Deserializer<ColoredFlameOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public ColoredFlameOptions fromCommand(ParticleType<ColoredFlameOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int color = reader.readInt();
            reader.expect(' ');
            float scale = reader.readFloat();
            return new ColoredFlameOptions(color, scale);
        }

        @Override
        public ColoredFlameOptions fromNetwork(ParticleType<ColoredFlameOptions> type, FriendlyByteBuf buf) {
            return new ColoredFlameOptions(buf.readInt(), buf.readFloat());
        }
    };

    @Override
    public ParticleType<ColoredFlameOptions> getType() {
        return ModParticleTypes.COLORED_FLAME;
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
