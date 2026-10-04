package com.neutrinodust.useful_ores.event;

import com.neutrinodust.useful_ores.init.ModOreFireBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import com.neutrinodust.useful_ores.init.ModColoredCampfireBlocks;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredBlock;

public class OreFireIgnitionEvents {

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(Items.FLINT_AND_STEEL)) {
            return;
        }

        Level level = event.getLevel();
        BlockPos clickedPos = event.getPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        if (clickedState.getBlock() instanceof CampfireBlock
                && ModColoredCampfireBlocks.CAMPFIRE_BLOCKS.values().stream()
                    .anyMatch(db -> clickedState.is(db.get()))
                && !clickedState.getValue(CampfireBlock.LIT)
                && !clickedState.getValue(CampfireBlock.WATERLOGGED)) {

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);

            if (!level.isClientSide()) {
                level.setBlock(clickedPos, clickedState.setValue(CampfireBlock.LIT, true), 11);
                level.playSound(null, clickedPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS,
                        1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                Player player = event.getEntity();
                if (player != null) {
                    EquipmentSlot slot = event.getHand() == InteractionHand.OFF_HAND
                            ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                    stack.hurtAndBreak(1, player, slot);
                }
            }
            return;
        }

        Direction face = event.getFace() != null ? event.getFace() : Direction.UP;
        BlockPos firePos = clickedPos.relative(face);

        DeferredBlock<Block> fireBlock = ModOreFireBlocks.IGNITABLE_TO_FIRE.get(
                level.getBlockState(firePos.below()).getBlock());
        if (fireBlock == null) {
            fireBlock = ModOreFireBlocks.IGNITABLE_TO_FIRE.get(clickedState.getBlock());
        }
        if (fireBlock == null) return;

        if (!level.getBlockState(firePos).isAir() || !BaseFireBlock.canBePlacedAt(level, firePos, face)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        if (level.isClientSide()) {
            return;
        }

        level.playSound(null, firePos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS,
                1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        level.setBlock(firePos, fireBlock.get().defaultBlockState(), 11);

        Player player = event.getEntity();
        if (player != null) {
            EquipmentSlot slot = event.getHand() == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            stack.hurtAndBreak(1, player, slot);
        }
    }
}

