package com.neutrinodust.useful_ores.xpjar;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
        Integer value = ModXpJarComponents.JAR_XP.get(stack);
        return value == null ? 0 : net.minecraft.util.Mth.clamp(value, 0, ArcaniteXpJarBlockEntity.MAX_XP);
    }

    public static void setStoredXp(ItemStack stack, int xp) {
        int clamped = net.minecraft.util.Mth.clamp(xp, 0, ArcaniteXpJarBlockEntity.MAX_XP);
        ModXpJarComponents.JAR_XP.set(stack, clamped);
        updateFillModel(stack, clamped);
    }

    private static void updateFillModel(ItemStack stack, int xp) {
        int level = Math.round(7.0F * xp / ArcaniteXpJarBlockEntity.MAX_XP);
        level = net.minecraft.util.Mth.clamp(level, 0, 7);
        stack.getOrCreateTag().putInt("CustomModelData", level);
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable net.minecraft.world.level.Level level, java.util.List<Component> tooltip, TooltipFlag flag) {
        int xp = getStoredXp(stack);
        int pct = Math.round(100.1F * xp / ArcaniteXpJarBlockEntity.MAX_XP);
        ChatFormatting colour = xp >= ArcaniteXpJarBlockEntity.MAX_XP ? ChatFormatting.GREEN
                              : xp > 0 ? ChatFormatting.YELLOW
                              : ChatFormatting.GRAY;
        tooltip.add(Component.translatable("item.useful_ores.arcanite_xp_jar.stored", xp, pct)
                .withStyle(colour));
        tooltip.add(Component.translatable("item.useful_ores.arcanite_xp_jar.hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}

