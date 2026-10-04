package com.neutrinodust.useful_ores.init;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModConfig {

    public static final ModConfigSpec SPEC;
    public static final Map<String, MaterialEntries> ENTRIES = new LinkedHashMap<>();

    public static ModConfigSpec.BooleanValue WIRELESS_RELAY_ALWAYS_ON_ORIGINS;

    private static final Object[][] DEFAULTS = {

        {"chromite",  1650, 8.0,  7.0, 12, 27, 2, 7, 5, 2, 7, 12, 1.2, 0.15},

        {"nyxium",    2200, 10.0, 8.0, 15, 34, 3, 8, 5, 2, 8, 15, 2.2, 0.1},

        {"phosgene",   650, 6.0,  6.5,  6, 18, 2, 5, 3, 1, 5, 14, 0.4, 0.1},

        {"arcanite",  2150, 11.5, 8.5, 32, 29, 2, 7, 5, 2, 7, 32, 2.2, 0.0},

        {"fulgurite", 1750, 8.5,  7.5, 16, 26, 2, 7, 5, 2, 7, 16, 1.8, 0.2},

        {"osmium",    1900, 8.5,  6.0, 22, 30, 2, 8, 5, 2, 8, 20, 1.5, 0.35},

        {"solarite",  2400, 12.0, 15.0, 24, 30, 3, 10, 7, 3, 10, 20, 3.0, 0.15},

        {"sperrylite", 700, 7.0,  6.5, 29, 21, 2, 6, 4, 2, 6, 29, 0.3, 0.05},

        {"argentite",  380, 6.5,  5.0, 18, 19, 2, 5, 3, 1, 5, 18, 0.2, 0.0},

        {"zephyrite",  190, 4.0,  5.0,  9, 14, 1, 3, 2, 1, 3,  8, 0.0, 0.0},

        {"ilmenite",  2600, 9.0,  7.0, 10, 38, 3, 8, 5, 2, 8, 10, 3.2, 0.2},

        {"scheelite",  900, 6.5,  6.0, 10, 19, 2, 5, 3, 1, 5, 10, 0.1, 0.15},

        {"enderium",  2700, 13.5, 20.0, 26, 28, 4, 11, 7, 3, 11, 24, 3.4, 0.18},

        {"voidshard", 3000, 15.0, 25.0, 28, 30, 4, 13, 9, 3, 13, 27, 3.8, 0.22},

        {"subspace",  6400, 16.0, 32.0, 34, 62, 5, 15, 10, 4, 15, 34, 4.0, 0.4},

        {"lonsdaleite", 6200, 15.0, 25.0, 28, 58, 4, 13, 9, 3, 13, 27, 3.8, 0.22},
        {"painite", 1561, 3.0, 8.0, 10, 33, 3, 8, 6, 3, 8, 10, 2.0, 0.0},
    };

    public static class MaterialEntries {
        public ModConfigSpec.IntValue toolDurability;
        public ModConfigSpec.DoubleValue attackDamage;
        public ModConfigSpec.DoubleValue miningSpeed;
        public ModConfigSpec.IntValue toolEnchantability;

        public ModConfigSpec.IntValue armorDurabilityMultiplier;
        public ModConfigSpec.IntValue helmetDefense;
        public ModConfigSpec.IntValue chestplateDefense;
        public ModConfigSpec.IntValue leggingsDefense;
        public ModConfigSpec.IntValue bootsDefense;
        public ModConfigSpec.IntValue bodyDefense;
        public ModConfigSpec.IntValue armorEnchantability;
        public ModConfigSpec.DoubleValue toughness;
        public ModConfigSpec.DoubleValue knockbackResistance;
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("wireless_relay");
        WIRELESS_RELAY_ALWAYS_ON_ORIGINS = builder
            .comment(
                "If true (default): an origin relay (one with no upstream, feeding a network) force-loads",
                "a small area around itself and keeps sampling its real redstone input 24/7, even with no",
                "players anywhere near it - the whole network stays perfectly live at all times, matching a",
                "physical always-on transmitter.",
                "",
                "If false: an origin behaves like ordinary redstone - it freezes at its last known input the",
                "instant its own chunk unloads (no player nearby), and resumes sampling the moment a player",
                "brings it back into range. Lower always-on cost (no permanently-ticking chunks per origin),",
                "at the cost of an origin's live input pausing while unobserved, exactly like vanilla redstone",
                "would.",
                "",
                "Either way, every OTHER relay in the network (mid-chain stations, mesh receivers) is",
                "unaffected: they always show the correct current network state the moment THEY load, in",
                "either mode - this setting only controls the true origin's own real-time input sampling.")
            .define("always_on_origins", true);
        builder.pop();

        builder.push("materials");
        for (Object[] row : DEFAULTS) {
            String name = (String) row[0];
            MaterialEntries e = new MaterialEntries();

            builder.push(name);

            builder.push("tool");
            e.toolDurability = builder
                .comment("Uses before the tool breaks")
                .defineInRange("durability", (Integer) row[1], 1, Integer.MAX_VALUE);
            e.attackDamage = builder
                .comment("Base attack damage bonus (added to the tool's base damage)")
                .defineInRange("attack_damage", (Double) row[2], 0.0, 4096.0);
            e.miningSpeed = builder
                .comment("Block-breaking speed on vanilla's real scale (wood=2, stone=4, iron=6, diamond=8, netherite=9). Feeds the tool's actual mining-speed stat.")
                .defineInRange("mining_speed", (Double) row[3], 0.0, 100.0);
            e.toolEnchantability = builder
                .comment("How good this tool material is at receiving enchantments")
                .defineInRange("enchantability", (Integer) row[4], 0, 100);
            builder.pop();

            builder.push("armor");
            e.armorDurabilityMultiplier = builder
                .comment("Base durability multiplier, scaled per-slot internally by vanilla")
                .defineInRange("durability_multiplier", (Integer) row[5], 1, 4096);
            e.helmetDefense = builder.defineInRange("helmet_defense", (Integer) row[6], 0, 30);
            e.chestplateDefense = builder.defineInRange("chestplate_defense", (Integer) row[7], 0, 30);
            e.leggingsDefense = builder.defineInRange("leggings_defense", (Integer) row[8], 0, 30);
            e.bootsDefense = builder.defineInRange("boots_defense", (Integer) row[9], 0, 30);
            e.bodyDefense = builder
                .comment("Defense value used for body armor slot (horse/wolf armor items, if added)")
                .defineInRange("body_defense", (Integer) row[10], 0, 30);
            e.armorEnchantability = builder.defineInRange("enchantability", (Integer) row[11], 0, 100);
            e.toughness = builder
                .comment("Extra armor toughness, reduces effectiveness of high-damage hits")
                .defineInRange("toughness", (Double) row[12], 0.0, 100.0);
            e.knockbackResistance = builder
                .comment("Flat knockback resistance (0.1 = 10%)")
                .defineInRange("knockback_resistance", (Double) row[13], 0.0, 1.0);
            builder.pop();

            builder.pop();

            ENTRIES.put(name, e);
        }
        builder.pop();

        SPEC = builder.build();
    }

    public static void register(net.neoforged.fml.ModContainer container) {
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.STARTUP, SPEC);
    }
}

