package com.neutrinodust.useful_ores.pedestal;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.level.BlockEvent;

public class PedestalBreakEvents {

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getState().getBlock() instanceof AncientPedestalBlock)) return;
        if (event.getPlayer() == null || event.getPlayer().isCreative()) return;

        ItemStack tool = event.getPlayer().getMainHandItem();
        if (isVoidshardPickaxe(tool)) return;

        event.setCanceled(true);
        event.getPlayer().displayClientMessage(
                Component.translatable("message.useful_ores.ancient_pedestal.wrong_tool"), true);
    }

    private static boolean isVoidshardPickaxe(ItemStack stack) {
        if (stack.isEmpty()) return false;

        return stack.is(ModItems.VOIDSHARD_ITEMS.get(4).get())
                || stack.is(ModItems.SUBSPACE_ITEMS.get(1).get());
    }
}

