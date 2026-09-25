package com.neutrinodust.useful_ores.block.rail;

import java.util.Map;
import java.util.UUID;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

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

    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractMinecart cart)) return;

        Level level = cart.level();
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;

        EnderiumPendingLaunchData data = EnderiumPendingLaunchData.get(serverLevel);
        Map<UUID, PendingLaunch> pending = data.pending;
        if (pending.isEmpty()) return;

        PendingLaunch launch = pending.get(cart.getUUID());
        if (launch == null) return;

        if (launch.entryStopTicks > 0) {
            cart.setDeltaMovement(Vec3.ZERO);
            launch.entryStopTicks--;
            data.setDirty();
            return;
        }

        if (!launch.landed) {
            cart.setPos(launch.landingPos.getX() + 0.5, launch.landingPos.getY() + 0.125,
                    launch.landingPos.getZ() + 0.5);
            cart.setOldPosAndRot();
            EnderiumRailBlockEntity.markTeleported(cart.getUUID(), launch.landingPos, level.getGameTime());
            launch.landed = true;
        }

        if (launch.exitStopTicks > 0) {
            cart.setDeltaMovement(Vec3.ZERO);
            launch.exitStopTicks--;
            data.setDirty();
            return;
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
            pending.remove(cart.getUUID());
        }
        data.setDirty();
    }
}

