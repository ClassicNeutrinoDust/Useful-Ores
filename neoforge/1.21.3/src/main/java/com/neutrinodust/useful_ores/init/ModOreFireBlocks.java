package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.block.ColoredOreFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModOreFireBlocks {

    private record OreFireEntry(String name, int color) {}

    private static final OreFireEntry[] ENTRIES = {
        new OreFireEntry("chromite", 0xF6B700),
        new OreFireEntry("enderium", 0xE8FF00),
        new OreFireEntry("nyxium", 0x8282EA),
        new OreFireEntry("phosgene", 0x1D8B3F),
        new OreFireEntry("arcanite", 0xCA51FE),
        new OreFireEntry("fulgurite", 0xE29511),
        new OreFireEntry("osmium", 0xACE7FF),
        new OreFireEntry("solarite", 0xF24E0B),
        new OreFireEntry("sperrylite", 0xE9E2E2),
        new OreFireEntry("argentite", 0xDBF3FF),
        new OreFireEntry("zephyrite", 0x0094F1),
        new OreFireEntry("ilmenite", 0x79D5FF),
        new OreFireEntry("scheelite", 0xFFCB61),
        new OreFireEntry("voidshard", 0x9C22FF),
        new OreFireEntry("lonsdaleite", 0xCFF5FF)
    };

    public static final Map<String, DeferredBlock<Block>> FIRE_BLOCKS = new LinkedHashMap<>();

    public static final Map<String, Integer> COLORS = new LinkedHashMap<>();

    public static final Map<Block, DeferredBlock<Block>> IGNITABLE_TO_FIRE = new LinkedHashMap<>();

    static {
        for (OreFireEntry e : ENTRIES) {
            DeferredBlock<Block> fire = ModRegisters.registerBlock(
                e.name() + "_fire",
                props -> new ColoredOreFireBlock(props, e.color()),
                BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.WOOD)
                    .lightLevel(state -> 15)
                    .pushReaction(PushReaction.DESTROY)
            );
            FIRE_BLOCKS.put(e.name(), fire);
            COLORS.put(e.name(), e.color());
        }
    }

    public static void init(IEventBus bus) {
    }

    public static void buildIgnitionMap() {
        put("chromite", ModItems.CHROMITE_BLOCKS, ModItems.DEEPSLATE_CHROMITE_ORE);
        put("enderium", ModItems.ENDERIUM_BLOCKS, null); put("nyxium", ModItems.NYXIUM_BLOCKS, null);
        put("phosgene", ModItems.PHOSGENE_BLOCKS, ModItems.DEEPSLATE_PHOSGENE_ORE);
        put("arcanite", ModItems.ARCANITE_BLOCKS, ModItems.DEEPSLATE_ARCANITE_ORE);
        put("fulgurite", ModItems.FULGURITE_BLOCKS, ModItems.DEEPSLATE_FULGURITE_ORE);
        put("osmium", ModItems.OSMIUM_BLOCKS, ModItems.DEEPSLATE_OSMIUM_ORE); put("solarite", ModItems.SOLARITE_BLOCKS, null);
        put("sperrylite", ModItems.SPERRYLITE_BLOCKS, ModItems.DEEPSLATE_SPERRYLITE_ORE);
        put("argentite", ModItems.ARGENTITE_BLOCKS, ModItems.DEEPSLATE_ARGENTITE_ORE);
        put("zephyrite", ModItems.ZEPHYRITE_BLOCKS, ModItems.DEEPSLATE_ZEPHYRITE_ORE);
        put("ilmenite", ModItems.ILMENITE_BLOCKS, ModItems.DEEPSLATE_ILMENITE_ORE);
        put("scheelite", ModItems.SCHEELITE_BLOCKS, ModItems.DEEPSLATE_SCHEELITE_ORE);
        put("voidshard", ModItems.VOIDSHARD_BLOCKS, null); put("lonsdaleite", ModItems.LONSDALEITE_BLOCKS, null);
    }

    private static void put(String name, java.util.List<DeferredBlock<Block>> blocks, DeferredBlock<Block> deepslateOre) {
        DeferredBlock<Block> fire = FIRE_BLOCKS.get(name);

        for (DeferredBlock<Block> block : blocks) IGNITABLE_TO_FIRE.put(block.get(), fire);
        if (deepslateOre != null) {
            IGNITABLE_TO_FIRE.put(deepslateOre.get(), fire);
        }
    }
}

