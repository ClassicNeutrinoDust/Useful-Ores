package com.neutrinodust.useful_ores.phosgene;

import com.neutrinodust.useful_ores.network.SyncPhosgenePowderBlocksPacket;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ClientPhosgenePowderBlocks {
    private static volatile Map<Long, Integer> BLOCKS = Map.of();

    private ClientPhosgenePowderBlocks() {}

    public static void fullSync(Iterable<SyncPhosgenePowderBlocksPacket.Entry> entries) {
        Map<Long, Integer> next = new HashMap<>();
        for (var e : entries) next.put(e.position(), e.color());
        BLOCKS = Collections.unmodifiableMap(next);
    }

    public static void add(Iterable<SyncPhosgenePowderBlocksPacket.Entry> entries) {
        Map<Long, Integer> next = new HashMap<>(BLOCKS);
        for (var e : entries) next.put(e.position(), e.color());
        BLOCKS = Collections.unmodifiableMap(next);
    }

    public static void remove(Iterable<SyncPhosgenePowderBlocksPacket.Entry> entries) {
        Map<Long, Integer> next = new HashMap<>(BLOCKS);
        for (var e : entries) next.remove(e.position());
        BLOCKS = Collections.unmodifiableMap(next);
    }

    public static void clear() {
        BLOCKS = Map.of();
    }

    public static Map<Long, Integer> snapshot() {
        return BLOCKS;
    }
}

