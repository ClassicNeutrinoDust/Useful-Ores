package com.neutrinodust.useful_ores.barrier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModBarrierSounds {

   public static final SoundEvent NYXIUM_DARK_BARRIER_PLACE = Registry.register(
      BuiltInRegistries.SOUND_EVENT,
      Identifier.fromNamespaceAndPath("useful_ores", "block.nyxium_dark_barrier.place"),
      SoundEvent.createVariableRangeEvent(
         Identifier.fromNamespaceAndPath("useful_ores", "block.nyxium_dark_barrier.place"))
   );

   public static void init() {
   }
}

