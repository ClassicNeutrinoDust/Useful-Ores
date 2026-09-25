package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.block.rail.TitaniumControllerRailBlock;
import com.neutrinodust.useful_ores.block.rail.TitaniumRailBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModTitaniumRailBlocks {

    public static final ModRegisters.RegisteredBlock<Block> TITANIUM_RAIL = ModRegisters.registerBlock(
        "titanium_rail",
        props -> new TitaniumRailBlock(props),
        railProperties(),
        new Item.Properties()
    );

    public static final ModRegisters.RegisteredBlock<Block> TITANIUM_CONTROLLER_RAIL = ModRegisters.registerBlock(
        "titanium_controller_rail",
        props -> new TitaniumControllerRailBlock(props),
        railProperties(),
        new Item.Properties()
    );

    private static BlockBehaviour.Properties railProperties() {
        return BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .noCollision()
            .strength(0.7F)
            .sound(SoundType.METAL)
            .pushReaction(PushReaction.POPPED);
    }

    public static void init() {
    }
}

