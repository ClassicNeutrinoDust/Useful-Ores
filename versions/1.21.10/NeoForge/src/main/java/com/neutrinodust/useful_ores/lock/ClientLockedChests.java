package com.neutrinodust.useful_ores.lock;

import net.minecraft.core.BlockPos;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ClientLockedChests {

    private ClientLockedChests() {}

    private static final Set<Long> POSITIONS = Collections.synchronizedSet(new HashSet<>());

    private static volatile long[] arrayCache = new long[0];
    private static volatile boolean dirty = true;

    public static void fullSync(List<Long> packed) {
        synchronized (POSITIONS) {
            POSITIONS.clear();
            POSITIONS.addAll(packed);
            dirty = true;
        }
    }

    public static void merge(List<Long> packed) {
        synchronized (POSITIONS) {
            POSITIONS.addAll(packed);
            dirty = true;
        }
    }

    public static void removeAll(List<Long> packed) {
        synchronized (POSITIONS) {
            POSITIONS.removeAll(packed);
            dirty = true;
        }
    }

    public static boolean isLocked(BlockPos pos) {
        return POSITIONS.contains(pos.asLong());
    }

    public static void clear() {
        synchronized (POSITIONS) {
            POSITIONS.clear();
            dirty = true;
        }
    }

    public static long[] snapshotArray() {
        if (!dirty) return arrayCache;
        synchronized (POSITIONS) {
            if (!dirty) return arrayCache;
            long[] arr = new long[POSITIONS.size()];
            int i = 0;
            for (long v : POSITIONS) arr[i++] = v;
            arrayCache = arr;
            dirty = false;
            return arr;
        }
    }
}

