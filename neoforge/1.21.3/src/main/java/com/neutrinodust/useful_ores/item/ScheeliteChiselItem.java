package com.neutrinodust.useful_ores.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

public class ScheeliteChiselItem extends Item {

    public ScheeliteChiselItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        Player player = context.getPlayer();

        BlockState newState = null;

        if (block instanceof StairBlock) {

            Block slabBlock = siblingBySuffix(block, "_stairs", "_slab");
            if (slabBlock != null) {
                newState = slabBlock.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM);
                newState = carryWaterlogged(newState, state);
            }
        } else if (!(block instanceof SlabBlock)) {

            Block stairsBlock = firstMatch(block,
                new String[] {"", "_planks", "_block", "s"},
                "_stairs"
            );
            if (stairsBlock != null) {
                Direction facing = player != null ? player.getDirection() : Direction.NORTH;
                newState = stairsBlock.defaultBlockState()
                    .setValue(StairBlock.FACING, facing)
                    .setValue(StairBlock.HALF, Half.BOTTOM);
                newState = carryWaterlogged(newState, state);
            }
        }

        if (newState == null) {
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.useful_ores.scheelite_chisel.no_match"),
                    true);
            }
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            level.setBlockAndUpdate(pos, newState);
            level.playSound(null, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F,
                level.getRandom().nextFloat() * 0.2F + 0.9F);

            if (player != null && !player.getAbilities().instabuild) {
                ItemStack stack = context.getItemInHand();
                InteractionHand hand = context.getHand();
                EquipmentSlot slot = hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                stack.hurtAndBreak(1, player, slot);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private static Block siblingBySuffix(Block block, String oldSuffix, String newSuffix) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) return null;
        String path = id.getPath();
        if (!oldSuffix.isEmpty()) {
            if (!path.endsWith(oldSuffix)) return null;
            path = path.substring(0, path.length() - oldSuffix.length());
        }
        ResourceLocation newId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path + newSuffix);
        return BuiltInRegistries.BLOCK.get(newId).map(net.minecraft.core.Holder.Reference::value).orElse(null);
    }

    private static Block firstMatch(Block block, String[] oldSuffixCandidates, String newSuffix) {
        for (String oldSuffix : oldSuffixCandidates) {
            if (oldSuffix.equals("s")) {
                ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
                if (id == null || !id.getPath().endsWith("s")) continue;
            }
            Block match = siblingBySuffix(block, oldSuffix, newSuffix);
            if (match != null) return match;
        }
        return null;
    }

    private static BlockState carryWaterlogged(BlockState newState, BlockState oldState) {
        if (newState.hasProperty(BlockStateProperties.WATERLOGGED)
                && oldState.hasProperty(BlockStateProperties.WATERLOGGED)) {
            return newState.setValue(BlockStateProperties.WATERLOGGED, oldState.getValue(BlockStateProperties.WATERLOGGED));
        }
        return newState;
    }

}
