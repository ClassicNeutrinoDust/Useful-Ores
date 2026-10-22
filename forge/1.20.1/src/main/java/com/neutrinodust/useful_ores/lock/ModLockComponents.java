package com.neutrinodust.useful_ores.lock;
import com.neutrinodust.useful_ores.util.ItemState;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
public final class ModLockComponents {
 public static final String BOUND_LOCK_ID = "BoundLockId";
 private ModLockComponents() {}
 public static String getBoundLockId(ItemStack stack) { return ItemState.getString(stack, BOUND_LOCK_ID); }
 public static void setBoundLockId(ItemStack stack, String id) { ItemState.setString(stack, BOUND_LOCK_ID, id); }
 public static void init(IEventBus bus) {}
}
