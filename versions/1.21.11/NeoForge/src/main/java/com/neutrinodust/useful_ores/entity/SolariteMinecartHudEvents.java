package com.neutrinodust.useful_ores.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SolariteMinecartHudEvents {

    private static final double REACH = 5.0;

    private final Map<UUID, UUID> lastTarget = new HashMap<>();

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (player.getVehicle() instanceof SolariteBatteryMinecartEntity) {
            return;
        }

        ServerLevel level = player.level();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eye.add(look.scale(REACH));

        SolariteBatteryMinecartEntity target = pickCart(level, player, eye, end);

        UUID id = player.getUUID();
        UUID previous = lastTarget.get(id);
        UUID currentId = target == null ? null : target.getUUID();
        if (Objects.equals(previous, currentId)) return;

        if (target != null) {
            int pct = Math.round(target.getBatteryLevel() * 100f);
            player.displayClientMessage(
                    Component.literal("Solarite Battery Minecart - Battery: " + pct + "%"), true);
            lastTarget.put(id, currentId);
        } else {
            player.displayClientMessage(Component.empty(), true);
            lastTarget.remove(id);
        }
    }

    @Nullable
    private static SolariteBatteryMinecartEntity pickCart(ServerLevel level, ServerPlayer player, Vec3 eye, Vec3 end) {
        AABB searchBox = player.getBoundingBox().inflate(REACH);
        SolariteBatteryMinecartEntity closest = null;
        double closestDistSq = Double.MAX_VALUE;

        for (Entity e : level.getEntities(player, searchBox)) {
            if (!(e instanceof SolariteBatteryMinecartEntity cart)) continue;
            Vec3 hit = cart.getBoundingBox().inflate(0.3).clip(eye, end).orElse(null);
            if (hit == null) continue;
            double distSq = eye.distanceToSqr(hit);
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = cart;
            }
        }
        return closest;
    }
}

