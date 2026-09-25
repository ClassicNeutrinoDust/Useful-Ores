package com.neutrinodust.useful_ores.pedestal;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

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

    public static final SavedDataType<AncientPedestalMapAssignments> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("useful_ores", "ancient_pedestal_map_assignments").toString(),
            AncientPedestalMapAssignments::new,
            CODEC,
            null
    );

    private AncientPedestalMapAssignments() {}

    public static AncientPedestalMapAssignments get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
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
}
