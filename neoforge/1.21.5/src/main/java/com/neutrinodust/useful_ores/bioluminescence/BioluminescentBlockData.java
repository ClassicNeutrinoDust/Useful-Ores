package com.neutrinodust.useful_ores.bioluminescence;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
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

    public static final SavedDataType<BioluminescentBlockData> TYPE = new SavedDataType<>(
        "bioluminescent_blocks",
        BioluminescentBlockData::new,
        CODEC,
        null
    );

    public static BioluminescentBlockData get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(TYPE);
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
}

