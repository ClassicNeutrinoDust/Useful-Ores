package com.neutrinodust.useful_ores.solar;
import com.neutrinodust.useful_ores.util.ItemState;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
public final class ModSolarComponents {
 public static final String BATTERY_ENERGY = "BatteryEnergy";
 private ModSolarComponents() {}
 public static int getEnergy(ItemStack stack) { return ItemState.getInt(stack,BATTERY_ENERGY,0); }
 public static void setEnergy(ItemStack stack,int value) { ItemState.setInt(stack,BATTERY_ENERGY,value); }
 public static void clear(ItemStack stack) { ItemState.remove(stack,BATTERY_ENERGY); }
 public static void init(IEventBus bus) {}
}
