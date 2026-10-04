package com.neutrinodust.useful_ores.client;

import net.minecraft.client.Minecraft;

public class CompendiumClientHelper {
   public static void openBook() {
      Minecraft.getInstance().setScreen(new CompendiumScreen());
   }
}

