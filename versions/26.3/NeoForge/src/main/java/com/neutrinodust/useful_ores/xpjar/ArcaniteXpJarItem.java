package com.neutrinodust.useful_ores.xpjar;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

public class ArcaniteXpJarItem extends BlockItem {

    public ArcaniteXpJarItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        BlockState clicked = level.getBlockState(pos);
        if (clicked.getBlock() instanceof ArcaniteXpJarBlock && context.getPlayer() != null) {
            return ArcaniteXpJarBlock.handleInteraction(clicked, level, pos, context.getPlayer());
        }
        return super.useOn(context);
    }

    public static int getStoredXp(ItemStack stack) {
        Integer value = stack.get(ModXpJarComponents.JAR_XP);
        return value == null ? 0 : Math.clamp(value, 0, ArcaniteXpJarBlockEntity.MAX_XP);
    }

    public static void setStoredXp(ItemStack stack, int xp) {
        int clamped = Math.clamp(xp, 0, ArcaniteXpJarBlockEntity.MAX_XP);
        stack.set(ModXpJarComponents.JAR_XP, clamped);
        updateFillModel(stack, clamped);
    }

    private static void updateFillModel(ItemStack stack, int xp) {
        int level = Math.round(7.0F * xp / ArcaniteXpJarBlockEntity.MAX_XP);
        level = Math.clamp(level, 0, 7);
        stack.set(DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(List.of((float) level), List.of(), List.of(), List.of()));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getStoredXp(stack) / ArcaniteXpJarBlockEntity.MAX_XP);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float pct = getStoredXp(stack) / (float) ArcaniteXpJarBlockEntity.MAX_XP;

        int green = Math.round(200 + 55 * pct);
        int red   = Math.round(30 * (1.0F - pct));
        return (red << 16) | (green << 8);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                 Consumer<Component> tooltip, TooltipFlag flag) {
        int xp = getStoredXp(stack);
        int pct = Math.round(100.0F * xp / ArcaniteXpJarBlockEntity.MAX_XP);
        ChatFormatting colour = xp >= ArcaniteXpJarBlockEntity.MAX_XP ? ChatFormatting.GREEN
                              : xp > 0 ? ChatFormatting.YELLOW
                              : ChatFormatting.GRAY;
        tooltip.accept(Component.translatable("item.useful_ores.arcanite_xp_jar.stored", xp, pct)
                .withStyle(colour));
        tooltip.accept(Component.translatable("item.useful_ores.arcanite_xp_jar.hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}

