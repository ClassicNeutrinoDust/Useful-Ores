package com.neutrinodust.useful_ores.block.rail;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RailShape;

public class SolariteRailBlock extends BaseRailBlock {


    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;

    public static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;

    public static final net.minecraft.world.level.block.state.properties.BooleanProperty LIT =
            net.minecraft.world.level.block.state.properties.BooleanProperty.create("lit");

    public SolariteRailBlock(Properties properties) {
        super(true, properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(SHAPE, RailShape.NORTH_SOUTH)
                .setValue(WATERLOGGED, false)
                .setValue(LIT, false));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(SHAPE, WATERLOGGED, LIT);
    }

    @Override
    public EnumProperty<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && oldState.getBlock() != this) {
            boolean shouldBeLit = level.getSkyDarken() < 4;
            if (state.getValue(LIT) != shouldBeLit) {
                level.setBlock(pos, state.setValue(LIT, shouldBeLit), 3);
            }
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

        boolean shouldBeLit = level.getSkyDarken() < 4;
        if (state.getValue(LIT) != shouldBeLit) {
            level.setBlock(pos, state.setValue(LIT, shouldBeLit), 3);
        }
        level.scheduleTick(pos, this, 20);
    }

}

