package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.bioluminescence.ClientGlowingBlocks;
import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import com.neutrinodust.useful_ores.lock.ClientLockedChests;
import com.neutrinodust.useful_ores.network.SolariteCartStepsPacket;
import com.neutrinodust.useful_ores.network.SyncGlowingBlocksPacket;
import com.neutrinodust.useful_ores.network.SyncLockedChestsPacket;
import com.neutrinodust.useful_ores.network.SyncPhosgenePowderBlocksPacket;
import com.neutrinodust.useful_ores.phosgene.ClientPhosgenePowderBlocks;
import net.minecraft.client.Minecraft;

public final class ClientPacketHandlers {
    private ClientPacketHandlers() {}

    public static void handleSolariteCartSteps(SolariteCartStepsPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null
                && minecraft.level.getEntity(packet.entityId()) instanceof SolariteBatteryMinecartEntity cart) {
            cart.getBehavior().receiveSteps(packet.steps(), packet.reset());
        }
    }

    public static void handleGlowingBlocks(SyncGlowingBlocksPacket packet) {
        switch (packet.mode()) {
            case FULL -> ClientGlowingBlocks.fullSync(packet.positions());
            case ADD -> ClientGlowingBlocks.merge(packet.positions());
            case REMOVE -> ClientGlowingBlocks.removeAll(packet.positions());
        }
    }

    public static void handleLockedChests(SyncLockedChestsPacket packet) {
        switch (packet.mode()) {
            case FULL -> ClientLockedChests.fullSync(packet.positions());
            case ADD -> ClientLockedChests.merge(packet.positions());
            case REMOVE -> ClientLockedChests.removeAll(packet.positions());
        }
    }

    public static void handlePhosgeneBlocks(SyncPhosgenePowderBlocksPacket packet) {
        switch (packet.mode()) {
            case FULL -> ClientPhosgenePowderBlocks.fullSync(packet.entries());
            case ADD -> ClientPhosgenePowderBlocks.add(packet.entries());
            case REMOVE -> ClientPhosgenePowderBlocks.remove(packet.entries());
        }
    }
}
