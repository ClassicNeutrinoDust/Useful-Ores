package com.neutrinodust.useful_ores.bioluminescence;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.level.BlockEvent;

public class BioluminescentEvents {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        BlockPos pos = event.getPos();
        BioluminescentBlockData data = BioluminescentBlockData.get(serverLevel);
        if (data.isGlowing(pos)) {
            data.unmarkGlowing(pos);
            serverLevel.getLightEngine().checkBlock(pos);
            BioluminescentSyncEvents.sendRemovalToAll(serverLevel, pos);
        }
    }
}

