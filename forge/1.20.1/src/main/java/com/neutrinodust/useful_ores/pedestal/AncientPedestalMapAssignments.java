package com.neutrinodust.useful_ores.pedestal;

import com.neutrinodust.useful_ores.util.NbtCompat;

import net.minecraft.nbt.CompoundTag;

import com.mojang.serialization.Codec;
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
                data.claimedTargets.addAll(list);
                return data;
            },
            data -> new ArrayList<>(data.claimedTargets)
    );

    private AncientPedestalMapAssignments() {}

    public static AncientPedestalMapAssignments get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(tag -> NbtCompat.loadCodec(tag, CODEC, AncientPedestalMapAssignments::new), AncientPedestalMapAssignments::new, "ancient_pedestal_map_assignments");
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
    public CompoundTag save(CompoundTag tag) {
        return NbtCompat.saveCodec(this, CODEC);
    }
}
