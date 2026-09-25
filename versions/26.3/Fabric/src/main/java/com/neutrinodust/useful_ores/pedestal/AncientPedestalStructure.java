package com.neutrinodust.useful_ores.pedestal;

import com.neutrinodust.useful_ores.init.ModStructureTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class AncientPedestalStructure extends Structure {

   public static final MapCodec<AncientPedestalStructure> CODEC = RecordCodecBuilder.<AncientPedestalStructure>mapCodec(instance -> instance.group(
         settingsCodec(instance),
         Codec.intRange(4, 12).fieldOf("sphere_radius").forGetter(s -> s.sphereRadius)
   ).apply(instance, AncientPedestalStructure::new));

   public final int sphereRadius;

   public AncientPedestalStructure(StructureSettings settings, int sphereRadius) {
      super(settings);
      this.sphereRadius = sphereRadius;
   }

   @Override
   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      int chunkX = context.chunkPos().getMiddleBlockX();
      int chunkZ = context.chunkPos().getMiddleBlockZ();
      RandomSource random = context.random();

      int minY = context.heightAccessor().getMinY() + sphereRadius + 6;
      int maxY = Math.min(40, context.heightAccessor().getMaxY() - sphereRadius - 6);
      if (maxY <= minY) return Optional.empty();

      int y = minY + random.nextInt(maxY - minY + 1);
      BlockPos centre = new BlockPos(chunkX, y, chunkZ);

      return Optional.of(new GenerationStub(centre, builder ->
            builder.addPiece(new AncientPedestalPiece(centre, sphereRadius))));
   }

   @Override
   public StructureType<?> type() {
      return ModStructureTypes.ANCIENT_PEDESTAL;
   }
}

