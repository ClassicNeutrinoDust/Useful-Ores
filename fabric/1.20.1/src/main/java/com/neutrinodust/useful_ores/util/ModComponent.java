package com.neutrinodust.useful_ores.util;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public final class ModComponent<T> {
    public static final String ROOT = "useful_ores";

    private final String key;
    private final Codec<T> codec;

    private ModComponent(String key, Codec<T> codec) {
        this.key = key;
        this.codec = codec;
    }

    public static <T> ModComponent<T> of(String key, Codec<T> codec) {
        return new ModComponent<>(key, codec);
    }

    public String key() { return key; }

    public boolean has(ItemStack stack) {
        CompoundTag root = stack.getTagElement(ROOT);
        return root != null && root.contains(key);
    }

    @Nullable
    public T get(ItemStack stack) {
        CompoundTag root = stack.getTagElement(ROOT);
        if (root == null || !root.contains(key)) return null;
        Tag raw = root.get(key);
        return codec.parse(NbtOps.INSTANCE, raw).result().orElse(null);
    }

    public T getOrDefault(ItemStack stack, T fallback) {
        T v = get(stack);
        return v == null ? fallback : v;
    }

    public void set(ItemStack stack, T value) {
        if (value == null) { remove(stack); return; }
        codec.encodeStart(NbtOps.INSTANCE, value).result()
             .ifPresent(encoded -> stack.getOrCreateTagElement(ROOT).put(key, encoded));
    }

    public void remove(ItemStack stack) {
        CompoundTag root = stack.getTagElement(ROOT);
        if (root == null) return;
        root.remove(key);
        if (root.isEmpty()) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(ROOT);
                if (tag.isEmpty()) stack.setTag(null);
            }
        }
    }
}
