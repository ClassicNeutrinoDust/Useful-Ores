package com.neutrinodust.useful_ores.lock;

import com.neutrinodust.useful_ores.network.SyncLockedChestsPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class ChestLockSyncEvents {

    private ChestLockSyncEvents() {}

    public static void sendFullSync(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        ChestLockData data = ChestLockData.get(serverLevel);
        ServerPlayNetworking.send(player,
                new SyncLockedChestsPacket(data.snapshotPositions(serverLevel), SyncLockedChestsPacket.Mode.FULL));
    }

    public static void sendLockedToAll(ServerLevel level, BlockPos pos) {
        SyncLockedChestsPacket pkt = new SyncLockedChestsPacket(
                List.of(pos.asLong()), SyncLockedChestsPacket.Mode.ADD);
        for (ServerPlayer player : PlayerLookup.world(level)) {
            ServerPlayNetworking.send(player, pkt);
        }
    }

    public static void sendUnlockedToAll(ServerLevel level, BlockPos pos) {
        SyncLockedChestsPacket pkt = new SyncLockedChestsPacket(
                List.of(pos.asLong()), SyncLockedChestsPacket.Mode.REMOVE);
        for (ServerPlayer player : PlayerLookup.world(level)) {
            ServerPlayNetworking.send(player, pkt);
        }
    }
}

