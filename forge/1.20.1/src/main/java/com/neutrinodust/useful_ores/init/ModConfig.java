package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.config.TomlConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class ModConfig {

    public static final Map<String, MaterialEntries> ENTRIES = new LinkedHashMap<>();

    public static final Map<String, BooleanValue> ORE_GENERATION = new LinkedHashMap<>();

    public static BooleanValue WIRELESS_RELAY_ALWAYS_ON_ORIGINS;
    private static List<String> disabledItems = List.of();
    private static boolean loaded = false;

    private static final String[] ORES = {
        "arcanite", "argentite", "chromite", "enderium", "extruding_crystal", "fulgurite",
        "ilmenite", "nyxium", "osmium", "painite", "phosgene", "scheelite",
        "solarite", "sperrylite", "voidshard", "zephyrite"
    };

    private static final String[] ORE_NOTES = {
        "Overworld", "Overworld", "Overworld", "The End", "Overworld (rarity depends on biome)", "Overworld",
        "Overworld", "Overworld (large, small and normal veins)", "Overworld", "The Nether", "Overworld", "Overworld",
        "The Nether", "Overworld", "The End", "Overworld"
    };

    private record MaterialDefaults(
        String name,
        int toolDurability, double attackDamage, double miningSpeed, int toolEnchantability,
        int armorDurabilityMultiplier, int helmet, int chestplate, int leggings, int boots, int body,
        int armorEnchantability, double toughness, double knockbackResistance) {}

    private static final List<MaterialDefaults> MATERIALS = List.of(
        new MaterialDefaults("chromite",   1650, 8.0,  7.0, 12, 27, 2, 7,  5, 2, 7,  12, 1.2, 0.15),
        new MaterialDefaults("nyxium",     2200, 10.0, 8.0, 15, 34, 3, 8,  5, 2, 8,  15, 2.2, 0.1),
        new MaterialDefaults("phosgene",   650,  6.0,  6.5, 6,  18, 2, 5,  3, 1, 5,  14, 0.4, 0.1),
        new MaterialDefaults("arcanite",   2150, 11.5, 8.5, 32, 29, 2, 7,  5, 2, 7,  32, 2.2, 0.0),
        new MaterialDefaults("fulgurite",  1750, 8.5,  7.5, 16, 26, 2, 7,  5, 2, 7,  16, 1.8, 0.2),
        new MaterialDefaults("osmium",     1900, 8.5,  6.0, 22, 30, 2, 8,  5, 2, 8,  20, 1.5, 0.35),
        new MaterialDefaults("solarite",   2400, 12.0, 15.0, 24, 30, 3, 10, 7, 3, 10, 20, 3.0, 0.15),
        new MaterialDefaults("sperrylite", 700,  7.0,  6.5, 29, 21, 2, 6,  4, 2, 6,  29, 0.3, 0.05),
        new MaterialDefaults("argentite",  380,  6.5,  5.0, 18, 19, 2, 5,  3, 1, 5,  18, 0.2, 0.0),
        new MaterialDefaults("zephyrite",  190,  4.0,  5.0, 9,  14, 1, 3,  2, 1, 3,  8,  0.0, 0.0),
        new MaterialDefaults("ilmenite",   2600, 9.0,  7.0, 10, 38, 3, 8,  5, 2, 8,  10, 3.2, 0.2),
        new MaterialDefaults("scheelite",  900,  6.5,  6.0, 10, 19, 2, 5,  3, 1, 5,  10, 0.1, 0.15),
        new MaterialDefaults("enderium",   2700, 13.5, 20.0, 26, 28, 4, 11, 7, 3, 11, 24, 3.4, 0.18),
        new MaterialDefaults("voidshard",  3000, 15.0, 25.0, 28, 30, 4, 13, 9, 3, 13, 27, 3.8, 0.22),
        new MaterialDefaults("subspace",   6400, 16.0, 32.0, 34, 62, 5, 15, 10, 4, 15, 34, 4.0, 0.4),
        new MaterialDefaults("lonsdaleite", 6200, 15.0, 25.0, 28, 58, 4, 13, 9, 3, 13, 27, 3.8, 0.22),
        new MaterialDefaults("painite",    1561, 3.0,  8.0, 10, 33, 3, 8,  6, 3, 8,  10, 2.0, 0.0)
    );

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

    public static void register() {
        TomlConfig c = new TomlConfig(
            FMLPaths.CONFIGDIR.get().resolve("useful_ores.toml"),
            "Useful Ores configuration. Restart the game after editing.",
            "This file is rewritten on every launch; invalid values are reset to their defaults.");

        c.push("general", "General gameplay settings");
        WIRELESS_RELAY_ALWAYS_ON_ORIGINS = new BooleanValue(c.bool("wireless_relay_always_on_origins", true,
            "Wireless relay origins keep working while nobody is nearby.",
            "  true  = an origin relay force-loads a small area and keeps sampling its redstone input 24/7.",
            "  false = it behaves like vanilla redstone and freezes while its chunk is unloaded",
            "          (cheaper: no permanently loaded chunks).",
            "Other relays in the network are unaffected by this setting."));
        c.pop();

        c.push("worldgen",
            "Ore generation. Set an ore to false to stop it generating in NEW chunks.",
            "Already generated chunks are not changed. The ore blocks and items still exist.");
        for (int i = 0; i < ORES.length; i++) {
            ORE_GENERATION.put(ORES[i], new BooleanValue(c.bool(ORES[i], true, ORE_NOTES[i])));
        }
        c.pop();

        c.push("items",
            "Item toggles. A disabled item cannot be crafted/smelted/cut (its recipes are removed)",
            "and is hidden from the Useful Ores creative tab. Existing copies in worlds are untouched.");
        disabledItems = c.stringList("disabled_items", List.of(),
            "Items to disable. Use item ids with or without the 'useful_ores:' prefix.",
            "'*' is a wildcard, so whole groups can be disabled at once. Examples:",
            "  \"solarite_sword\"          -> just that item",
            "  \"useful_ores:*_spear\"     -> every spear",
            "  \"solarite_*\"              -> everything made from solarite",
            "  \"*_helmet\", \"*_boots\"     -> all helmets and boots",
            "Default: empty list (everything enabled).");
        c.pop();

        c.push("materials", "Tool and armor stats, one section per material.");
        for (MaterialDefaults d : MATERIALS) {
            MaterialEntries e = new MaterialEntries();
            c.push(d.name());

            c.push("tool", "Tool stats (sword, pickaxe, axe, shovel, hoe, spear)");
            e.toolDurability = new IntValue(c.integer("durability", d.toolDurability(), 1, Integer.MAX_VALUE,
                "Uses before the tool breaks"));
            e.attackDamage = new DoubleValue(c.decimal("attack_damage", d.attackDamage(), 0.0, 4096.0,
                "Attack damage bonus (added to the tool's base damage)"));
            e.miningSpeed = new DoubleValue(c.decimal("mining_speed", d.miningSpeed(), 0.0, 100.0,
                "Block-breaking speed (vanilla: wood=2, stone=4, iron=6, diamond=8, netherite=9)"));
            e.toolEnchantability = new IntValue(c.integer("enchantability", d.toolEnchantability(), 0, 100,
                "Enchantability of the tools"));
            c.pop();

            c.push("armor", "Armor stats");
            e.armorDurabilityMultiplier = new IntValue(c.integer("durability_multiplier", d.armorDurabilityMultiplier(), 1, 4096,
                "Durability multiplier (scaled per slot by vanilla)"));
            e.helmetDefense = new IntValue(c.integer("helmet_defense", d.helmet(), 0, 30));
            e.chestplateDefense = new IntValue(c.integer("chestplate_defense", d.chestplate(), 0, 30));
            e.leggingsDefense = new IntValue(c.integer("leggings_defense", d.leggings(), 0, 30));
            e.bootsDefense = new IntValue(c.integer("boots_defense", d.boots(), 0, 30));
            e.bodyDefense = new IntValue(c.integer("body_defense", d.body(), 0, 30,
                "Defense of the body slot (horse/wolf armor)"));
            e.armorEnchantability = new IntValue(c.integer("enchantability", d.armorEnchantability(), 0, 100));
            e.toughness = new DoubleValue(c.decimal("toughness", d.toughness(), 0.0, 100.0,
                "Armor toughness (reduces the impact of high-damage hits)"));
            e.knockbackResistance = new DoubleValue(c.decimal("knockback_resistance", d.knockbackResistance(), 0.0, 1.0,
                "Knockback resistance (0.1 = 10%)"));
            c.pop();

            c.pop();
            ENTRIES.put(d.name(), e);
        }
        c.pop();

        c.save();
        loaded = true;
    }

    public static boolean isOreEnabled(String ore) {
        BooleanValue v = ORE_GENERATION.get(ore);
        return v == null || v.get();
    }

    public static boolean isItemEnabled(String itemId) {
        String path = itemId.startsWith("useful_ores:") ? itemId.substring("useful_ores:".length()) : itemId;
        if (!loaded) return true;
        for (String raw : disabledItems) {
            String pattern = raw.trim().toLowerCase(Locale.ROOT);
            if (pattern.startsWith("useful_ores:")) pattern = pattern.substring("useful_ores:".length());
            if (pattern.isEmpty()) continue;
            if (matches(pattern, path)) return false;
        }
        return true;
    }

    private static boolean matches(String glob, String text) {
        if (glob.indexOf('*') < 0) return glob.equals(text);
        StringBuilder regex = new StringBuilder();
        for (String part : glob.split("\\*", -1)) {
            regex.append(Pattern.quote(part)).append(".*");
        }
        regex.setLength(regex.length() - 2);
        return text.matches(regex.toString());
    }
}
