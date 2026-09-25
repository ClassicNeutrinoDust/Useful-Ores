package com.neutrinodust.useful_ores.lock;

import com.neutrinodust.useful_ores.util.NbtCompat;

import net.minecraft.nbt.CompoundTag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChestLockData extends SavedData {

    public record LockEntry(UUID owner, UUID lockId, boolean bound) {
        public LockEntry withBound() {
            return new LockEntry(owner, lockId, true);
        }
    }

    private final Map<Long, LockEntry> locks = new HashMap<>();

    private record Entry(long pos, UUID owner, UUID lockId, boolean bound) {}

    private static Codec<UUID> UUID_CODEC() {
        return Codec.STRING.xmap(UUID::fromString, UUID::toString);
    }

    private static final Codec<Entry> ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("pos").forGetter(Entry::pos),
            UUID_CODEC().fieldOf("owner").forGetter(Entry::owner),
            UUID_CODEC().fieldOf("lock_id").forGetter(Entry::lockId),
            Codec.BOOL.optionalFieldOf("bound", false).forGetter(Entry::bound)
    ).apply(instance, Entry::new));

    public static final Codec<ChestLockData> CODEC = ENTRY_CODEC.listOf().xmap(
            list -> {
                ChestLockData data = new ChestLockData();
                for (Entry e : list) {
                    data.locks.put(e.pos(), new LockEntry(e.owner(), e.lockId(), e.bound()));
                }
                return data;
            },
            data -> {
                List<Entry> list = new ArrayList<>();
                for (Map.Entry<Long, LockEntry> e : data.locks.entrySet()) {
                    LockEntry rec = e.getValue();
                    list.add(new Entry(e.getKey(), rec.owner(), rec.lockId(), rec.bound()));
                }
                return list;
            }
    );

    public static final SavedData.Factory<ChestLockData> TYPE = new SavedData.Factory<>(
            ChestLockData::new,
            (tag, registries) -> NbtCompat.loadCodec(tag, CODEC, ChestLockData::new)
    );

    public static ChestLockData get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(TYPE, "chest_locks");
    }

    public LockEntry get(BlockPos pos) {
        return locks.get(pos.asLong());
    }

    public boolean isLocked(BlockPos pos) {
        return locks.containsKey(pos.asLong());
    }


    /**
     * Returns all block positions that belong to the chest container at {@code pos}.
     * A double chest is represented by both halves so the lock applies to the whole
     * shared inventory, regardless of which half the player clicks.
     */
    public static java.util.List<BlockPos> getContainerPositions(net.minecraft.world.level.Level level, BlockPos pos) {
        java.util.ArrayList<BlockPos> positions = new java.util.ArrayList<>(2);
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof net.minecraft.world.level.block.ChestBlock)) {
            positions.add(pos);
            return positions;
        }

        positions.add(pos);
        if (state.getValue(net.minecraft.world.level.block.ChestBlock.TYPE) != net.minecraft.world.level.block.state.properties.ChestType.SINGLE) {
            BlockPos other = pos.relative(net.minecraft.world.level.block.ChestBlock.getConnectedDirection(state));
            BlockState otherState = level.getBlockState(other);
            if (otherState.getBlock() == state.getBlock()
                    && otherState.getValue(net.minecraft.world.level.block.ChestBlock.TYPE) != net.minecraft.world.level.block.state.properties.ChestType.SINGLE) {
                positions.add(other);
            }
        }
        return positions;
    }

    /** Returns the lock entry for either half of a chest container. */
    public LockEntry getForContainer(net.minecraft.world.level.Level level, BlockPos pos) {
        for (BlockPos containerPos : getContainerPositions(level, pos)) {
            LockEntry entry = get(containerPos);
            if (entry != null) return entry;
        }
        return null;
    }

    /** Locks every half of a chest container using one shared lock identity. */
    public void lockContainer(net.minecraft.world.level.Level level, BlockPos pos, UUID owner, UUID lockId) {
        LockEntry entry = new LockEntry(owner, lockId, false);
        for (BlockPos containerPos : getContainerPositions(level, pos)) {
            locks.put(containerPos.asLong(), entry);
        }
        setDirty();
    }

    /** Marks every half of a chest container as bound, recovering old one-half entries too. */
    public void markContainerBound(net.minecraft.world.level.Level level, BlockPos pos) {
        LockEntry existing = getForContainer(level, pos);
        if (existing == null || existing.bound()) return;
        LockEntry boundEntry = existing.withBound();
        for (BlockPos containerPos : getContainerPositions(level, pos)) {
            locks.put(containerPos.asLong(), boundEntry);
        }
        setDirty();
    }

    /** Unlocks every half of a chest container. */
    public void unlockContainer(net.minecraft.world.level.Level level, BlockPos pos) {
        boolean changed = false;
        for (BlockPos containerPos : getContainerPositions(level, pos)) {
            changed |= locks.remove(containerPos.asLong()) != null;
        }
        if (changed) setDirty();
    }

    /** Returns whether either half of the chest container at {@code pos} is locked. */
    public boolean isLocked(net.minecraft.world.level.Level level, BlockPos pos) {
        for (BlockPos containerPos : getContainerPositions(level, pos)) {
            if (isLocked(containerPos)) return true;
        }
        return false;
    }

    /**
     * Returns locked positions expanded to both halves of any currently formed double chest.
     * Also migrates legacy one-half lock entries so old worlds get the corrected behavior.
     */
    public List<Long> snapshotPositions(net.minecraft.world.level.Level level) {
        java.util.ArrayList<Long> original = new java.util.ArrayList<>(locks.keySet());
        java.util.HashMap<Long, LockEntry> additions = new java.util.HashMap<>();
        for (Long packed : original) {
            LockEntry entry = locks.get(packed);
            if (entry == null) continue;
            for (BlockPos containerPos : getContainerPositions(level, BlockPos.of(packed))) {
                additions.putIfAbsent(containerPos.asLong(), entry);
            }
        }
        if (!additions.isEmpty()) {
            int before = locks.size();
            locks.putAll(additions);
            if (locks.size() != before) setDirty();
        }
        return new ArrayList<>(locks.keySet());
    }

    public List<Long> snapshotPositions() {
        return new ArrayList<>(locks.keySet());
    }

    public void lock(BlockPos pos, UUID owner, UUID lockId) {
        locks.put(pos.asLong(), new LockEntry(owner, lockId, false));
        setDirty();
    }

    public void markBound(BlockPos pos) {
        LockEntry rec = locks.get(pos.asLong());
        if (rec == null || rec.bound()) return;
        locks.put(pos.asLong(), rec.withBound());
        setDirty();
    }

    public void unlock(BlockPos pos) {
        if (locks.remove(pos.asLong()) != null) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return NbtCompat.saveCodec(this, CODEC);
    }
}

