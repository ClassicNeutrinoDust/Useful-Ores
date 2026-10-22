package com.neutrinodust.useful_ores.util;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class ItemState {
    private static final String ROOT = "UsefulOres";
    private ItemState() {}
    private static CompoundTag root(ItemStack stack, boolean create) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(ROOT, 10)) return tag.getCompound(ROOT);
        return new CompoundTag();
    }
    private static void write(ItemStack stack, CompoundTag data) {
        CompoundTag parent = stack.getOrCreateTag();
        if (data.isEmpty()) parent.remove(ROOT); else parent.put(ROOT, data);
        if (parent.isEmpty()) stack.setTag(null);
    }
    public static boolean getBoolean(ItemStack stack, String key) { return root(stack, false).getBoolean(key); }
    public static boolean hasBoolean(ItemStack stack, String key) { return root(stack, false).contains(key, 1); }
    public static void setBoolean(ItemStack stack, String key, boolean value) { CompoundTag d=root(stack,true); d.putBoolean(key,value); write(stack,d); }
    public static void remove(ItemStack stack, String key) { CompoundTag d=root(stack,false); d.remove(key); write(stack,d); }
    public static String getString(ItemStack stack, String key) { CompoundTag d=root(stack,false); return d.contains(key,8) ? d.getString(key) : null; }
    public static void setString(ItemStack stack, String key, String value) { CompoundTag d=root(stack,true); if(value==null) d.remove(key); else d.putString(key,value); write(stack,d); }
    public static int getInt(ItemStack stack, String key, int fallback) { CompoundTag d=root(stack,false); return d.contains(key,3) ? d.getInt(key) : fallback; }
    public static void setInt(ItemStack stack, String key, int value) { CompoundTag d=root(stack,true); d.putInt(key,value); write(stack,d); }
    public static GlobalPos getGlobalPos(ItemStack stack, String key) {
        CompoundTag d=root(stack,false); if(!d.contains(key,10)) return null; CompoundTag p=d.getCompound(key);
        ResourceLocation id=ResourceLocation.tryParse(p.getString("Dimension")); if(id==null || !p.contains("Pos",4)) return null;
        return GlobalPos.of(ResourceKey.create(Registries.DIMENSION,id), net.minecraft.core.BlockPos.of(p.getLong("Pos")));
    }
    public static void setGlobalPos(ItemStack stack, String key, GlobalPos value) {
        CompoundTag d=root(stack,true); if(value==null) d.remove(key); else { CompoundTag p=new CompoundTag(); p.putString("Dimension",value.dimension().location().toString()); p.putLong("Pos",value.pos().asLong()); d.put(key,p); } write(stack,d);
    }
}
