package com.neutrinodust.useful_ores.block;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class RedstoneClockBlock extends BaseEntityBlock {

   public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public static final IntegerProperty PERIOD_INDEX = IntegerProperty.create("period_index", 0, 7);

   private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

   public RedstoneClockBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any()
         .setValue(FACING, Direction.NORTH)
         .setValue(POWERED, false)
         .setValue(PERIOD_INDEX, 3));
   }


   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, POWERED, PERIOD_INDEX);
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
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new RedstoneClockBlockEntity(pos, state);
   }

   @Override
   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return level.isClientSide()
         ? null
         : createTickerHelper(type, ModBlockEntities.REDSTONE_CLOCK, RedstoneClockBlockEntity::tick);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (level.isClientSide()) return InteractionResult.SUCCESS;

      if (level.getBlockEntity(pos) instanceof RedstoneClockBlockEntity clock && player instanceof ServerPlayer serverPlayer) {
         clock.cyclePeriod(level, pos, state);
         int periodTicks = clock.getPeriodTicks();
         double seconds = periodTicks / 20.0;
         serverPlayer.sendSystemMessage(
            Component.translatable("message.useful_ores.redstone_clock.period", periodTicks, seconds),
            true
         );
         return InteractionResult.SUCCESS;
      }
      return InteractionResult.PASS;
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
}

