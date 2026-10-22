package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.network.CancelRelaySelectionPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

@Mod.EventBusSubscriber(modid = "useful_ores", value = Dist.CLIENT)
public final class WirelessRelayKeyHandler {

   public static final String CATEGORY = "key.categories.useful_ores";

   public static final KeyMapping CANCEL_RELAY_KEY = new KeyMapping(
         "key.useful_ores.cancel_relay_selection",
         KeyConflictContext.IN_GAME,
         InputConstants.getKey("key.keyboard.c"),
         CATEGORY
   );

   private WirelessRelayKeyHandler() {}

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (!(event.player instanceof net.minecraft.client.player.LocalPlayer)) return;
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen != null) return;

      while (CANCEL_RELAY_KEY.consumeClick()) {
         if (mc.getConnection() != null) {
            com.neutrinodust.useful_ores.network.ModNetworking.sendToServer(new CancelRelaySelectionPacket());
         }
      }
   }
}

