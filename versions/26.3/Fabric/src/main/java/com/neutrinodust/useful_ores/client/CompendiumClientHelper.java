package com.neutrinodust.useful_ores.client;

import net.minecraft.client.Minecraft;

public class CompendiumClientHelper {
   public static void openBook() {
      Minecraft.getInstance().gui.setScreen(new CompendiumScreen());
   }
}

