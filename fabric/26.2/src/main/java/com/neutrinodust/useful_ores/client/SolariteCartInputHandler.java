package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import com.neutrinodust.useful_ores.network.SolariteCartSpeedInputPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class SolariteCartInputHandler {

   private SolariteCartInputHandler() {}

   private static int lastSent = 0;

   private static int ticksSinceSend = 0;

   public static void register() {
      ClientTickEvents.END_CLIENT_TICK.register(SolariteCartInputHandler::onClientTick);
   }

   private static void onClientTick(Minecraft mc) {
      LocalPlayer player = mc.player;
      if (player == null) return;

      int direction;
      if (!(player.getVehicle() instanceof SolariteBatteryMinecartEntity) || mc.gui.screen() != null) {
         direction = 0;
      } else {
         boolean forward = mc.options.keyUp.isDown();
         boolean backward = mc.options.keyDown.isDown();
         direction = forward == backward ? 0 : (forward ? 1 : -1);
      }

      ticksSinceSend++;
      boolean changed = direction != lastSent;
      boolean heartbeat = direction != 0 && ticksSinceSend >= 2;
      if ((changed || heartbeat) && mc.getConnection() != null) {
         ClientPlayNetworking.send(new SolariteCartSpeedInputPacket(direction));
         lastSent = direction;
         ticksSinceSend = 0;
      }
   }
}

