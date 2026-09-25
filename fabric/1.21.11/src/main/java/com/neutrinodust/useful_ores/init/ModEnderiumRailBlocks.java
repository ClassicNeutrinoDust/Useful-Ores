package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.block.rail.EnderiumRailBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModEnderiumRailBlocks {

    public static final ModRegisters.RegisteredBlock<Block> ENDERIUM_RAIL = ModRegisters.registerBlock(
        "enderium_rail",
        props -> new EnderiumRailBlock(props),
        railProperties(),
        new Item.Properties()
    );

    private static BlockBehaviour.Properties railProperties() {
        return BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_CYAN)
            .noCollision()
            .strength(0.9F)
            .sound(SoundType.METAL)
            .pushReaction(PushReaction.DESTROY);
    }

    public static void init() {
    }
}

