package com.neutrinodust.useful_ores.block.rail;

import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

public class EnderiumRailLaunchEnforcer {

    private static final int REASSERT_TICKS = 5;
    private static final int ENTRY_STOP_TICKS = 10;
    private static final int EXIT_STOP_TICKS = 10;

    static final class PendingLaunch {
        final BlockPos landingPos;
        final Direction direction;
        final double speed;
        final double velocityY;
        int entryStopTicks = ENTRY_STOP_TICKS;
        int exitStopTicks = EXIT_STOP_TICKS;
        boolean landed;
        int ticksRemaining;

        PendingLaunch(BlockPos landingPos, Direction direction, double speed, double velocityY, int ticksRemaining) {
            this.landingPos = landingPos.immutable();
            this.direction = direction;
            this.speed = speed;
            this.velocityY = velocityY;
            this.ticksRemaining = ticksRemaining;
        }
    }

    public EnderiumRailLaunchEnforcer() {
    }

    public static boolean queueTeleport(ServerLevel level, UUID cartId, BlockPos landingPos, Direction direction, double speed, double velocityY) {
        EnderiumPendingLaunchData data = EnderiumPendingLaunchData.get(level);
        if (data.pending.containsKey(cartId)) return false;
        data.pending.put(cartId, new PendingLaunch(landingPos, direction, speed, velocityY, REASSERT_TICKS));
        data.setDirty();
        return true;
    }

    public void onServerTick(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        EnderiumPendingLaunchData data = EnderiumPendingLaunchData.get(overworld);
        Map<UUID, PendingLaunch> pending = data.pending;
        if (pending.isEmpty()) return;

        boolean changed = false;
        for (ServerLevel level : server.getAllLevels()) {
            if (pending.isEmpty()) break;
            for (UUID cartId : java.util.List.copyOf(pending.keySet())) {
                if (!(level.getEntity(cartId) instanceof AbstractMinecart cart)) continue;

                PendingLaunch launch = pending.get(cartId);
                if (launch == null) continue;

                if (launch.entryStopTicks > 0) {
                    cart.setDeltaMovement(Vec3.ZERO);
                    launch.entryStopTicks--;
                    changed = true;
                    continue;
                }

                if (!launch.landed) {
                    cart.setPos(launch.landingPos.getX() + 0.5, launch.landingPos.getY() + 0.125,
                            launch.landingPos.getZ() + 0.5);
                    cart.setOldPosAndRot();
                    EnderiumRailBlockEntity.markTeleported(cart.getUUID(), launch.landingPos, level.getGameTime());
                    launch.landed = true;
                    changed = true;
                }

                if (launch.exitStopTicks > 0) {
                    cart.setDeltaMovement(Vec3.ZERO);
                    launch.exitStopTicks--;
                    changed = true;
                    continue;
                }

                Vec3 forced = new Vec3(
                    launch.direction.getStepX() * launch.speed,
                    launch.velocityY,
                    launch.direction.getStepZ() * launch.speed
                );
                cart.setDeltaMovement(forced);
                cart.setYRot(launch.direction.toYRot());

                launch.ticksRemaining--;
                if (launch.ticksRemaining <= 0) {
                    pending.remove(cartId);
                }
                changed = true;
            }
        }

        if (changed) {
            data.setDirty();
        }
    }
}

