package com.neutrinodust.useful_ores.farseeker;

import com.neutrinodust.useful_ores.init.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;

import java.util.Set;

public class FarseekerLootEvents {

    private static final Set<String> TARGET_LOOT_TABLES = Set.of(
            "minecraft:chests/end_city_treasure"
    );

    private static final int CRYSTAL_WEIGHT = 7;
    private static final int EMPTY_WEIGHT = 93;

    public static void register() {
        LootTableEvents.MODIFY.register((resourceKey, tableBuilder, source, registries) -> {
            if (!TARGET_LOOT_TABLES.contains(resourceKey.identifier().toString())) return;

            LootPool.Builder crystalPool = LootPool.lootPool()
                    .setRolls(Holder.direct(new ConstantValue(1)))
                    .add(LootItem.lootTableItem(ModItems.FARSEEKER_CRYSTAL.get()).setWeight(CRYSTAL_WEIGHT))
                    .add(EmptyLootItem.emptyItem().setWeight(EMPTY_WEIGHT));

            tableBuilder.withPool(crystalPool);
        });
    }
}

