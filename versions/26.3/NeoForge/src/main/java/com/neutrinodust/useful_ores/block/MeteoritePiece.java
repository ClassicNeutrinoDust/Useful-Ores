package com.neutrinodust.useful_ores.block;

import com.neutrinodust.useful_ores.init.ModItems;
import com.neutrinodust.useful_ores.init.ModStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.ArrayList;
import java.util.List;

public class MeteoritePiece extends StructurePiece {

   private final BlockPos origin;
   private final int massRadius;
   private final double craterRadius;
   private final double coreRadius;
   private final int burial;
   private final double[][] lobes;
   private final List<int[]> oreBlobs;

   public MeteoritePiece(BlockPos origin, int massRadius, double craterRadius, double coreRadius,
                          int burial, double[][] lobes, List<int[]> oreBlobs) {
      super(ModStructurePieceTypes.METEORITE.get(), 0, makeBoundingBox(origin, massRadius, craterRadius, burial));
      this.origin = origin;
      this.massRadius = massRadius;
      this.craterRadius = craterRadius;
      this.coreRadius = coreRadius;
      this.burial = burial;
      this.lobes = lobes;
      this.oreBlobs = oreBlobs;
   }

   public MeteoritePiece(StructurePieceSerializationContext context, CompoundTag tag) {
      super(ModStructurePieceTypes.METEORITE.get(), tag);
      this.origin = new BlockPos(tag.getIntOr("OX", 0), tag.getIntOr("OY", 0), tag.getIntOr("OZ", 0));
      this.massRadius = tag.getIntOr("MassRadius", 0);
      this.craterRadius = tag.getDoubleOr("CraterRadius", 0.0);
      this.coreRadius = tag.getDoubleOr("CoreRadius", 0.0);
      this.burial = tag.getIntOr("Burial", 0);
      this.lobes = readLobes(tag.getListOrEmpty("Lobes"));
      this.oreBlobs = readOreBlobs(tag.getListOrEmpty("OreBlobs"));
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
      tag.putInt("OX", origin.getX());
      tag.putInt("OY", origin.getY());
      tag.putInt("OZ", origin.getZ());
      tag.putInt("MassRadius", massRadius);
      tag.putDouble("CraterRadius", craterRadius);
      tag.putDouble("CoreRadius", coreRadius);
      tag.putInt("Burial", burial);
      tag.put("Lobes", writeLobes(lobes));
      tag.put("OreBlobs", writeOreBlobs(oreBlobs));
   }

   @Override
   public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                            RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {

      BlockState scorchedStone = ModItems.SCORCHED_STONE.get().defaultBlockState();
      BlockState scorchedDirt = ModItems.SCORCHED_DIRT.get().defaultBlockState();
      BlockState ash = ModItems.METEORITE_ASH_BLOCK.get().defaultBlockState();
      BlockState meteorite = ModItems.METEORITE_BLOCK.get().defaultBlockState();
      BlockState meteoriteCore = ModItems.METEORITE_CORE.get().defaultBlockState();
      BlockState lonsdaleiteOre = ModItems.LONSDALEITE_BLOCKS.get(1).get().defaultBlockState();

      double craterRadiusSq = craterRadius * craterRadius;
      double craterDepth = craterRadius * 0.55;
      int carveRadius = (int) Math.ceil(craterRadius) + 1;

      for (int dx = -carveRadius; dx <= carveRadius; dx++) {
         for (int dz = -carveRadius; dz <= carveRadius; dz++) {
            double horizDistSq = dx * dx + dz * dz;
            if (horizDistSq > craterRadiusSq) continue;
            double falloff = 1.0 - (horizDistSq / craterRadiusSq);
            int bowlDepth = (int) Math.round(craterDepth * falloff);
            if (bowlDepth <= 0) continue;

            BlockPos top = origin.offset(dx, 0, dz);
            boolean isNearRim = falloff < 0.18;
            BlockPos surface = findSurface(level, top);
            RandomSource colRandom = deterministicRandom(top, 0xA1L);

            for (int y = 0; y < bowlDepth; y++) {
               BlockPos p = surface.below(y);
               if (!box.isInside(p)) continue;
               boolean isFloor = y == bowlDepth - 1;
               if (isFloor) {
                  level.setBlock(p, chooseLining(colRandom, scorchedStone, scorchedDirt, ash, isNearRim), 3);
               } else {
                  level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
               }
            }

            if (falloff <= 0.02 && horizDistSq <= craterRadiusSq * 1.3) {
               RandomSource rimRandom = deterministicRandom(top, 0xB2L);
               if (rimRandom.nextFloat() < 0.5F) {
                  BlockPos rimGround = findSurface(level, top);
                  if (box.isInside(rimGround) && level.getBlockState(rimGround).getFluidState().isEmpty()) {
                     level.setBlock(rimGround, rimRandom.nextBoolean() ? scorchedDirt : ash, 3);
                  }
               }
            }
         }
      }

      BlockPos massCenter = origin.below(burial);
      for (int dx = -massRadius - 3; dx <= massRadius + 3; dx++) {
         for (int dy = -massRadius - 3; dy <= massRadius + 3; dy++) {
            for (int dz = -massRadius - 3; dz <= massRadius + 3; dz++) {
               double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
               if (dist > massRadius + 8) continue;

               double nx = dist > 1e-4 ? dx / dist : 0;
               double ny = dist > 1e-4 ? dy / dist : 0;
               double nz = dist > 1e-4 ? dz / dist : 0;

               double bump = 0;
               for (double[] lobe : lobes) {
                  double dot = nx * lobe[0] + ny * lobe[1] + nz * lobe[2];
                  if (dot > 0) bump += lobe[3] * dot * dot;
               }
               double effectiveRadius = massRadius + bump;

               BlockPos p = massCenter.offset(dx, dy, dz);
               if (!box.isInside(p)) continue;

               if (dist > effectiveRadius - 1.2 && dist <= effectiveRadius) {
                  if (deterministicRandom(p, 0xC3L).nextFloat() < 0.22F) continue;
               } else if (dist > effectiveRadius) {
                  continue;
               }

               boolean inCore = dist <= coreRadius;
               level.setBlock(p, inCore ? meteoriteCore : meteorite, 3);
            }
         }
      }

      BlockPos oreCenter = massCenter.below((int) (coreRadius * 0.35));
      for (int[] blob : oreBlobs) {
         BlockPos blobCenter = oreCenter.offset(blob[0], blob[1], blob[2]);
         int blobRadius = blob[3];
         for (int dx = -blobRadius; dx <= blobRadius; dx++) {
            for (int dy = -blobRadius; dy <= blobRadius; dy++) {
               for (int dz = -blobRadius; dz <= blobRadius; dz++) {
                  if (dx * dx + dy * dy + dz * dz > blobRadius * blobRadius + 1) continue;
                  BlockPos p = blobCenter.offset(dx, dy, dz);
                  if (!box.isInside(p)) continue;
                  BlockState existing = level.getBlockState(p);
                  if (existing.is(ModItems.METEORITE_BLOCK.get()) || existing.is(ModItems.METEORITE_CORE.get())) {
                     level.setBlock(p, lonsdaleiteOre, 3);
                  }
               }
            }
         }
      }
   }

   private static RandomSource deterministicRandom(BlockPos pos, long salt) {
      return RandomSource.create(Mth.getSeed(pos.getX(), pos.getY(), pos.getZ()) ^ salt);
   }

   private static BlockPos findSurface(WorldGenLevel level, BlockPos column) {
      BlockPos.MutableBlockPos pos = column.mutable();
      int safety = 0;
      while (level.getBlockState(pos).isAir() && pos.getY() > level.getMinY() + 1 && safety < 64) {
         pos.move(0, -1, 0);
         safety++;
      }
      return pos.immutable();
   }

   private static BlockState chooseLining(RandomSource random, BlockState scorchedStone, BlockState scorchedDirt, BlockState ash, boolean nearRim) {
      float roll = random.nextFloat();
      if (nearRim) {
         return roll < 0.55F ? ash : scorchedDirt;
      }
      return roll < 0.65F ? scorchedStone : (roll < 0.9F ? scorchedDirt : ash);
   }

   private static BoundingBox makeBoundingBox(BlockPos origin, int massRadius, double craterRadius, int burial) {
      int reach = (int) Math.ceil(Math.max(craterRadius, massRadius)) + 12;
      int minY = origin.getY() - burial - massRadius - 8;
      int maxY = origin.getY() + massRadius + 8;
      return new BoundingBox(origin.getX() - reach, minY, origin.getZ() - reach,
            origin.getX() + reach, maxY, origin.getZ() + reach);
   }

   private static ListTag writeLobes(double[][] lobes) {
      ListTag list = new ListTag();
      for (double[] l : lobes) {
         CompoundTag t = new CompoundTag();
         t.putDouble("x", l[0]);
         t.putDouble("y", l[1]);
         t.putDouble("z", l[2]);
         t.putDouble("a", l[3]);
         list.add(t);
      }
      return list;
   }

   private static double[][] readLobes(ListTag list) {
      double[][] lobes = new double[list.size()][4];
      for (int i = 0; i < list.size(); i++) {
         CompoundTag t = list.getCompoundOrEmpty(i);
         lobes[i] = new double[]{t.getDoubleOr("x", 0.0), t.getDoubleOr("y", 0.0), t.getDoubleOr("z", 0.0), t.getDoubleOr("a", 0.0)};
      }
      return lobes;
   }

   private static ListTag writeOreBlobs(List<int[]> blobs) {
      ListTag list = new ListTag();
      for (int[] b : blobs) {
         list.add(new IntArrayTag(b));
      }
      return list;
   }

   private static List<int[]> readOreBlobs(ListTag list) {
      List<int[]> blobs = new ArrayList<>();
      for (int i = 0; i < list.size(); i++) {
         blobs.add(((IntArrayTag) list.get(i)).getAsIntArray());
      }
      return blobs;
   }
}

