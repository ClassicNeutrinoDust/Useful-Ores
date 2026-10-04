package com.neutrinodust.useful_ores.bioluminescence;

import com.neutrinodust.useful_ores.network.SyncGlowingBlocksPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class BioluminescentSyncEvents {

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
        BioluminescentBlockData data = BioluminescentBlockData.get(serverLevel);
        PacketDistributor.sendToPlayer(player,
                new SyncGlowingBlocksPacket(data.snapshot(), SyncGlowingBlocksPacket.Mode.FULL));
    }

    public static void sendDeltaToAll(ServerLevel level, net.minecraft.core.BlockPos pos) {
        SyncGlowingBlocksPacket pkt = new SyncGlowingBlocksPacket(
                java.util.List.of(pos.asLong()), SyncGlowingBlocksPacket.Mode.ADD);
        PacketDistributor.sendToPlayersInDimension(level, pkt);
    }

    public static void sendRemovalToAll(ServerLevel level, net.minecraft.core.BlockPos pos) {
        SyncGlowingBlocksPacket pkt = new SyncGlowingBlocksPacket(
                java.util.List.of(pos.asLong()), SyncGlowingBlocksPacket.Mode.REMOVE);
        PacketDistributor.sendToPlayersInDimension(level, pkt);
    }
}

