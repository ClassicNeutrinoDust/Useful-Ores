package com.neutrinodust.useful_ores.phosgene;

import net.minecraft.core.BlockPos;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ClientPhosgenePowderBlocks {
    private static final Map<Long, Integer> BLOCKS = Collections.synchronizedMap(new HashMap<>());
    private ClientPhosgenePowderBlocks() {}

    public static void fullSync(Iterable<com.neutrinodust.useful_ores.network.SyncPhosgenePowderBlocksPacket.Entry> entries) {
        synchronized (BLOCKS) {
            BLOCKS.clear();
            for (var e : entries) BLOCKS.put(e.position(), e.color());
        }
    }

    public static void add(Iterable<com.neutrinodust.useful_ores.network.SyncPhosgenePowderBlocksPacket.Entry> entries) {
        synchronized (BLOCKS) {
            for (var e : entries) BLOCKS.put(e.position(), e.color());
        }
    }

    public static void remove(Iterable<com.neutrinodust.useful_ores.network.SyncPhosgenePowderBlocksPacket.Entry> entries) {
        synchronized (BLOCKS) {
            for (var e : entries) BLOCKS.remove(e.position());
        }
    }

    public static void clear() {
        synchronized (BLOCKS) { BLOCKS.clear(); }
    }

    public static Map<Long, Integer> snapshot() {
        synchronized (BLOCKS) { return new HashMap<>(BLOCKS); }
    }
}

