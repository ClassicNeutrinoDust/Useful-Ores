package com.neutrinodust.useful_ores.bioluminescence;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

public class BioluminescentEvents {

    @SubscribeEvent
    public void onBlockBreak(BreakBlockEvent event) {
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

