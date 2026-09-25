package com.neutrinodust.useful_ores.phosgene;

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
import java.util.HashMap;
import java.util.Map;

public final class PhosgenePowderData extends SavedData {
    public static final long LIFETIME_TICKS = 24_000L;
    public static final int EFFECT_TICKS = 3 * 60 * 20;

    public record Applied(String potionId, long expiresAt, int color) {}

    private final Map<Long, Applied> applied = new HashMap<>();

    public static final Codec<PhosgenePowderData> CODEC = Codec.STRING.listOf().xmap(
            list -> {
                PhosgenePowderData data = new PhosgenePowderData();
                for (String encoded : list) {
                    String[] p = encoded.split("\\|", 4);
                    if (p.length != 4) continue;
                    try {
                        data.applied.put(Long.parseLong(p[0]),
                                new Applied(p[1], Long.parseLong(p[2]), Integer.parseInt(p[3])));
                    } catch (NumberFormatException ignored) {
                    }
                }
                return data;
            },
            data -> data.applied.entrySet().stream()
                    .map(e -> e.getKey() + "|" + e.getValue().potionId() + "|" +
                            e.getValue().expiresAt() + "|" + e.getValue().color())
                    .toList()
    );

    public static final SavedData.Factory<PhosgenePowderData> TYPE = new SavedData.Factory<>(
            PhosgenePowderData::new,
            (tag, registries) -> NbtCompat.loadCodec(tag, CODEC, PhosgenePowderData::new),
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    public static PhosgenePowderData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE, "phosgene_powder_blocks");
    }

    public Applied get(BlockPos pos) {
        return applied.get(pos.asLong());
    }

    public Map<Long, Applied> snapshot() {
        return new HashMap<>(applied);
    }

    public void apply(BlockPos pos, String potionId, long now, int color) {
        applied.put(pos.asLong(), new Applied(potionId, now + LIFETIME_TICKS, color));
        setDirty();
    }

    public void update(BlockPos pos, Applied applied) {
        this.applied.put(pos.asLong(), applied);
        setDirty();
    }

    public void remove(BlockPos pos) {
        if (applied.remove(pos.asLong()) != null) setDirty();
    }

    public void remove(long packedPos) {
        if (applied.remove(packedPos) != null) setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return NbtCompat.saveCodec(this, CODEC);
    }
}

