package com.neutrinodust.useful_ores.block.rail;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import com.neutrinodust.useful_ores.mixin.AbstractMinecartAccessor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.Vec3;







public class EnderiumRailLaunchEnforcer {
    static final class PendingLaunch {
        final BlockPos landingPos;
        final Direction direction;
        final double speed;
        final double velocityY;
        
        int entryStopTicks;
        int exitStopTicks;
        boolean landed;
        int ticksRemaining;

        PendingLaunch(BlockPos landingPos, Direction direction, double speed, double velocityY) {
            this.landingPos = landingPos.immutable();
            this.direction = direction;
            this.speed = speed;
            this.velocityY = velocityY;
        }

        PendingLaunch(BlockPos landingPos, Direction direction, double speed, double velocityY, int ticksRemaining) {
            this(landingPos, direction, speed, velocityY);
            this.ticksRemaining = ticksRemaining;
        }
    }

    public EnderiumRailLaunchEnforcer() {
    }

    public static boolean queueTeleport(ServerLevel level, UUID cartId, BlockPos landingPos, Direction direction,
                                        double speed, double velocityY) {
        EnderiumPendingLaunchData data = EnderiumPendingLaunchData.get(level);
        if (data.pending.containsKey(cartId)) return false;
        data.pending.put(cartId, new PendingLaunch(landingPos, direction, speed, velocityY, 10));
        
        if (level.getEntity(cartId) instanceof AbstractMinecart cart) {
            cart.setDeltaMovement(Vec3.ZERO);
        }
        data.setDirty();
        return true;
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        EnderiumPendingLaunchData data = EnderiumPendingLaunchData.get(server.overworld());
        Map<UUID, PendingLaunch> pending = data.pending;
        if (pending.isEmpty()) return;

        boolean changed = false;
        for (ServerLevel level : server.getAllLevels()) {
            for (UUID cartId : java.util.List.copyOf(pending.keySet())) {
                AbstractMinecart cart = null;
                if (level.getEntity(cartId) instanceof AbstractMinecart found) cart = found;
                if (cart == null) continue;

                PendingLaunch launch = pending.get(cartId);
                if (launch == null) continue;

                
                if (launch.ticksRemaining > 0) {
                    launch.ticksRemaining--;
                    cart.setDeltaMovement(Vec3.ZERO);
                    continue;
                }

                pending.remove(cartId);
                BlockPos source = cart.blockPosition();
                spawnTeleportParticles(level, source, false);

                
                
                
                
                float baseYaw = 180.0F - (float) (Math.atan2(launch.direction.getStepZ(), launch.direction.getStepX())
                        * 180.0D / Math.PI);
                float currentYaw = cart.getYRot();
                float noFlipDelta = Math.abs(Mth.wrapDegrees(baseYaw - currentYaw));
                float flipDelta = Math.abs(Mth.wrapDegrees(baseYaw + 180.0F - currentYaw));
                boolean flipped = flipDelta < noFlipDelta;
                float destinationYaw = flipped ? baseYaw + 180.0F : baseYaw;

                cart.setPos(launch.landingPos.getX() + 0.5D, launch.landingPos.getY() + 0.1D,
                        launch.landingPos.getZ() + 0.5D);
                ((AbstractMinecartAccessor) (Object) cart).useful_ores$setFlipped(flipped);
                cart.setYRot(destinationYaw % 360.0F);
                cart.setXRot(0.0F);
                cart.setOldPosAndRot();

                Vec3 forced = new Vec3(
                        launch.direction.getStepX() * launch.speed,
                        launch.velocityY,
                        launch.direction.getStepZ() * launch.speed);
                cart.setDeltaMovement(forced);
                EnderiumRailBlockEntity.markTeleported(cart.getUUID(), launch.landingPos, level.getGameTime());
                spawnTeleportParticles(level, launch.landingPos, true);
                changed = true;
            }
        }

        if (changed) data.setDirty();
    }

    private static void spawnTeleportParticles(ServerLevel level, BlockPos pos, boolean arrival) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.55D;
        double z = pos.getZ() + 0.5D;
        level.sendParticles(arrival ? ParticleTypes.REVERSE_PORTAL : ParticleTypes.PORTAL,
                x, y, z, 28, 0.35D, 0.35D, 0.35D, 0.02D);
        level.sendParticles(arrival ? ParticleTypes.PORTAL : ParticleTypes.REVERSE_PORTAL,
                x, y, z, 12, 0.18D, 0.18D, 0.18D, 0.01D);
    }
}
