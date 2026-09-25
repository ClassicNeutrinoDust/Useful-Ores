package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.network.CancelRelaySelectionPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = "useful_ores", value = Dist.CLIENT)
public final class WirelessRelayKeyHandler {

   public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(
         Identifier.fromNamespaceAndPath("useful_ores", "keys")
   );

   public static final KeyMapping CANCEL_RELAY_KEY = new KeyMapping(
         "key.useful_ores.cancel_relay_selection",
         KeyConflictContext.IN_GAME,
         InputConstants.getKey("key.keyboard.c"),
         CATEGORY
   );

   private WirelessRelayKeyHandler() {}


   @SubscribeEvent
   public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
      event.registerCategory(CATEGORY);
      event.register(CANCEL_RELAY_KEY);
   }

   @SubscribeEvent
   private static void onPlayerTick(PlayerTickEvent.Post event) {
      if (!(event.getEntity() instanceof net.minecraft.client.player.LocalPlayer)) return;
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen != null) return;

      while (CANCEL_RELAY_KEY.consumeClick()) {
         if (mc.getConnection() != null) {
            mc.getConnection().send(new ServerboundCustomPayloadPacket(new CancelRelaySelectionPacket()));
         }
      }
   }
}

