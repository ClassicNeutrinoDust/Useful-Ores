package com.neutrinodust.useful_ores.block;

import com.neutrinodust.useful_ores.init.ModStructureTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MeteoriteStructure extends Structure {

   public static final MapCodec<MeteoriteStructure> CODEC = RecordCodecBuilder.<MeteoriteStructure>mapCodec(instance -> instance.group(
         settingsCodec(instance),
         Codec.intRange(4, 24).fieldOf("min_radius").forGetter(s -> s.minRadius),
         Codec.intRange(4, 32).fieldOf("max_radius").forGetter(s -> s.maxRadius),
         Codec.doubleRange(1.0, 4.0).fieldOf("crater_multiplier").forGetter(s -> s.craterMultiplier),
         Codec.intRange(1, 16).fieldOf("ore_count").forGetter(s -> s.oreCount)
   ).apply(instance, MeteoriteStructure::new));

   public final int minRadius;
   public final int maxRadius;
   public final double craterMultiplier;
   public final int oreCount;

   public MeteoriteStructure(StructureSettings settings, int minRadius, int maxRadius, double craterMultiplier, int oreCount) {
      super(settings);
      this.minRadius = minRadius;
      this.maxRadius = maxRadius;
      this.craterMultiplier = craterMultiplier;
      this.oreCount = oreCount;
   }

   @Override
   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      int chunkX = context.chunkPos().getMiddleBlockX();
      int chunkZ = context.chunkPos().getMiddleBlockZ();
      RandomSource random = context.random();

      int surfaceY = context.chunkGenerator().getFirstFreeHeight(
            chunkX, chunkZ, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

      int floorY = context.chunkGenerator().getFirstFreeHeight(
            chunkX, chunkZ, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
      if (surfaceY != floorY) {
         return Optional.empty();
      }

      BlockPos surfacePos = new BlockPos(chunkX, surfaceY, chunkZ);

      int massRadius = minRadius + random.nextInt(maxRadius - minRadius + 1);
      double craterRadius = massRadius * craterMultiplier;
      int burial = (int) Math.round(massRadius * 0.70);
      double coreRadius = Math.max(2.5, massRadius * 0.42);

      int lobeCount = 5 + random.nextInt(4);
      double[][] lobes = new double[lobeCount][4];
      for (int i = 0; i < lobeCount; i++) {
         double lx = random.nextFloat() * 2 - 1;
         double ly = random.nextFloat() * 2 - 1;
         double lz = random.nextFloat() * 2 - 1;
         double len = Math.sqrt(lx * lx + ly * ly + lz * lz);
         if (len < 1e-4) len = 1;
         double sign = random.nextFloat() < 0.7F ? 1.0 : -1.0;
         double amount = sign * (massRadius * (0.10 + random.nextFloat() * 0.22));
         lobes[i] = new double[]{lx / len, ly / len, lz / len, amount};
      }

      int oreSpread = Math.max(2, (int) coreRadius);
      List<int[]> oreBlobs = new ArrayList<>();
      int placed = 0, attempts = 0;
      while (placed < oreCount && attempts < oreCount * 8) {
         attempts++;
         int ox = random.nextInt(oreSpread) - oreSpread / 2;
         int oy = -random.nextInt(Math.max(2, oreSpread / 2));
         int oz = random.nextInt(oreSpread) - oreSpread / 2;
         int blobRadius = 1 + random.nextInt(2);
         oreBlobs.add(new int[]{ox, oy, oz, blobRadius});
         placed++;
      }

      return Optional.of(new GenerationStub(surfacePos, builder ->
            builder.addPiece(new MeteoritePiece(surfacePos, massRadius, craterRadius, coreRadius, burial, lobes, oreBlobs))));
   }

   @Override
   public StructureType<?> type() {
      return ModStructureTypes.METEORITE.get();
   }
}

