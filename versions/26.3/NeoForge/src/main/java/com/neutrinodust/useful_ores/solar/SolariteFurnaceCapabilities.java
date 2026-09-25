package com.neutrinodust.useful_ores.solar;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

public final class SolariteFurnaceCapabilities {

    private SolariteFurnaceCapabilities() {}

    public static void init(IEventBus eventBus) {
        eventBus.addListener(SolariteFurnaceCapabilities::register);
    }

    private static void register(RegisterCapabilitiesEvent event) {

        event.registerBlock(
                Capabilities.Item.BLOCK,
                (level, pos, state, be, side) -> {
                    if (be instanceof SolariteFurnaceBlockEntity furnace) {
                        return new WorldlyContainerWrapper(furnace, side);
                    }
                    return null;
                },
                ModItems.SOLARITE_FURNACE_BLOCK.get()
        );

        event.registerBlock(
                Capabilities.Item.BLOCK,
                (level, pos, state, be, side) -> {
                    BlockPos controllerPos = SolariteFurnacePartBlock.controllerPos(state, pos);
                    BlockEntity controllerBe = level.getBlockEntity(controllerPos);
                    if (controllerBe instanceof SolariteFurnaceBlockEntity furnace) {
                        return new WorldlyContainerWrapper(furnace, side);
                    }
                    return null;
                },
                ModItems.SOLARITE_FURNACE_PART.get()
        );
    }
}

