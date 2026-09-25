package com.neutrinodust.useful_ores.xpjar;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
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
        Integer value = stack.get(ModXpJarComponents.JAR_XP.get());
        return value == null ? 0 : Math.clamp(value, 0, ArcaniteXpJarBlockEntity.MAX_XP);
    }

    public static void setStoredXp(ItemStack stack, int xp) {
        int clamped = Math.clamp(xp, 0, ArcaniteXpJarBlockEntity.MAX_XP);
        stack.set(ModXpJarComponents.JAR_XP.get(), clamped);
        updateFillModel(stack, clamped);
    }

    private static void updateFillModel(ItemStack stack, int xp) {
        int level = Math.round(7.0F * xp / ArcaniteXpJarBlockEntity.MAX_XP);
        level = Math.clamp(level, 0, 7);
        stack.set(DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(List.of((float) level), List.of(), List.of(), List.of()));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> tooltip, TooltipFlag flag) {
        int xp = getStoredXp(stack);
        int pct = Math.round(100.0F * xp / ArcaniteXpJarBlockEntity.MAX_XP);
        ChatFormatting colour = xp >= ArcaniteXpJarBlockEntity.MAX_XP ? ChatFormatting.GREEN
                              : xp > 0 ? ChatFormatting.YELLOW
                              : ChatFormatting.GRAY;
        tooltip.add(Component.translatable("item.useful_ores.arcanite_xp_jar.stored", xp, pct)
                .withStyle(colour));
        tooltip.add(Component.translatable("item.useful_ores.arcanite_xp_jar.hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}

