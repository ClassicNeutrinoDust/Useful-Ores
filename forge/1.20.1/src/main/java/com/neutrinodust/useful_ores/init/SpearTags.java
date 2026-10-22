package com.neutrinodust.useful_ores.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class SpearTags {
    public static final TagKey<Item> SPEARS = TagKey.create(
        Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "spears")
    );
    private SpearTags() {}
}
