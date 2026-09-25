package com.neutrinodust.useful_ores.farseeker;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import java.util.Set;

public class FarseekerLootEvents {

    private static final Set<String> TARGET_LOOT_TABLES = Set.of(
            "minecraft:chests/end_city_treasure"
    );

    private static final int CRYSTAL_WEIGHT = 7;
    private static final int EMPTY_WEIGHT = 93;

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        if (!TARGET_LOOT_TABLES.contains(event.getName().toString())) return;

        LootPool crystalPool = LootPool.lootPool()
                .setRolls(Holder.direct(new ConstantValue(1)))
                .add(LootItem.lootTableItem(ModItems.FARSEEKER_CRYSTAL.get()).setWeight(CRYSTAL_WEIGHT))
                .add(EmptyLootItem.emptyItem().setWeight(EMPTY_WEIGHT))
                .build();

        event.getTable().addPool(crystalPool);
    }
}

