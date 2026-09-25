package com.neutrinodust.useful_ores.bioluminescence;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class BioluminescentAxeEvents {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickAxeScrapeGlow(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof AxeItem)) return;

        Level level = event.getLevel();
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;

        BlockPos pos = event.getPos();
        BioluminescentBlockData data = BioluminescentBlockData.get(serverLevel);
        if (!data.isGlowing(pos)) return;

        data.unmarkGlowing(pos);
        serverLevel.getLightEngine().checkBlock(pos);
        BioluminescentSyncEvents.sendRemovalToAll(serverLevel, pos);

        level.playSound(null, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}

