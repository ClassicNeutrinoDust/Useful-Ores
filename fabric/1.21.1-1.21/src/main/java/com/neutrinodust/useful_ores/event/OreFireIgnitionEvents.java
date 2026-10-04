package com.neutrinodust.useful_ores.event;

import com.neutrinodust.useful_ores.init.ModColoredCampfireBlocks;
import com.neutrinodust.useful_ores.init.ModOreFireBlocks;
import com.neutrinodust.useful_ores.init.ModRegisters;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
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
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class OreFireIgnitionEvents {

    public InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Items.FLINT_AND_STEEL)) {
            return InteractionResult.PASS;
        }

        BlockPos clickedPos = hitResult.getBlockPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        if (clickedState.getBlock() instanceof CampfireBlock
                && ModColoredCampfireBlocks.CAMPFIRE_BLOCKS.values().stream()
                    .anyMatch(db -> clickedState.is(db.get()))
                && !clickedState.getValue(CampfireBlock.LIT)
                && !clickedState.getValue(CampfireBlock.WATERLOGGED)) {

            if (!level.isClientSide()) {
                level.setBlock(clickedPos, clickedState.setValue(CampfireBlock.LIT, true), 11);
                level.playSound(null, clickedPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS,
                        1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                EquipmentSlot slot = hand == InteractionHand.OFF_HAND
                        ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                stack.hurtAndBreak(1, player, slot);
            }
            return InteractionResult.SUCCESS;
        }

        ModRegisters.RegisteredBlock<Block> fireBlock = ModOreFireBlocks.IGNITABLE_TO_FIRE.get(clickedState.getBlock());
        if (fireBlock == null) {
            return InteractionResult.PASS;
        }

        Direction face = hitResult.getDirection() != null ? hitResult.getDirection() : Direction.UP;
        BlockPos firePos = clickedPos.relative(face);

        if (!level.getBlockState(firePos).isAir() || !BaseFireBlock.canBePlacedAt(level, firePos, face)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        level.playSound(null, firePos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS,
                1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        level.setBlock(firePos, fireBlock.get().defaultBlockState(), 11);

        EquipmentSlot slot = hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
        stack.hurtAndBreak(1, player, slot);

        return InteractionResult.SUCCESS;
    }
}

