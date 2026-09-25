package com.neutrinodust.useful_ores.solar;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

public class SolarBatteryItem extends BlockItem {

    public static final int MAX_ENERGY = 12000;

    public SolarBatteryItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static int getEnergy(ItemStack stack) {
        Integer value = stack.get(ModSolarComponents.BATTERY_ENERGY);
        return value == null ? 0 : Math.max(0, Math.min(MAX_ENERGY, value));
    }

    public static void setEnergy(ItemStack stack, int energy) {
        stack.set(ModSolarComponents.BATTERY_ENERGY, Math.max(0, Math.min(MAX_ENERGY, energy)));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getEnergy(stack) / MAX_ENERGY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float pct = getEnergy(stack) / (float) MAX_ENERGY;

        int red = Math.round((1.0F - pct) * 255.0F);
        int green = Math.round(pct * 255.0F);
        return (red << 16) | (green << 8);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        int pct = Math.round(100.1F * getEnergy(stack) / MAX_ENERGY);
        tooltip.accept(Component.translatable("item.useful_ores.solar_battery.charge", pct)
                .withStyle(pct > 0 ? ChatFormatting.YELLOW : ChatFormatting.GRAY));
    }
}

