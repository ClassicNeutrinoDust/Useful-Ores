package com.neutrinodust.useful_ores.lock;

import com.neutrinodust.useful_ores.item.TitaniumKeyItem;
import com.neutrinodust.useful_ores.item.TitaniumLockItem;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.UUID;

public class ChestLockEvents {

    public InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {

        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ChestBlock)) return InteractionResult.PASS;

        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        ChestLockData data = ChestLockData.get(serverLevel);
        ChestLockData.LockEntry rec = data.getForContainer(serverLevel, pos);
        ItemStack stack = player.getItemInHand(hand);

        if (rec == null) {
            if (stack.getItem() instanceof TitaniumLockItem) {
                data.lockContainer(serverLevel, pos, serverPlayer.getUUID(), UUID.randomUUID());
                if (!serverPlayer.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                for (BlockPos containerPos : ChestLockData.getContainerPositions(serverLevel, pos)) {
                    ChestLockSyncEvents.sendLockedToAll(serverLevel, containerPos);
                }
                level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F, 0.9F);
                serverPlayer.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.locked"), true);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }

        if (stack.getItem() instanceof TitaniumLockItem) {

            serverPlayer.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.already_locked"), true);
            return InteractionResult.FAIL;
        }

        if (stack.getItem() instanceof TitaniumKeyItem) {
            if (!rec.bound()) {
                UUID alreadyBoundTo = TitaniumKeyItem.getBoundLockId(stack);
                if (alreadyBoundTo != null) {
                    serverPlayer.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.key_already_bound"), true);
                    return InteractionResult.FAIL;
                }
                if (stack.getCount() != 1) {

                    serverPlayer.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.hold_one_key"), true);
                    return InteractionResult.FAIL;
                }

                TitaniumKeyItem.bind(stack, rec.lockId());
                data.markContainerBound(serverLevel, pos);
                level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F, 1.4F);
                serverPlayer.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.key_bound"), true);
                return InteractionResult.SUCCESS;
            }

            UUID stackBoundId = TitaniumKeyItem.getBoundLockId(stack);
            if (stackBoundId != null && stackBoundId.equals(rec.lockId())) {

                data.unlockContainer(serverLevel, pos);
                stack.shrink(1);
                for (BlockPos containerPos : ChestLockData.getContainerPositions(serverLevel, pos)) {
                    ChestLockSyncEvents.sendUnlockedToAll(serverLevel, containerPos);
                }
                level.playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                serverPlayer.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.unlocked"), true);
                return InteractionResult.PASS;
            }

            serverPlayer.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.wrong_key"), true);
            return InteractionResult.FAIL;
        }

        serverPlayer.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.locked_deny"), true);
        return InteractionResult.FAIL;
    }

    public boolean onBeforeBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) return true;

        ChestLockData data = ChestLockData.get(serverLevel);
        ChestLockData.LockEntry rec = data.getForContainer(serverLevel, pos);
        if (rec == null) return true;

        if (player != null && player.getUUID().equals(rec.owner())) {

            data.unlockContainer(serverLevel, pos);
            for (BlockPos containerPos : ChestLockData.getContainerPositions(serverLevel, pos)) {
                    ChestLockSyncEvents.sendUnlockedToAll(serverLevel, containerPos);
                }
            return true;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.locked_deny"), true);
        }
        return false;
    }
}

