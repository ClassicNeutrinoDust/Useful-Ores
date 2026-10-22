package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.block.ColoredCampfireBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModColoredCampfireBlocks {

    private record CampfireEntry(String name, int color) {}

    private static final CampfireEntry[] ENTRIES = {
        new CampfireEntry("chromite", 0xF6B700),
        new CampfireEntry("enderium", 0xE8FF00),
        new CampfireEntry("nyxium", 0x8282EA),
        new CampfireEntry("phosgene", 0x1D8B3F),
        new CampfireEntry("arcanite", 0xCA51FE),
        new CampfireEntry("fulgurite", 0xE29511),
        new CampfireEntry("osmium", 0xACE7FF),
        new CampfireEntry("solarite", 0xF24E0B),
        new CampfireEntry("sperrylite", 0xE9E2E2),
        new CampfireEntry("argentite", 0xDBF3FF),
        new CampfireEntry("zephyrite", 0x0094F1),
        new CampfireEntry("ilmenite", 0x79D5FF),
        new CampfireEntry("scheelite", 0xFFCB61),
        new CampfireEntry("voidshard", 0x9C22FF),
        new CampfireEntry("lonsdaleite", 0xCFF5FF)
    };

    public static final Map<String, ModRegisters.RegisteredBlock<Block>> CAMPFIRE_BLOCKS = new LinkedHashMap<>();

    public static final Map<String, Integer> COLORS = new LinkedHashMap<>();

    public static final Map<String, ModRegisters.RegisteredItem<Item>> INFUSED_COAL_ITEMS = new LinkedHashMap<>();

    public static ModRegisters.RegisteredBlock<Block> VOIDSHARD_CAMPFIRE;

    static {
        for (CampfireEntry e : ENTRIES) {

            boolean isVoidshard = e.name().equals("voidshard");

            ModRegisters.RegisteredBlock<Block> campfire = ModRegisters.registerBlock(
                e.name() + "_campfire",
                props -> new ColoredCampfireBlock(true, 1, props, e.color()),
                BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PODZOL)
                    .strength(2.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    .lightLevel(state -> isVoidshard ? 0 : (state.getValue(CampfireBlock.LIT) ? 15 : 0))
                    .ignitedByLava()
                    .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY),
                new Item.Properties()
            );
            CAMPFIRE_BLOCKS.put(e.name(), campfire);
            COLORS.put(e.name(), e.color());
            if (isVoidshard) {
                VOIDSHARD_CAMPFIRE = campfire;
            }

            ModRegisters.RegisteredItem<Item> infusedCoal = ModRegisters.registerItem(
                e.name() + "_infused_coal", Item::new, new Item.Properties()
            );
            INFUSED_COAL_ITEMS.put(e.name(), infusedCoal);
        }
    }

    public static void init() {
    }
}

