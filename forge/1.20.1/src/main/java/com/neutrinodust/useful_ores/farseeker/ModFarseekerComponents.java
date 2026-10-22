package com.neutrinodust.useful_ores.farseeker;
import com.neutrinodust.useful_ores.util.ItemState;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
public final class ModFarseekerComponents {
 public static final String FARSEEKER = "Farseeker";
 private ModFarseekerComponents() {}
 public static boolean has(ItemStack stack) { return ItemState.getBoolean(stack, FARSEEKER); }
 public static void set(ItemStack stack, boolean value) { if(value) ItemState.setBoolean(stack,FARSEEKER,true); else ItemState.remove(stack,FARSEEKER); }
 public static void init(IEventBus bus) {}
}
