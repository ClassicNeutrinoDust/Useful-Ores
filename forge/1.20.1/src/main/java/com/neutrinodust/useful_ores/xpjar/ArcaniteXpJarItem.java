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
        Integer value = ModXpJarComponents.getXp(stack);
        return value == null ? 0 : Math.max(0, Math.min(ArcaniteXpJarBlockEntity.MAX_XP, value));
    }

    @Override
    public net.minecraft.world.item.ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        updateFillModel(stack, 0);
        return stack;
    }

    public static void setStoredXp(ItemStack stack, int xp) {
        int clamped = Math.max(0, Math.min(ArcaniteXpJarBlockEntity.MAX_XP, xp));
        ModXpJarComponents.setXp(stack, clamped);
        updateFillModel(stack, clamped);
    }

    private static void updateFillModel(ItemStack stack, int xp) {
        int level = Math.round(7.0F * xp / ArcaniteXpJarBlockEntity.MAX_XP);
        level = Math.max(0, Math.min(7, level));
        stack.getOrCreateTag().putInt("CustomModelData", level);
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.level.Level level, java.util.List<Component> tooltip, TooltipFlag flag) {
        int xp = getStoredXp(stack);
        updateFillModel(stack, xp);
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
