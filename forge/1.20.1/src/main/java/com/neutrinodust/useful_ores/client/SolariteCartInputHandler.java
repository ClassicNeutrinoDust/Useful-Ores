package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import com.neutrinodust.useful_ores.network.SolariteCartSpeedInputPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "useful_ores", value = Dist.CLIENT)
public final class SolariteCartInputHandler {

   private SolariteCartInputHandler() {}

   private static int lastSent = 0;

   private static int ticksSinceSend = 0;

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (!(event.player instanceof LocalPlayer player)) return;
      Minecraft mc = Minecraft.getInstance();

      SolariteBatteryMinecartEntity cart = player.getVehicle() instanceof SolariteBatteryMinecartEntity c ? c : null;

      int direction;
      if (cart == null || mc.screen != null) {
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
         com.neutrinodust.useful_ores.network.ModNetworking.sendToServer(new SolariteCartSpeedInputPacket(direction));
         lastSent = direction;
         ticksSinceSend = 0;
      }
   }
}

