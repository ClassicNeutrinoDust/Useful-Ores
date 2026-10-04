package com.neutrinodust.useful_ores.solar;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public final class SolariteFurnaceCapabilities {
    private SolariteFurnaceCapabilities() {}

    public static void init(IEventBus eventBus) {
        eventBus.addListener(SolariteFurnaceCapabilities::register);
    }

    private static void register(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                (level, pos, state, be, side) -> sided(be, side),
                ModItems.SOLARITE_FURNACE_BLOCK.get()
        );

        event.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                (level, pos, state, be, side) -> {
                    BlockPos controllerPos = SolariteFurnacePartBlock.controllerPos(state, pos);
                    BlockEntity controllerBe = level.getBlockEntity(controllerPos);
                    return sided(controllerBe, side);
                },
                ModItems.SOLARITE_FURNACE_PART.get()
        );
    }

    private static IItemHandler sided(BlockEntity be, Direction side) {
        if (be instanceof SolariteFurnaceBlockEntity furnace) {
            return new SidedInvWrapper(furnace, side);
        }
        return null;
    }
}
