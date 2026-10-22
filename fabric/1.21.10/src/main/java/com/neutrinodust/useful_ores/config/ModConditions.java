package com.neutrinodust.useful_ores.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.neutrinodust.useful_ores.init.ModConfig;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

public final class ModConditions {

    public static final ResourceConditionType<ItemEnabled> ITEM_ENABLED =
        ResourceConditionType.create(ResourceLocation.fromNamespaceAndPath("useful_ores", "item_enabled"), ItemEnabled.CODEC);

    private ModConditions() {}

    public static void init() {
        ResourceConditions.register(ITEM_ENABLED);
    }

    public record ItemEnabled(String item) implements ResourceCondition {
        public static final MapCodec<ItemEnabled> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("item").forGetter(ItemEnabled::item)
        ).apply(i, ItemEnabled::new));

        @Override public ResourceConditionType<?> getType() { return ITEM_ENABLED; }
        @Override public boolean test(RegistryOps.RegistryInfoLookup registryLookup) { return ModConfig.isItemEnabled(item); }
    }
}
