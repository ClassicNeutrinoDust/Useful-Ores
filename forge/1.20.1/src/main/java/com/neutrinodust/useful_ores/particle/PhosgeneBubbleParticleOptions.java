package com.neutrinodust.useful_ores.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public record PhosgeneBubbleParticleOptions(int color, float scale) implements ParticleOptions {
 public static final com.mojang.serialization.Codec<PhosgeneBubbleParticleOptions> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(instance -> instance.group(
   com.mojang.serialization.Codec.INT.fieldOf("color").forGetter(PhosgeneBubbleParticleOptions::color),
   com.mojang.serialization.Codec.FLOAT.fieldOf("scale").forGetter(PhosgeneBubbleParticleOptions::scale)
 ).apply(instance, PhosgeneBubbleParticleOptions::new));
 public static final ParticleOptions.Deserializer<PhosgeneBubbleParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
  @Override public PhosgeneBubbleParticleOptions fromCommand(ParticleType<PhosgeneBubbleParticleOptions> type, StringReader reader) throws CommandSyntaxException {
   reader.skipWhitespace(); int color=reader.readInt(); reader.skipWhitespace(); float scale=reader.readFloat(); return new PhosgeneBubbleParticleOptions(color,scale);
  }
  @Override public PhosgeneBubbleParticleOptions fromNetwork(ParticleType<PhosgeneBubbleParticleOptions> type, FriendlyByteBuf buf) { return new PhosgeneBubbleParticleOptions(buf.readInt(),buf.readFloat()); }
 };
 @Override public ParticleType<PhosgeneBubbleParticleOptions> getType() { return ModParticleTypes.PHOSGENE_BUBBLE.get(); }
 @Override public void writeToNetwork(FriendlyByteBuf buf) { buf.writeInt(color); buf.writeFloat(scale); }
 @Override public String writeToString() { return BuiltInRegistries.PARTICLE_TYPE.getKey(getType())+" "+color+" "+scale; }
}
