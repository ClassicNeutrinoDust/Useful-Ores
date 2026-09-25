package com.neutrinodust.useful_ores.phosgene;

import com.neutrinodust.useful_ores.network.SyncPhosgenePowderBlocksPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public final class PhosgenePowderSync {
    private PhosgenePowderSync() {}

    public static void sendFullSync(ServerPlayer player) {
        List<SyncPhosgenePowderBlocksPacket.Entry> entries = new ArrayList<>();
        for (var e : PhosgenePowderData.get(((ServerLevel) player.level())).snapshot().entrySet()) {
            entries.add(new SyncPhosgenePowderBlocksPacket.Entry(e.getKey(), e.getValue().color()));
        }
        PacketDistributor.sendToPlayer(player,
                new SyncPhosgenePowderBlocksPacket(entries, SyncPhosgenePowderBlocksPacket.Mode.FULL));
    }

    public static void sendAppliedToAll(ServerLevel level, BlockPos pos, int color) {
        SyncPhosgenePowderBlocksPacket packet = new SyncPhosgenePowderBlocksPacket(
                List.of(new SyncPhosgenePowderBlocksPacket.Entry(pos.asLong(), color)),
                SyncPhosgenePowderBlocksPacket.Mode.ADD);
        for (ServerPlayer player : level.players()) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }

    public static void sendRemovalToAll(ServerLevel level, BlockPos pos) {
        for (ServerPlayer player : level.players()) {
            PacketDistributor.sendToPlayer(player,
                    new SyncPhosgenePowderBlocksPacket(
                            List.of(new SyncPhosgenePowderBlocksPacket.Entry(pos.asLong(), 0)),
                            SyncPhosgenePowderBlocksPacket.Mode.REMOVE));
        }
    }
}

