package com.neutrinodust.useful_ores.bioluminescence;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class BioluminescentAxeEvents {

    public InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof AxeItem)) return InteractionResult.PASS;

        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;

        BlockPos pos = hitResult.getBlockPos();
        BioluminescentBlockData data = BioluminescentBlockData.get(serverLevel);
        if (!data.isGlowing(pos)) return InteractionResult.PASS;

        data.unmarkGlowing(pos);
        serverLevel.getLightEngine().checkBlock(pos);
        BioluminescentSyncEvents.sendRemovalToAll(serverLevel, pos);

        level.playSound(null, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);

        return InteractionResult.SUCCESS;
    }
}

