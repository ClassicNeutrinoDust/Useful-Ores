package com.neutrinodust.useful_ores.armor;

import com.neutrinodust.useful_ores.init.ModItems;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OsmiumArmorEvents {

    private static final double SINK_ACCEL = 0.08;

    private static final double SINK_SPEED_CAP = 0.4;

    private static final double JUMP_VELOCITY = 0.55;

    private static final double JUMP_GRAVITY_STEP = 0.08;

    private static final int MAX_ASCEND_TICKS = 12;

    private final Map<UUID, Integer> ascendTicks = new HashMap<>();

    private final Map<UUID, Boolean> wasOnGround = new HashMap<>();

    @SubscribeEvent
    public void onPlayerTickPre(PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.START) return;
        Player player = event.player;
        wasOnGround.put(player.getUUID(), player.onGround());
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        Player player = event.player;
        boolean active = isWearingFullOsmium(player) && player.isInWater();

        if (!active) {
            ascendTicks.remove(player.getUUID());
            wasOnGround.remove(player.getUUID());
            return;
        }

        UUID id = player.getUUID();
        Vec3 vel = player.getDeltaMovement();
        boolean groundBeforeThisTick = wasOnGround.getOrDefault(id, player.onGround());

        if (ascendTicks.containsKey(id)) {

            int t = ascendTicks.get(id);
            double simulatedY = JUMP_VELOCITY - JUMP_GRAVITY_STEP * t;
            if (player.onGround() || simulatedY <= 0 || t >= MAX_ASCEND_TICKS) {

                ascendTicks.remove(id);
            } else {
                player.setDeltaMovement(vel.x, simulatedY, vel.z);
                ascendTicks.put(id, t + 1);
                return;
            }
        } else if (groundBeforeThisTick && !player.onGround()) {

            player.setDeltaMovement(vel.x, JUMP_VELOCITY, vel.z);
            ascendTicks.put(id, 1);
            return;
        }

        if (!player.onGround()) {
            Vec3 fallVel = player.getDeltaMovement();
            if (fallVel.y > -SINK_SPEED_CAP) {
                double newY = Math.max(fallVel.y - SINK_ACCEL, -SINK_SPEED_CAP);
                player.setDeltaMovement(fallVel.x, newY, fallVel.z);
            }
        }

        if (!ascendTicks.containsKey(id) && player.onGround() && player.isSwimming()) {
            player.setSwimming(false);
        }
    }

    private boolean isWearingFullOsmium(Player player) {
        return hasFullOsmiumSet(player);
    }

    public static boolean hasFullOsmiumSet(Player player) {
        return isOsmiumPieceStatic(player.getItemBySlot(EquipmentSlot.HEAD), 8)
            && isOsmiumPieceStatic(player.getItemBySlot(EquipmentSlot.CHEST), 9)
            && isOsmiumPieceStatic(player.getItemBySlot(EquipmentSlot.LEGS), 10)
            && isOsmiumPieceStatic(player.getItemBySlot(EquipmentSlot.FEET), 11);
    }

    private static boolean isOsmiumPieceStatic(ItemStack stack, int osmiumItemsIndex) {
        return stack.is(ModItems.OSMIUM_ITEMS.get(osmiumItemsIndex).get());
    }

    private boolean isOsmiumPiece(ItemStack stack, int osmiumItemsIndex) {
        Item expected = ModItems.OSMIUM_ITEMS.get(osmiumItemsIndex).get();
        return stack.is(expected);
    }
}

