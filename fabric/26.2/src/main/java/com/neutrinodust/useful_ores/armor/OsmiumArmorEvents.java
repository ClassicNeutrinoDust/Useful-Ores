package com.neutrinodust.useful_ores.armor;

import com.neutrinodust.useful_ores.init.ModItems;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OsmiumArmorEvents {

    public static final OsmiumArmorEvents INSTANCE = new OsmiumArmorEvents();

    private static final Identifier WATER_WALK_MODIFIER_ID =
        Identifier.fromNamespaceAndPath("useful_ores", "osmium_water_walk");

    private static final double SINK_ACCEL = 0.08;
    private static final double SINK_SPEED_CAP = 0.4;
    private static final double JUMP_VELOCITY = 0.55;
    private static final double JUMP_GRAVITY_STEP = 0.08;
    private static final int MAX_ASCEND_TICKS = 12;

    private final Map<UUID, Integer> ascendTicks = new HashMap<>();
    private final Map<UUID, Boolean> wasOnGround = new HashMap<>();

    public void onPlayerTickPre(Player player) {
        wasOnGround.put(player.getUUID(), player.onGround());
    }

    public void onPlayerTickPost(Player player) {
        boolean active = isWearingFullOsmium(player) && player.isInWater();

        updateWaterWalkModifier(player, active);

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

    private void updateWaterWalkModifier(Player player, boolean active) {
        AttributeInstance attribute = player.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY);
        if (attribute == null) return;

        boolean hasModifier = attribute.getModifier(WATER_WALK_MODIFIER_ID) != null;
        if (active && !hasModifier) {
            attribute.addTransientModifier(new AttributeModifier(
                WATER_WALK_MODIFIER_ID, 1.0, AttributeModifier.Operation.ADD_VALUE));
        } else if (!active && hasModifier) {
            attribute.removeModifier(WATER_WALK_MODIFIER_ID);
        }
    }

    private boolean isWearingFullOsmium(Player player) {
        return isOsmiumPiece(player.getItemBySlot(EquipmentSlot.HEAD), 8)
            && isOsmiumPiece(player.getItemBySlot(EquipmentSlot.CHEST), 9)
            && isOsmiumPiece(player.getItemBySlot(EquipmentSlot.LEGS), 10)
            && isOsmiumPiece(player.getItemBySlot(EquipmentSlot.FEET), 11);
    }

    private boolean isOsmiumPiece(ItemStack stack, int osmiumItemsIndex) {
        Item expected = ModItems.OSMIUM_ITEMS.get(osmiumItemsIndex).get();
        return stack.is(expected);
    }
}

