package com.neutrinodust.useful_ores.barrier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBarrierSounds {
   public static final DeferredRegister<SoundEvent> SOUNDS =
      DeferredRegister.create(Registries.SOUND_EVENT, "useful_ores");

   public static final DeferredHolder<SoundEvent, SoundEvent> NYXIUM_DARK_BARRIER_PLACE =
      SOUNDS.register(
         "block.nyxium_dark_barrier.place",
         () -> SoundEvent.createVariableRangeEvent(
            Identifier.fromNamespaceAndPath("useful_ores", "block.nyxium_dark_barrier.place"))
      );

   public static void init(IEventBus bus) {
      SOUNDS.register(bus);
   }
}

