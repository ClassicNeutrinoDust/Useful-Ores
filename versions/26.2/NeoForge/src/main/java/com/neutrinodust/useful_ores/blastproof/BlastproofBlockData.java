package com.neutrinodust.useful_ores.blastproof;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;

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

   public static final SavedDataType<BlastproofBlockData> TYPE = new SavedDataType<>(
      Identifier.fromNamespaceAndPath("useful_ores", "blastproof_blocks"),
      BlastproofBlockData::new,
      CODEC
   );

   public static BlastproofBlockData get(ServerLevel level) {
      SavedDataStorage storage = level.getDataStorage();
      return storage.computeIfAbsent(TYPE);
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
}

