package com.neutrinodust.useful_ores.bioluminescence;

import com.neutrinodust.useful_ores.network.SyncGlowingBlocksPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class BioluminescentSyncEvents {

    public static void sendFullSync(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        BioluminescentBlockData data = BioluminescentBlockData.get(serverLevel);
        ServerPlayNetworking.send(player,
                new SyncGlowingBlocksPacket(data.snapshot(), SyncGlowingBlocksPacket.Mode.FULL));
    }

    public static void sendDeltaToAll(ServerLevel level, BlockPos pos) {
        SyncGlowingBlocksPacket pkt = new SyncGlowingBlocksPacket(
                List.of(pos.asLong()), SyncGlowingBlocksPacket.Mode.ADD);
        for (ServerPlayer player : PlayerLookup.world(level)) {
            ServerPlayNetworking.send(player, pkt);
        }
    }

    public static void sendRemovalToAll(ServerLevel level, BlockPos pos) {
        SyncGlowingBlocksPacket pkt = new SyncGlowingBlocksPacket(
                List.of(pos.asLong()), SyncGlowingBlocksPacket.Mode.REMOVE);
        for (ServerPlayer player : PlayerLookup.world(level)) {
            ServerPlayNetworking.send(player, pkt);
        }
    }
}

