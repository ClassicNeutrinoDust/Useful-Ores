package com.neutrinodust.useful_ores.client;

import com.neutrinodust.useful_ores.entity.SolariteBatteryMinecartEntity;
import com.neutrinodust.useful_ores.network.SolariteCartSpeedInputPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "useful_ores", value = Dist.CLIENT)
public final class SolariteCartInputHandler {

   private SolariteCartInputHandler() {}

   private static int lastSent = 0;

   private static int ticksSinceSend = 0;


   @SubscribeEvent
   private static void onPlayerTick(PlayerTickEvent.Post event) {
      if (!(event.getEntity() instanceof LocalPlayer player)) return;
      Minecraft mc = Minecraft.getInstance();

      int direction;
      if (!(player.getVehicle() instanceof SolariteBatteryMinecartEntity) || mc.screen != null) {
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
         mc.getConnection().send(new ServerboundCustomPayloadPacket(new SolariteCartSpeedInputPacket(direction)));
         lastSent = direction;
         ticksSinceSend = 0;
      }
   }
}

