package com.neutrinodust.useful_ores.bioluminescence;

import com.neutrinodust.useful_ores.util.NbtCompat;

import net.minecraft.nbt.CompoundTag;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BioluminescentBlockData extends SavedData {

    private final Set<Long> positions = new HashSet<>();

    public static final Codec<BioluminescentBlockData> CODEC = Codec.LONG.listOf().xmap(
        list -> {
            BioluminescentBlockData data = new BioluminescentBlockData();
            data.positions.addAll(list);
            return data;
        },
        data -> new ArrayList<>(data.positions)
    );

    public static final com.neutrinodust.useful_ores.util.ModSavedDataFactory<BioluminescentBlockData> TYPE = new com.neutrinodust.useful_ores.util.ModSavedDataFactory<>(
            BioluminescentBlockData::new,
            tag -> NbtCompat.loadCodec(tag, CODEC, BioluminescentBlockData::new)
    );

    public static BioluminescentBlockData get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return TYPE.get(storage, "bioluminescent_blocks");
    }

    public void markGlowing(BlockPos pos) {
        if (positions.add(pos.asLong())) {
            setDirty();
        }
    }

    public void unmarkGlowing(BlockPos pos) {
        if (positions.remove(pos.asLong())) {
            setDirty();
        }
    }

    public boolean isGlowing(BlockPos pos) {
        return positions.contains(pos.asLong());
    }

    public List<Long> snapshot() {
        return new ArrayList<>(positions);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return NbtCompat.saveCodec(this, CODEC);
    }
}

