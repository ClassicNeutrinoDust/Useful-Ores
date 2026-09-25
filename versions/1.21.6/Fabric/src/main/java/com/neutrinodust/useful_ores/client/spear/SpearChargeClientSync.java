package com.neutrinodust.useful_ores.client.spear;

import com.neutrinodust.useful_ores.init.SpearTags;
import com.neutrinodust.useful_ores.network.SpearChargeFramePacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Client-side pre-collision motion sampler for the spear charge.
 *
 * The server owns contact detection and damage. This packet exists only because
 * the server cannot reconstruct the client's pre-collision movement after ground
 * or entity collision has already modified the authoritative delta movement.
 */
public final class SpearChargeClientSync {
    private SpearChargeClientSync() {}

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(SpearChargeClientSync::tick);
    }

    private static void tick(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        ItemStack stack = player.getUseItem();
        if (!player.isUsingItem() || !stack.is(SpearTags.SPEARS)) return;

        // START_CLIENT_TICK is deliberately used: (position - oldPosition) is the
        // completed movement of the previous frame, captured before the next local
        // collision/movement step. This matches the reference mod's proven velocity
        // sampling strategy without making the client authoritative over the victim.
        Vec3 velocity20 = new Vec3(
                (player.getX() - player.xo) * 20.0D,
                (player.getY() - player.yo) * 20.0D,
                (player.getZ() - player.zo) * 20.0D);

        ClientPlayNetworking.send(new SpearChargeFramePacket(
                velocity20.x, velocity20.y, velocity20.z, null));
    }
}
