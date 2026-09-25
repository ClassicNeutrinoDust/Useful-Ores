package com.neutrinodust.useful_ores.phosgene;

import com.neutrinodust.useful_ores.network.SyncPhosgenePowderBlocksPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public final class PhosgenePowderSync {
    private PhosgenePowderSync() {}

    public static void sendFullSync(ServerPlayer player) {
        ServerLevel level = ((ServerLevel) player.level());
        List<SyncPhosgenePowderBlocksPacket.Entry> entries = new ArrayList<>();
        for (var e : PhosgenePowderData.get(level).snapshot().entrySet()) {
            entries.add(new SyncPhosgenePowderBlocksPacket.Entry(e.getKey(), e.getValue().color()));
        }
        ServerPlayNetworking.send(player,
                new SyncPhosgenePowderBlocksPacket(entries, SyncPhosgenePowderBlocksPacket.Mode.FULL));
    }

    public static void sendAppliedToAll(ServerLevel level, BlockPos pos, int color) {
        SyncPhosgenePowderBlocksPacket packet = new SyncPhosgenePowderBlocksPacket(
                List.of(new SyncPhosgenePowderBlocksPacket.Entry(pos.asLong(), color)),
                SyncPhosgenePowderBlocksPacket.Mode.ADD);
        for (ServerPlayer player : PlayerLookup.world(level)) ServerPlayNetworking.send(player, packet);
    }

    public static void sendRemovalToAll(ServerLevel level, BlockPos pos) {
        SyncPhosgenePowderBlocksPacket packet = new SyncPhosgenePowderBlocksPacket(
                List.of(new SyncPhosgenePowderBlocksPacket.Entry(pos.asLong(), 0)),
                SyncPhosgenePowderBlocksPacket.Mode.REMOVE);
        for (ServerPlayer player : PlayerLookup.world(level)) ServerPlayNetworking.send(player, packet);
    }
}

