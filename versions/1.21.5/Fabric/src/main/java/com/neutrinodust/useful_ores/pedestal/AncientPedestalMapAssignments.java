package com.neutrinodust.useful_ores.pedestal;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** Persistent registry of Ancient Pedestal center coordinates already assigned to maps. */
public final class AncientPedestalMapAssignments extends SavedData {
    private final Set<Long> claimedTargets = new HashSet<>();

    /*
     * Older versions stored BlockPos#asLong values, which included Y and could
     * therefore represent the same pedestal twice. Convert legacy values to the
     * new X/Z-only identity while loading.
     */
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
            "ancient_pedestal_map_assignments",
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
