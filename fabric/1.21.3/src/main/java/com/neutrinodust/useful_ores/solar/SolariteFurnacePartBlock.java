package com.neutrinodust.useful_ores.solar;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class SolariteFurnacePartBlock extends BaseEntityBlock {

    public static final com.mojang.serialization.MapCodec<SolariteFurnacePartBlock> CODEC = simpleCodec(SolariteFurnacePartBlock::new);

    public static final BooleanProperty HIGH_X = BooleanProperty.create("high_x");
    public static final BooleanProperty HIGH_Y = BooleanProperty.create("high_y");
    public static final BooleanProperty HIGH_Z = BooleanProperty.create("high_z");

    public SolariteFurnacePartBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(HIGH_X, false).setValue(HIGH_Y, false).setValue(HIGH_Z, false));
    }

    @Override
    public com.mojang.serialization.MapCodec<SolariteFurnacePartBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HIGH_X, HIGH_Y, HIGH_Z);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        
        
        return RenderShape.INVISIBLE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolariteFurnacePartBlockEntity(pos, state);
    }

    public static BlockPos controllerPos(BlockState state, BlockPos partPos) {
        return partPos.offset(
                state.getValue(HIGH_X) ? -1 : 0,
                state.getValue(HIGH_Y) ? -1 : 0,
                state.getValue(HIGH_Z) ? -1 : 0
        );
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockPos controllerPos = controllerPos(state, pos);
        BlockState controllerState = level.getBlockState(controllerPos);
        if (controllerState.getBlock() instanceof SolariteFurnaceBlock furnaceBlock) {
            return furnaceBlock.useWithoutItem(controllerState, level, controllerPos, player, hit);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel
                && !SolariteFurnaceMultiblock.isWorking()) {
            BlockPos controllerPos = controllerPos(state, pos);
            SolariteFurnaceMultiblock.disassemble(serverLevel, controllerPos, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}

