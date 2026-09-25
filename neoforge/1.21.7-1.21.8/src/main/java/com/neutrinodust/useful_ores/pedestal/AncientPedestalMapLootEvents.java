package com.neutrinodust.useful_ores.pedestal;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;


public final class AncientPedestalMapLootEvents {
    private static final String PEDESTAL_LOOT_TABLE = "useful_ores:chests/ancient_pedestal_chamber";
    private static final String ANCIENT_CITY_LOOT_TABLE = "minecraft:chests/ancient_city";

    private static LootPool.Builder buildMapPool(boolean excludeNearest, boolean ancientCity) {
        LootPool.Builder pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(
                        LootItem.lootTableItem(Items.MAP)
                                .setWeight(1)
                                .apply(new AncientPedestalMapLootFunction.Builder(excludeNearest))
                );

        
        pool.add(EmptyLootItem.emptyItem().setWeight(ancientCity ? 4 : 19));
        return pool;
    }

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        if (!event.getName().getPath().startsWith("chests/")) return;

        String id = event.getName().toString();
        boolean pedestalChest = PEDESTAL_LOOT_TABLE.equals(id);
        boolean ancientCityChest = ANCIENT_CITY_LOOT_TABLE.equals(id);
        event.getTable().addPool(buildMapPool(pedestalChest, ancientCityChest).build());
    }
}
