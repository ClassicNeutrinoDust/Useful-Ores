package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.network.CancelRelaySelectionPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public final class WirelessRelayKeyHandler {

   public static final String CATEGORY = "key.categories.useful_ores";

   public static final KeyMapping CANCEL_RELAY_KEY = new KeyMapping(
         "key.useful_ores.cancel_relay_selection",
         InputConstants.Type.KEYSYM,
         InputConstants.getKey("key.keyboard.c").getValue(),
         CATEGORY
   );

   private WirelessRelayKeyHandler() {}

   public static void register() {
      KeyBindingHelper.registerKeyBinding(CANCEL_RELAY_KEY);
      ClientTickEvents.END_CLIENT_TICK.register(WirelessRelayKeyHandler::onClientTick);
   }

   private static void onClientTick(Minecraft mc) {
      LocalPlayer player = mc.player;
      if (player == null) return;
      if (mc.screen != null) return;

      while (CANCEL_RELAY_KEY.consumeClick()) {
         if (mc.getConnection() != null) {
            ClientPlayNetworking.send(new CancelRelaySelectionPacket());
         }
      }
   }
}

