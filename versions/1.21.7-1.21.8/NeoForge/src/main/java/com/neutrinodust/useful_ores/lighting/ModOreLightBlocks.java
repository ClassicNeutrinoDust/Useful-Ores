package com.neutrinodust.useful_ores.lighting;

import com.neutrinodust.useful_ores.init.ModRegisters;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModOreLightBlocks {

    private record Entry(String name, int color) {}

    private static final Entry[] ENTRIES = {
        new Entry("chromite", 0xF6B700),
        new Entry("enderium", 0xE8FF00),
        new Entry("nyxium", 0x8282EA),
        new Entry("phosgene", 0x1D8B3F),
        new Entry("arcanite", 0xCA51FE),
        new Entry("fulgurite", 0xE29511),
        new Entry("osmium", 0xACE7FF),
        new Entry("solarite", 0xF24E0B),
        new Entry("sperrylite", 0xE9E2E2),
        new Entry("argentite", 0xDBF3FF),
        new Entry("zephyrite", 0x0094F1),
        new Entry("ilmenite", 0x79D5FF),
        new Entry("scheelite", 0xFFCB61)
    };

    private static final int LIGHT_LEVEL = 14;

    public static final Map<String, DeferredBlock<Block>> TORCHES = new LinkedHashMap<>();

    public static final Map<String, DeferredBlock<Block>> WALL_TORCHES = new LinkedHashMap<>();

    public static final Map<String, DeferredItem<Item>> TORCH_ITEMS = new LinkedHashMap<>();

    public static final Map<String, DeferredBlock<Block>> LANTERNS = new LinkedHashMap<>();

    public static final Map<String, Integer> COLORS = new LinkedHashMap<>();

    static {
        for (Entry e : ENTRIES) {
            String name = e.name();
            int color = e.color();

            DeferredBlock<Block> wallTorch = ModRegisters.registerBlock(
                    name + "_wall_torch",
                    props -> new ColoredWallTorchBlock(props, color),
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.WOOD)
                            .lightLevel(state -> LIGHT_LEVEL)
                            .pushReaction(PushReaction.DESTROY)
            );
            DeferredBlock<Block> torch = ModRegisters.registerBlock(
                    name + "_torch",
                    props -> new ColoredTorchBlock(props, color),
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.WOOD)
                            .lightLevel(state -> LIGHT_LEVEL)
                            .pushReaction(PushReaction.DESTROY)
            );
            DeferredItem<Item> torchItem = ModRegisters.registerItem(
                    name + "_torch",
                    props -> new StandingAndWallBlockItem(torch.get(), wallTorch.get(), Direction.DOWN, props),
                    new Item.Properties()
                            .rarity(Rarity.RARE)
                            .useBlockDescriptionPrefix()
            );

            DeferredBlock<Block> lantern = ModRegisters.registerBlock(
                    name + "_lantern",
                    props -> new LanternBlock(props),
                    BlockBehaviour.Properties.of()
                            .requiresCorrectToolForDrops()
                            .strength(3.5F)
                            .sound(SoundType.LANTERN)
                            .lightLevel(state -> LIGHT_LEVEL)
                            .noOcclusion(),
                    new Item.Properties().rarity(Rarity.RARE)
            );

            TORCHES.put(name, torch);
            WALL_TORCHES.put(name, wallTorch);
            TORCH_ITEMS.put(name, torchItem);
            LANTERNS.put(name, lantern);
            COLORS.put(name, color);
        }
    }

    public static void init(IEventBus bus) {
    }
}

