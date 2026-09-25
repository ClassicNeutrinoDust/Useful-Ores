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

public class ModVoidshardLightBlocks {

    private static final String NAME = "voidshard";
    private static final int COLOR = 0x9C22FF;
    private static final int LIGHT_LEVEL = 0;

    public static DeferredBlock<Block> VOIDSHARD_TORCH;
    public static DeferredBlock<Block> VOIDSHARD_WALL_TORCH;
    public static DeferredBlock<Block> VOIDSHARD_LANTERN;
    public static DeferredItem<Item> VOIDSHARD_TORCH_ITEM;

    static {
        DeferredBlock<Block> wallTorch = ModRegisters.registerBlock(
                NAME + "_wall_torch",
                props -> new ColoredWallTorchBlock(props, COLOR),
                BlockBehaviour.Properties.of()
                        .noCollision()
                        .instabreak()
                        .sound(SoundType.WOOD)
                        .lightLevel(state -> LIGHT_LEVEL)
                        .pushReaction(PushReaction.DESTROY)
        );
        DeferredBlock<Block> torch = ModRegisters.registerBlock(
                NAME + "_torch",
                props -> new ColoredTorchBlock(props, COLOR),
                BlockBehaviour.Properties.of()
                        .noCollision()
                        .instabreak()
                        .sound(SoundType.WOOD)
                        .lightLevel(state -> LIGHT_LEVEL)
                        .pushReaction(PushReaction.DESTROY)
        );
        DeferredItem<Item> torchItem = ModRegisters.registerItem(
                NAME + "_torch",
                props -> new StandingAndWallBlockItem(torch.get(), wallTorch.get(), Direction.DOWN, props),
                new Item.Properties()
                        .rarity(Rarity.EPIC)

                        .useBlockDescriptionPrefix()
        );

        DeferredBlock<Block> lantern = ModRegisters.registerBlock(
                NAME + "_lantern",
                props -> new LanternBlock(props),
                BlockBehaviour.Properties.of()
                        .requiresCorrectToolForDrops()
                        .strength(3.5F)
                        .sound(SoundType.LANTERN)
                        .lightLevel(state -> LIGHT_LEVEL)
                        .noOcclusion(),
                new Item.Properties().rarity(Rarity.EPIC)
        );

        VOIDSHARD_TORCH = torch;
        VOIDSHARD_WALL_TORCH = wallTorch;
        VOIDSHARD_TORCH_ITEM = torchItem;
        VOIDSHARD_LANTERN = lantern;
    }

    public static void init(IEventBus bus) {

    }
}

