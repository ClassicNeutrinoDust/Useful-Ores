package com.neutrinodust.useful_ores.attribution;

import com.neutrinodust.useful_ores.util.ItemState;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;

public final class ModDataComponents {
 public static final String PAINITE_FURY_TOOL = "PainiteFuryTool";
 public static final String ATTRIBUTED_EFFECT = "AttributedEffect";
 private ModDataComponents() {}
 public static boolean isFuryTool(ItemStack stack) { return ItemState.getBoolean(stack, PAINITE_FURY_TOOL); }
 public static void setFuryTool(ItemStack stack, boolean value) { if(value) ItemState.setBoolean(stack, PAINITE_FURY_TOOL, true); else ItemState.remove(stack, PAINITE_FURY_TOOL); }
 public static String getAttributedEffect(ItemStack stack) { return ItemState.getString(stack, ATTRIBUTED_EFFECT); }
 public static void setAttributedEffect(ItemStack stack, String id) { ItemState.setString(stack, ATTRIBUTED_EFFECT, id); }
 public static void init(IEventBus bus) {}
}
