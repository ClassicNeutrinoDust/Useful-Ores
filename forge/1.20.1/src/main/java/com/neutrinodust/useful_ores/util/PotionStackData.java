package com.neutrinodust.useful_ores.util;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public final class PotionStackData {
 private static final String[] KEYS = {"Potion", "CustomPotionEffects", "CustomPotionColor", "CustomPotionName"};
 private PotionStackData() {}
 public static boolean hasContents(ItemStack stack) {
  if (!stack.hasTag()) return false;
  for (String key : KEYS) if (stack.getTag().contains(key)) return true;
  return false;
 }
 
 public static boolean isVanillaEffectPotion(ItemStack stack) {
  if (!(stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION))) return false;

  if (stack.hasTag() && stack.getTag().contains("CustomPotionEffects")) return false;
  Potion potion = potion(stack);
  ResourceLocation id = BuiltInRegistries.POTION.getKey(potion);
  return id != null && "minecraft".equals(id.getNamespace()) && !potion.getEffects().isEmpty();
 }
 public static List<MobEffectInstance> effects(ItemStack stack) { return PotionUtils.getMobEffects(stack); }
 public static Potion potion(ItemStack stack) { return PotionUtils.getPotion(stack); }
 public static void setPotion(ItemStack stack, Potion potion) { PotionUtils.setPotion(stack, potion); }
 public static void setCustomEffects(ItemStack stack, List<MobEffectInstance> effects) { PotionUtils.setCustomEffects(stack, effects); }
 public static void copyContents(ItemStack source, ItemStack target) {
  if (source.hasTag()) {
   for (String key : KEYS) if (source.getTag().contains(key)) target.getOrCreateTag().put(key, source.getTag().get(key).copy());
  }
 }
 public static void clearContents(ItemStack stack) {
  if (!stack.hasTag()) return;
  for (String key : KEYS) stack.getTag().remove(key);
  if (stack.getTag().isEmpty()) stack.setTag(null);
 }
}
