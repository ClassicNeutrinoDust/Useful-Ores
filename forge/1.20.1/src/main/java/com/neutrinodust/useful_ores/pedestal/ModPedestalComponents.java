package com.neutrinodust.useful_ores.pedestal;
import com.neutrinodust.useful_ores.util.ItemState;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
public final class ModPedestalComponents {
 public static final String LINKED_PEDESTAL = "LinkedPedestal";
 private ModPedestalComponents() {}
 public static GlobalPos get(ItemStack stack) { return ItemState.getGlobalPos(stack,LINKED_PEDESTAL); }
 public static void set(ItemStack stack,GlobalPos value) { ItemState.setGlobalPos(stack,LINKED_PEDESTAL,value); }
 public static void clear(ItemStack stack) { ItemState.remove(stack,LINKED_PEDESTAL); }
 public static void init(IEventBus bus) {}
}
