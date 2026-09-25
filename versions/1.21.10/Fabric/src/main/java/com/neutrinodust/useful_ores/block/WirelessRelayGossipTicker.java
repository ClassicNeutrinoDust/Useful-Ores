package com.neutrinodust.useful_ores.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.Set;

public class WirelessRelayGossipTicker {

    private static final int BEACON_INTERVAL_TICKS = 200;

    private static long lastDrivenGameTime = -1;
    private static long lastBeaconGameTime = -1;

    private static MinecraftServer lastServer = null;

    public static void driveTick(MinecraftServer server) {
        if (server != lastServer) {

            lastServer = server;
            lastDrivenGameTime = -1;
            lastBeaconGameTime = -1;
        }
        ServerLevel overworld = server.overworld();
        if (overworld == null) return;
        long gameTime = overworld.getGameTime();
        if (gameTime == lastDrivenGameTime) return;
        lastDrivenGameTime = gameTime;

        WirelessRelayNetworkData registry = WirelessRelayNetworkData.get(overworld);

        if (lastBeaconGameTime < 0 || gameTime - lastBeaconGameTime >= BEACON_INTERVAL_TICKS) {
            lastBeaconGameTime = gameTime;
            refresh(server, registry.beaconSweep());
        }

        if (registry.hasPendingGossip()) {
            refresh(server, registry.drainGossip());
        }
    }

    private static void refresh(MinecraftServer server, Set<GlobalPos> changed) {
        for (GlobalPos pos : changed) {
            ServerLevel targetLevel = server.getLevel(pos.dimension());
            if (targetLevel == null) continue;
            BlockPos blockPos = pos.pos();

            if (targetLevel.isLoaded(blockPos)) {
                WirelessRedstoneRelayBlockEntity.pushUpdateTo(targetLevel, blockPos);
            }
        }
    }
}

