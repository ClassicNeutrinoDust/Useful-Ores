package com.neutrinodust.useful_ores.event;

import com.neutrinodust.useful_ores.init.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;

public class OreDropLootEvents {

    private static final String WITCH_TABLE      = "minecraft:entities/witch";
    private static final String BLACKSMITH_TABLE = "minecraft:chests/village/village_weaponsmith";
    private static final String MINESHAFT_TABLE  = "minecraft:chests/abandoned_mineshaft";

    public static void register() {
        LootTableEvents.MODIFY.register((resourceKey, tableBuilder, source, registries) -> {
            String name = resourceKey.identifier().toString();

            switch (name) {
                case WITCH_TABLE -> {

                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(Holder.direct(new ConstantValue(1)))
                            .add(LootItem.lootTableItem(ModItems.ARCANITE_ITEMS.get(0).get()).setWeight(1))
                            .add(EmptyLootItem.emptyItem().setWeight(19)));

                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(Holder.direct(new ConstantValue(1)))
                            .add(LootItem.lootTableItem(ModItems.NYXIUM_ITEMS.get(0).get()).setWeight(3))
                            .add(EmptyLootItem.emptyItem().setWeight(97)));
                }
                case BLACKSMITH_TABLE -> {

                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(Holder.direct(new ConstantValue(1)))
                            .add(LootItem.lootTableItem(ModItems.ILMENITE_ITEMS.get(1).get()).setWeight(1))
                            .add(EmptyLootItem.emptyItem().setWeight(19)));
                }
                case MINESHAFT_TABLE -> {

                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(Holder.direct(new ConstantValue(1)))
                            .add(LootItem.lootTableItem(ModItems.PHOSGENE_ITEMS.get(0).get()).setWeight(1))
                            .add(EmptyLootItem.emptyItem().setWeight(19)));
                }
                default -> {  }
            }
        });
    }
}

