package com.neutrinodust.useful_ores.pedestal;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Adds Ancient Pedestal maps when chest loot is actually generated. */
public final class AncientPedestalMapLootEvents {
    private static final String PEDESTAL_LOOT_TABLE = "useful_ores:chests/ancient_pedestal_chamber";
    private static final String ANCIENT_CITY_LOOT_TABLE = "minecraft:chests/ancient_city";

    private AncientPedestalMapLootEvents() {}

    public static void register() {
        LootTableEvents.MODIFY_DROPS.register((entry, context, drops) -> {
            ResourceKey<LootTable> key = entry.unwrapKey().orElse(null);
            if (key == null || !key.identifier().getPath().startsWith("chests/")) return;
            if (context.getLevel().dimension() != Level.OVERWORLD) return;

            String id = key.identifier().toString();
            boolean pedestalChest = PEDESTAL_LOOT_TABLE.equals(id);
            boolean ancientCityChest = ANCIENT_CITY_LOOT_TABLE.equals(id);

            int denominator = ancientCityChest ? 5 : 20;
            if (context.getRandom().nextInt(denominator) != 0) return;

            var origin = context.getOptionalParameter(LootContextParams.ORIGIN);
            if (origin == null) return;

            var map = AncientPedestalMapLogic.createMap(
                    context.getLevel(),
                    (int) Math.floor(origin.x()),
                    (int) Math.floor(origin.z()),
                    pedestalChest
            );
            if (!map.isEmpty()) drops.add(map);
        });
    }
}
