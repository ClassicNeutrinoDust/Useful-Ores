package com.neutrinodust.useful_ores.lock;

import com.neutrinodust.useful_ores.item.TitaniumKeyItem;
import com.neutrinodust.useful_ores.item.TitaniumLockItem;
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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.UUID;

public class ChestLockEvents {

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Level level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ChestBlock)) return;

        Player rawPlayer = event.getEntity();
        if (!(rawPlayer instanceof ServerPlayer player)) return;

        ChestLockData data = ChestLockData.get(serverLevel);
        ChestLockData.LockEntry rec = data.getForContainer(serverLevel, pos);
        ItemStack stack = event.getItemStack();

        if (rec == null) {
            if (stack.getItem() instanceof TitaniumLockItem) {
                data.lockContainer(serverLevel, pos, player.getUUID(), UUID.randomUUID());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                for (BlockPos containerPos : ChestLockData.getContainerPositions(serverLevel, pos)) {
                    ChestLockSyncEvents.sendLockedToAll(serverLevel, containerPos);
                }
                level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F, 0.9F);
                player.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.locked"), true);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }

            return;
        }

        if (stack.getItem() instanceof TitaniumLockItem) {

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            player.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.already_locked"), true);
            return;
        }

        if (stack.getItem() instanceof TitaniumKeyItem) {
            if (!rec.bound()) {
                UUID alreadyBoundTo = TitaniumKeyItem.getBoundLockId(stack);
                if (alreadyBoundTo != null) {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.FAIL);
                    player.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.key_already_bound"), true);
                    return;
                }
                if (stack.getCount() != 1) {

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.FAIL);
                    player.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.hold_one_key"), true);
                    return;
                }

                TitaniumKeyItem.bind(stack, rec.lockId());
                data.markContainerBound(serverLevel, pos);
                level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F, 1.4F);
                player.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.key_bound"), true);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }

            UUID stackBoundId = TitaniumKeyItem.getBoundLockId(stack);
            if (stackBoundId != null && stackBoundId.equals(rec.lockId())) {

                data.unlockContainer(serverLevel, pos);
                stack.shrink(1);
                for (BlockPos containerPos : ChestLockData.getContainerPositions(serverLevel, pos)) {
                    ChestLockSyncEvents.sendUnlockedToAll(serverLevel, containerPos);
                }
                level.playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                player.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.unlocked"), true);
                return;
            }

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            player.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.wrong_key"), true);
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);
        player.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.locked_deny"), true);
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        BlockPos pos = event.getPos();
        ChestLockData data = ChestLockData.get(serverLevel);
        ChestLockData.LockEntry rec = data.getForContainer(serverLevel, pos);
        if (rec == null) return;

        Player player = event.getPlayer();
        if (player != null && player.getUUID().equals(rec.owner())) {

            data.unlockContainer(serverLevel, pos);
            for (BlockPos containerPos : ChestLockData.getContainerPositions(serverLevel, pos)) {
                    ChestLockSyncEvents.sendUnlockedToAll(serverLevel, containerPos);
                }
            return;
        }

        event.setCanceled(true);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(Component.translatable("message.useful_ores.titanium_lock.locked_deny"), true);
        }
    }

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        ChestLockData data = ChestLockData.get(serverLevel);
        event.getAffectedBlocks().removeIf(pos -> data.isLocked(serverLevel, pos));
    }
}

