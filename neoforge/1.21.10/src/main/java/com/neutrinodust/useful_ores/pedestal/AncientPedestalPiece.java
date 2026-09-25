package com.neutrinodust.useful_ores.pedestal;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import com.neutrinodust.useful_ores.init.ModStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;


public class AncientPedestalPiece extends StructurePiece {

   private final BlockPos centre;
   private final int radius;

   private static final ResourceKey<LootTable> ANCIENT_PEDESTAL_LOOT_TABLE = ResourceKey.create(
         Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("useful_ores", "chests/ancient_pedestal_chamber")
   );

   public AncientPedestalPiece(BlockPos centre, int radius) {
      super(ModStructurePieceTypes.ANCIENT_PEDESTAL.get(), 0, makeBoundingBox(centre, radius));
      this.centre = centre;
      this.radius = radius;
   }

   public AncientPedestalPiece(StructurePieceSerializationContext context, CompoundTag tag) {
      super(ModStructurePieceTypes.ANCIENT_PEDESTAL.get(), tag);
      this.centre = new BlockPos(tag.getIntOr("CX", 0), tag.getIntOr("CY", 0), tag.getIntOr("CZ", 0));
      this.radius = tag.getIntOr("Radius", 8);
   }

   
   public BlockPos getCentre() {
      return centre;
   }

   @Override
   public BlockPos getLocatorPosition() {
      return centre;
   }

   private static BoundingBox makeBoundingBox(BlockPos centre, int radius) {
      int r = radius + 2;
      return new BoundingBox(centre.getX() - r, centre.getY() - r, centre.getZ() - r,
            centre.getX() + r, centre.getY() + r, centre.getZ() + r);
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
      tag.putInt("CX", centre.getX());
      tag.putInt("CY", centre.getY());
      tag.putInt("CZ", centre.getZ());
      tag.putInt("Radius", radius);
   }

   @Override
   public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                            RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {

      BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();
      BlockState cryingObsidian = Blocks.CRYING_OBSIDIAN.defaultBlockState();
      BlockState air = Blocks.CAVE_AIR.defaultBlockState();

      double shellOuter = radius;
      double shellInner = radius - 1.6;
      double shellOuterSq = shellOuter * shellOuter;
      double shellInnerSq = shellInner * shellInner;

      BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

      for (int dx = -radius - 1; dx <= radius + 1; dx++) {
         for (int dy = -radius - 1; dy <= radius + 1; dy++) {
            for (int dz = -radius - 1; dz <= radius + 1; dz++) {
               mutable.setWithOffset(centre, dx, dy, dz);
               if (!box.isInside(mutable)) continue;

               double distSq = dx * dx + dy * dy + dz * dz;
               if (distSq > shellOuterSq) continue;

               if (distSq >= shellInnerSq) {

                  boolean crying = deterministicChance(mutable, 0.16);
                  level.setBlock(mutable, crying ? cryingObsidian : obsidian, 3);
               } else {

                  level.setBlock(mutable, air, 2);
               }
            }
         }
      }

      int floorY = centre.getY() - (int) Math.floor(shellInner) + 1;
      for (int dx = -3; dx <= 3; dx++) {
         for (int dz = -3; dz <= 3; dz++) {
            if (dx * dx + dz * dz > 9) continue;
            mutable.setWithOffset(centre, dx, floorY - centre.getY(), dz);
            if (!box.isInside(mutable)) continue;
            level.setBlock(mutable, obsidian, 3);
         }
      }

      BlockPos pedestalPos = new BlockPos(centre.getX(), floorY + 1, centre.getZ());
      if (box.isInside(pedestalPos)) {
         level.setBlock(pedestalPos, ModItems.ANCIENT_PEDESTAL.get().defaultBlockState(), 3);
      }

      BlockPos chestPos = pedestalPos.relative(net.minecraft.core.Direction.NORTH, 2);
      if (box.isInside(chestPos)) {
         level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
         BlockEntity be = level.getBlockEntity(chestPos);
         if (be instanceof RandomizableContainerBlockEntity container) {
            container.setLootTable(ANCIENT_PEDESTAL_LOOT_TABLE, random.nextLong());
         }
      }
   }

   private static boolean deterministicChance(BlockPos pos, double chance) {
      long h = pos.getX() * 341873128712L + pos.getY() * 132897987541L + pos.getZ() * 1013904223L;
      h = (h ^ (h >>> 33)) * 0xff51afd7ed558ccdL;
      h = (h ^ (h >>> 33));
      double unit = ((h >>> 11) & ((1L << 53) - 1)) / (double) (1L << 53);
      return unit < chance;
   }
}

