package com.neutrinodust.useful_ores.bioluminescence;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BioluminescentEvents {

    public void onBlockBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        BioluminescentBlockData data = BioluminescentBlockData.get(serverLevel);
        if (data.isGlowing(pos)) {
            data.unmarkGlowing(pos);
            serverLevel.getLightEngine().checkBlock(pos);
            BioluminescentSyncEvents.sendRemovalToAll(serverLevel, pos);
        }
    }
}

