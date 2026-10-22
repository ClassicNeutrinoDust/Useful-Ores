package com.neutrinodust.useful_ores.lock;

import com.neutrinodust.useful_ores.network.SyncLockedChestsPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ChestLockSyncEvents {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        sendFullSync(player);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        sendFullSync(player);
    }

    private static void sendFullSync(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        ChestLockData data = ChestLockData.get(serverLevel);
        com.neutrinodust.useful_ores.network.ModNetworking.sendToPlayer(player,
                new SyncLockedChestsPacket(data.snapshotPositions(serverLevel), SyncLockedChestsPacket.Mode.FULL));
    }

    public static void sendLockedToAll(ServerLevel level, BlockPos pos) {
        SyncLockedChestsPacket pkt = new SyncLockedChestsPacket(
                java.util.List.of(pos.asLong()), SyncLockedChestsPacket.Mode.ADD);
        com.neutrinodust.useful_ores.network.ModNetworking.sendToDimension(level, pkt);
    }

    public static void sendUnlockedToAll(ServerLevel level, BlockPos pos) {
        SyncLockedChestsPacket pkt = new SyncLockedChestsPacket(
                java.util.List.of(pos.asLong()), SyncLockedChestsPacket.Mode.REMOVE);
        com.neutrinodust.useful_ores.network.ModNetworking.sendToDimension(level, pkt);
    }
}

