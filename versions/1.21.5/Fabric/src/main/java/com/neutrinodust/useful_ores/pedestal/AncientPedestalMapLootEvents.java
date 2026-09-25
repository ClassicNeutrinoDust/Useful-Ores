package com.neutrinodust.useful_ores.pedestal;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/** Adds the Ancient Pedestal map directly to the relevant chest loot tables. */
public final class AncientPedestalMapLootEvents {
    private static final String PEDESTAL_LOOT_TABLE = "useful_ores:chests/ancient_pedestal_chamber";
    private static final String ANCIENT_CITY_LOOT_TABLE = "minecraft:chests/ancient_city";

    private static LootPool.Builder buildMapPool(boolean excludeNearest, boolean ancientCity) {
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(
                        LootItem.lootTableItem(Items.MAP)
                                .setWeight(1)
                                .apply(new AncientPedestalMapLootFunction.Builder(excludeNearest))
                )
                .add(EmptyLootItem.emptyItem().setWeight(ancientCity ? 4 : 19));
    }

    private AncientPedestalMapLootEvents() {}

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            String id = key.location().toString();
            if (ANCIENT_CITY_LOOT_TABLE.equals(id)) {
                tableBuilder.pool(buildMapPool(false, true).build());
            } else if (PEDESTAL_LOOT_TABLE.equals(id)) {
                tableBuilder.pool(buildMapPool(true, false).build());
            }
        });
    }
}
