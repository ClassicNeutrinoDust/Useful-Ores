package com.neutrinodust.useful_ores.init;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class ModConfig {

    public static final Map<String, MaterialEntries> ENTRIES = new LinkedHashMap<>();
    public static BooleanValue WIRELESS_RELAY_ALWAYS_ON_ORIGINS;

    private static Properties props;
    private static Path configFile;

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

    public static final class IntValue {
        private final int value;
        IntValue(int value) { this.value = value; }
        public Integer get() { return value; }
    }

    public static final class DoubleValue {
        private final double value;
        DoubleValue(double value) { this.value = value; }
        public Double get() { return value; }
    }

    public static final class BooleanValue {
        private final boolean value;
        BooleanValue(boolean value) { this.value = value; }
        public Boolean get() { return value; }
    }

    public static class MaterialEntries {
        public IntValue toolDurability;
        public DoubleValue attackDamage;
        public DoubleValue miningSpeed;
        public IntValue toolEnchantability;

        public IntValue armorDurabilityMultiplier;
        public IntValue helmetDefense;
        public IntValue chestplateDefense;
        public IntValue leggingsDefense;
        public IntValue bootsDefense;
        public IntValue bodyDefense;
        public IntValue armorEnchantability;
        public DoubleValue toughness;
        public DoubleValue knockbackResistance;
    }

    private static int readInt(String key, int def) {
        return Integer.parseInt(props.getProperty(key, String.valueOf(def)));
    }

    private static double readDouble(String key, double def) {
        return Double.parseDouble(props.getProperty(key, String.valueOf(def)));
    }

    private static boolean readBool(String key, boolean def) {
        return Boolean.parseBoolean(props.getProperty(key, String.valueOf(def)));
    }

    public static void register() {
        configFile = FabricLoader.getInstance().getConfigDir().resolve("useful_ores.properties");
        props = new Properties();

        if (Files.exists(configFile)) {
            try (InputStream in = Files.newInputStream(configFile)) {
                props.load(in);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read useful_ores.properties", e);
            }
        }

        WIRELESS_RELAY_ALWAYS_ON_ORIGINS = new BooleanValue(
            readBool("wireless_relay.always_on_origins", true)
        );
        props.setProperty("wireless_relay.always_on_origins",
            String.valueOf(WIRELESS_RELAY_ALWAYS_ON_ORIGINS.get()));

        for (Object[] row : DEFAULTS) {
            String name = (String) row[0];
            String p = "materials." + name + ".";
            MaterialEntries e = new MaterialEntries();

            e.toolDurability = new IntValue(readInt(p + "tool.durability", (Integer) row[1]));
            e.attackDamage = new DoubleValue(readDouble(p + "tool.attack_damage", (Double) row[2]));
            e.miningSpeed = new DoubleValue(readDouble(p + "tool.mining_speed", (Double) row[3]));
            e.toolEnchantability = new IntValue(readInt(p + "tool.enchantability", (Integer) row[4]));

            e.armorDurabilityMultiplier = new IntValue(readInt(p + "armor.durability_multiplier", (Integer) row[5]));
            e.helmetDefense = new IntValue(readInt(p + "armor.helmet_defense", (Integer) row[6]));
            e.chestplateDefense = new IntValue(readInt(p + "armor.chestplate_defense", (Integer) row[7]));
            e.leggingsDefense = new IntValue(readInt(p + "armor.leggings_defense", (Integer) row[8]));
            e.bootsDefense = new IntValue(readInt(p + "armor.boots_defense", (Integer) row[9]));
            e.bodyDefense = new IntValue(readInt(p + "armor.body_defense", (Integer) row[10]));
            e.armorEnchantability = new IntValue(readInt(p + "armor.enchantability", (Integer) row[11]));
            e.toughness = new DoubleValue(readDouble(p + "armor.toughness", (Double) row[12]));
            e.knockbackResistance = new DoubleValue(readDouble(p + "armor.knockback_resistance", (Double) row[13]));

            props.setProperty(p + "tool.durability", String.valueOf(e.toolDurability.get()));
            props.setProperty(p + "tool.attack_damage", String.valueOf(e.attackDamage.get()));
            props.setProperty(p + "tool.mining_speed", String.valueOf(e.miningSpeed.get()));
            props.setProperty(p + "tool.enchantability", String.valueOf(e.toolEnchantability.get()));
            props.setProperty(p + "armor.durability_multiplier", String.valueOf(e.armorDurabilityMultiplier.get()));
            props.setProperty(p + "armor.helmet_defense", String.valueOf(e.helmetDefense.get()));
            props.setProperty(p + "armor.chestplate_defense", String.valueOf(e.chestplateDefense.get()));
            props.setProperty(p + "armor.leggings_defense", String.valueOf(e.leggingsDefense.get()));
            props.setProperty(p + "armor.boots_defense", String.valueOf(e.bootsDefense.get()));
            props.setProperty(p + "armor.body_defense", String.valueOf(e.bodyDefense.get()));
            props.setProperty(p + "armor.enchantability", String.valueOf(e.armorEnchantability.get()));
            props.setProperty(p + "armor.toughness", String.valueOf(e.toughness.get()));
            props.setProperty(p + "armor.knockback_resistance", String.valueOf(e.knockbackResistance.get()));

            ENTRIES.put(name, e);
        }

        try (OutputStream out = Files.newOutputStream(configFile)) {
            props.store(out,
                "Useful Ores config. Tool durability/attack_damage/mining_speed/enchantability and\n"
                + "armor durability_multiplier/per-slot defense/toughness/knockback_resistance/enchantability,\n"
                + "one block of settings per ore. mining_speed is vanilla's real block-breaking-speed scale\n"
                + "(wood=2, stone=4, iron=6, diamond=8, netherite=9). Requires a restart to take effect.\n\n"
                + "wireless_relay.always_on_origins: if true (default), an origin wireless relay force-loads\n"
                + "a small area and keeps sampling its real redstone input 24/7 even with no players nearby.\n"
                + "If false, it behaves like ordinary redstone and freezes when its chunk unloads.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write useful_ores.properties", e);
        }
    }
}

