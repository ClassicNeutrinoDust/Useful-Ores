package com.neutrinodust.useful_ores.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RedstoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public abstract class LogicGateBlock extends Block {
   public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
   private static final int DELAY_TICKS = 1;

   protected LogicGateBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, POWERED);
   }

   @Override
   public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Override
   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   @Override
   protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockPos below = pos.below();
      return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
   }

   @Override
   protected boolean isSignalSource(BlockState state) { return true; }

   @Override
   protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return state.getValue(POWERED) && direction == state.getValue(FACING) ? 15 : 0;
   }

   @Override
   protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return this.getSignal(state, level, pos, direction);
   }

   @Override
   protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
      super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
      if (level.isClientSide()) return;

      boolean desired = computeOutput(level, pos, state);
      if (desired != state.getValue(POWERED) && !level.getBlockTicks().willTickThisTick(pos, this)) {
         level.scheduleTick(pos, this, DELAY_TICKS);
      }
   }

   @Override
   protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      boolean desired = computeOutput(level, pos, state);
      if (desired != state.getValue(POWERED)) {
         level.setBlockAndUpdate(pos, state.setValue(POWERED, desired));
      }
   }

   protected abstract boolean computeOutput(Level level, BlockPos pos, BlockState state);

   public static Direction inputA(BlockState state) {
      return state.getValue(FACING).getCounterClockWise();
   }

   public static Direction inputB(BlockState state) {
      return state.getValue(FACING).getClockWise();
   }

   protected static boolean readInput(Level level, BlockPos pos, Direction face) {
      BlockPos inputPos = pos.relative(face);
      int signal = level.getSignal(inputPos, face);
      if (signal < 15) {
         BlockState inputState = level.getBlockState(inputPos);
         if (inputState.is(Blocks.REDSTONE_WIRE)) {
            signal = Math.max(signal, inputState.getValue(RedstoneWireBlock.POWER));
         }
      }
      return signal > 0;
   }
}

