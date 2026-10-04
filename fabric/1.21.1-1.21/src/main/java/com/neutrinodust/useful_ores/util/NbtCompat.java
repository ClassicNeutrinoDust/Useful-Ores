package com.neutrinodust.useful_ores.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.Optional;


public final class NbtCompat {
    private NbtCompat() {}

    public static boolean getBooleanOr(CompoundTag tag, String key, boolean fallback) {
        return tag.contains(key, Tag.TAG_BYTE) ? tag.getBoolean(key) : fallback;
    }
    public static byte getByteOr(CompoundTag tag, String key, byte fallback) {
        return tag.contains(key, Tag.TAG_BYTE) ? tag.getByte(key) : fallback;
    }
    public static short getShortOr(CompoundTag tag, String key, short fallback) {
        return tag.contains(key, Tag.TAG_SHORT) ? tag.getShort(key) : fallback;
    }
    public static int getIntOr(CompoundTag tag, String key, int fallback) {
        return tag.contains(key, Tag.TAG_INT) ? tag.getInt(key) : fallback;
    }
    public static long getLongOr(CompoundTag tag, String key, long fallback) {
        return tag.contains(key, Tag.TAG_LONG) ? tag.getLong(key) : fallback;
    }
    public static float getFloatOr(CompoundTag tag, String key, float fallback) {
        return tag.contains(key, Tag.TAG_FLOAT) ? tag.getFloat(key) : fallback;
    }
    public static double getDoubleOr(CompoundTag tag, String key, double fallback) {
        return tag.contains(key, Tag.TAG_DOUBLE) ? tag.getDouble(key) : fallback;
    }
    public static String getStringOr(CompoundTag tag, String key, String fallback) {
        return tag.contains(key, Tag.TAG_STRING) ? tag.getString(key) : fallback;
    }
    public static ListTag getListOrEmpty(CompoundTag tag, String key, int elementType) {
        return tag.contains(key, Tag.TAG_LIST) ? tag.getList(key, elementType) : new ListTag();
    }
    public static CompoundTag getCompoundOrEmpty(ListTag list, int index) {
        return index >= 0 && index < list.size() ? list.getCompound(index) : new CompoundTag();
    }

    public static <T> Optional<T> readCodec(CompoundTag tag, String key, Codec<T> codec) {
        if (!tag.contains(key)) return Optional.empty();
        return codec.parse(NbtOps.INSTANCE, tag.get(key)).result();
    }

    public static <T> void store(CompoundTag tag, String key, Codec<T> codec, T value) {
        codec.encodeStart(NbtOps.INSTANCE, value).result().ifPresent(encoded -> tag.put(key, encoded));
    }

    public static <T> void storeNullable(CompoundTag tag, String key, Codec<T> codec, T value) {
        if (value == null) {
            tag.remove(key);
            return;
        }
        codec.encodeStart(NbtOps.INSTANCE, value).result().ifPresent(encoded -> tag.put(key, encoded));
    }

    public static <T extends net.minecraft.world.level.saveddata.SavedData> CompoundTag saveCodec(T value, Codec<T> codec) {
        CompoundTag root = new CompoundTag();
        codec.encodeStart(NbtOps.INSTANCE, value).result().ifPresent(encoded -> root.put("data", encoded));
        return root;
    }

    public static <T extends net.minecraft.world.level.saveddata.SavedData> T loadCodec(CompoundTag root, Codec<T> codec, java.util.function.Supplier<T> fallback) {
        Tag encoded = root.get("data");
        if (encoded == null) return fallback.get();
        return codec.parse(NbtOps.INSTANCE, encoded).result().orElseGet(fallback);
    }
}
