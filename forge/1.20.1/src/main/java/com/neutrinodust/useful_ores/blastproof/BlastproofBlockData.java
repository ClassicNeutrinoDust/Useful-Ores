package com.neutrinodust.useful_ores.blastproof;

import com.neutrinodust.useful_ores.util.NbtCompat;

import net.minecraft.nbt.CompoundTag;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlastproofBlockData extends SavedData {

   private final Set<Long> positions = new HashSet<>();

   public static final Codec<BlastproofBlockData> CODEC = Codec.LONG.listOf().xmap(
      list -> {
         BlastproofBlockData data = new BlastproofBlockData();
         data.positions.addAll(list);
         return data;
      },
      data -> new ArrayList<>(data.positions)
   );

   public static BlastproofBlockData get(ServerLevel level) {
      DimensionDataStorage storage = level.getDataStorage();
      return storage.computeIfAbsent(tag -> NbtCompat.loadCodec(tag, CODEC, BlastproofBlockData::new), BlastproofBlockData::new, "blastproof_blocks");
   }

   public void markBlastproof(BlockPos pos) {
      if (positions.add(pos.asLong())) {
         setDirty();
      }
   }

   public void unmark(BlockPos pos) {
      if (positions.remove(pos.asLong())) {
         setDirty();
      }
   }

   public boolean isBlastproof(BlockPos pos) {
      return positions.contains(pos.asLong());
   }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return NbtCompat.saveCodec(this, CODEC);
    }
}

