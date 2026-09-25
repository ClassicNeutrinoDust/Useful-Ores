package com.neutrinodust.useful_ores.client.spear;

import com.neutrinodust.useful_ores.init.SpearTags;
import com.neutrinodust.useful_ores.network.SpearChargeFramePacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;








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

        
        
        
        
        Vec3 velocity20 = new Vec3(
                (player.getX() - player.xo) * 20.0D,
                (player.getY() - player.yo) * 20.0D,
                (player.getZ() - player.zo) * 20.0D);

        ClientPlayNetworking.send(new SpearChargeFramePacket(
                velocity20.x, velocity20.y, velocity20.z, null));
    }
}
