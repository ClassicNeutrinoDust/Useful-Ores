package com.neutrinodust.useful_ores.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "useful_ores", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InteractiveBlockMenuHandler {
    private InteractiveBlockMenuHandler() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        boolean solariteFurnace = state.getBlock() instanceof com.neutrinodust.useful_ores.solar.SolariteFurnaceBlock
                || state.getBlock() instanceof com.neutrinodust.useful_ores.solar.SolariteFurnacePartBlock;

        if (solariteFurnace && event.getEntity().isShiftKeyDown()) return;

        boolean interactive = solariteFurnace
                || state.getBlock() instanceof com.neutrinodust.useful_ores.filter.ArgentiteFilterBlock
                || state.getBlock() instanceof SuperBeaconBlock;
        if (!interactive) return;

        event.setUseItem(net.minecraftforge.eventbus.api.Event.Result.DENY);
        event.setUseBlock(net.minecraftforge.eventbus.api.Event.Result.ALLOW);
    }
}
