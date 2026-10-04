package com.neutrinodust.useful_ores.pedestal;

import net.minecraft.util.datafix.DataFixTypes;
import com.neutrinodust.useful_ores.util.NbtCompat;

import net.minecraft.nbt.CompoundTag;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public final class AncientPedestalMapAssignments extends SavedData {
    private final Set<Long> claimedTargets = new HashSet<>();

    




    public static final Codec<AncientPedestalMapAssignments> CODEC = Codec.LONG.listOf().xmap(
            list -> {
                AncientPedestalMapAssignments data = new AncientPedestalMapAssignments();
                for (long legacyKey : list) {
                    BlockPos oldPos = BlockPos.of(legacyKey);
                    data.claimedTargets.add(targetKey(oldPos.getX(), oldPos.getZ()));
                }
                return data;
            },
            data -> new ArrayList<>(data.claimedTargets)
    );

    public static final SavedData.Factory<AncientPedestalMapAssignments> TYPE = new SavedData.Factory<>(
            AncientPedestalMapAssignments::new,
            (tag, registries) -> NbtCompat.loadCodec(tag, CODEC, AncientPedestalMapAssignments::new),
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private AncientPedestalMapAssignments() {}

    public static AncientPedestalMapAssignments get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE, "ancient_pedestal_map_assignments");
    }

    public boolean claim(long targetKey) {
        if (!claimedTargets.add(targetKey)) return false;
        setDirty();
        return true;
    }

    public boolean isClaimed(long targetKey) {
        return claimedTargets.contains(targetKey);
    }

    private static long targetKey(int x, int z) {
        return ((long) x << 32) ^ (z & 0xffffffffL);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return NbtCompat.saveCodec(this, CODEC);
    }
}
