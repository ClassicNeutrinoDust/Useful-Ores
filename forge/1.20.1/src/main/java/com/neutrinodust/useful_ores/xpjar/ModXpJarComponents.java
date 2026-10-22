package com.neutrinodust.useful_ores.xpjar;
import com.neutrinodust.useful_ores.util.ItemState;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
public final class ModXpJarComponents {
 public static final String JAR_XP = "JarXp";
 private ModXpJarComponents() {}
 public static int getXp(ItemStack stack) { return ItemState.getInt(stack,JAR_XP,0); }
 public static void setXp(ItemStack stack,int value) { ItemState.setInt(stack,JAR_XP,value); }
 public static void clear(ItemStack stack) { ItemState.remove(stack,JAR_XP); }
 public static void init(IEventBus bus) {}
}
