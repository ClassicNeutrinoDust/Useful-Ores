package com.neutrinodust.useful_ores.init;

import com.neutrinodust.useful_ores.block.rail.SolariteRailBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModSolariteRailBlocks {

    public static final DeferredBlock<Block> SOLARITE_RAIL = ModRegisters.registerBlock(
        "solarite_rail",
        props -> new SolariteRailBlock(props),
        railProperties(),
        new Item.Properties()
    );

    private static BlockBehaviour.Properties railProperties() {
        return BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .noCollission()
            .strength(0.7F)
            .sound(SoundType.METAL)
            .pushReaction(PushReaction.DESTROY);
    }

    public static void init(IEventBus bus) {
    }
}

