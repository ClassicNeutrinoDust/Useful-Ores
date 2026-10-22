package com.neutrinodust.useful_ores.painite;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class PainiteBreakSpeedEvents {
    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!PainitePower.isFuryActive(player) || !PainitePower.isPainiteCombatTool(player.getMainHandItem())) return;
        event.setNewSpeed(event.getNewSpeed() * 1.60F);
    }
}
