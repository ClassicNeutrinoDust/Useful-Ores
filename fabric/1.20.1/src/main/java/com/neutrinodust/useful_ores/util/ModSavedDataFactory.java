package com.neutrinodust.useful_ores.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ModSavedDataFactory<T extends SavedData> {
    private final Supplier<T> creator;
    private final Function<CompoundTag, T> loader;

    public ModSavedDataFactory(Supplier<T> creator, Function<CompoundTag, T> loader) {
        this.creator = creator;
        this.loader = loader;
    }

    public T get(DimensionDataStorage storage, String name) {
        return storage.computeIfAbsent(loader, creator, name);
    }
}
