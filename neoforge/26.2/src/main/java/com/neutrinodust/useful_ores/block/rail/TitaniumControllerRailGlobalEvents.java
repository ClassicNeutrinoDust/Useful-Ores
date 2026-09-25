package com.neutrinodust.useful_ores.block.rail;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class TitaniumControllerRailGlobalEvents {

    @SubscribeEvent
    public void onPlace(BlockEvent.EntityPlaceEvent event) {
        Level level = (Level) event.getLevel();
        if (level.isClientSide()) return;
        BlockState placed = event.getPlacedBlock();
        if (!(placed.getBlock() instanceof BaseRailBlock)) return;
        TitaniumControllerRailBlock.enforceControllerPriority(level, event.getPos());
    }

    @SubscribeEvent
    public void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        Level level = (Level) event.getLevel();
        if (level.isClientSide()) return;

        BlockPos sourcePos = event.getPos();
        checkAndEnforce(level, sourcePos);

        for (Direction direction : event.getNotifiedSides()) {
            checkAndEnforce(level, sourcePos.relative(direction));
        }
    }

    private static void checkAndEnforce(Level level, BlockPos pos) {
        if (level.getBlockState(pos).getBlock() instanceof BaseRailBlock) {
            TitaniumControllerRailBlock.enforceControllerPriority(level, pos);
        }
    }
}

