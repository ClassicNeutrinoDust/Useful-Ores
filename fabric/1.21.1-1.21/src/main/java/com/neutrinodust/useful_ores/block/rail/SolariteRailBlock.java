package com.neutrinodust.useful_ores.block.rail;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RailShape;


public class SolariteRailBlock extends BaseRailBlock {
    public static final MapCodec<SolariteRailBlock> CODEC = simpleCodec(SolariteRailBlock::new);
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    
    private static final int ACTIVATION_SKY_DARKEN_MAX = 4;
    private static final int LIGHT_CHECK_PERIOD = 20;

    public SolariteRailBlock(Properties properties) {
        
        super(false, properties);
        registerDefaultState(stateDefinition.any()
                .setValue(SHAPE, RailShape.NORTH_SOUTH)
                .setValue(WATERLOGGED, false)
                .setValue(LIT, false));
    }

    @Override
    public MapCodec<SolariteRailBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(SHAPE, WATERLOGGED, LIT);
    }

    @Override
    public EnumProperty<RailShape> getShapeProperty() {
        return SHAPE;
    }


    public static boolean isActive(BlockState state) {
        return state.getBlock() instanceof SolariteRailBlock
                && state.hasProperty(LIT)
                && state.getValue(LIT);
    }

    public static boolean shouldBeActive(Level level, BlockPos pos) {
        if (level.dimension() != Level.OVERWORLD) return false;
        
        
        
        long time = Math.floorMod(level.getDayTime(), 24000L);
        return time < 12000L && level.getSkyDarken() < ACTIVATION_SKY_DARKEN_MAX;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            refreshActive(level, pos, state);
            level.scheduleTick(pos, this, LIGHT_CHECK_PERIOD);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        refreshActive(level, pos, state);
        level.scheduleTick(pos, this, LIGHT_CHECK_PERIOD);
    }

    private static void refreshActive(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof SolariteRailBlock rail)) return;
        boolean active = shouldBeActive(level, pos);
        if (state.getValue(LIT) != active) {
            level.setBlock(pos, state.setValue(LIT, active), 3);
        }
    }
}
