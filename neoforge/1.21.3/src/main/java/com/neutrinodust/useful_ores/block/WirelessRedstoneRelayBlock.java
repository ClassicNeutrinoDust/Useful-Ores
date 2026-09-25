package com.neutrinodust.useful_ores.block;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import com.neutrinodust.useful_ores.particle.ModParticleTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jspecify.annotations.Nullable;

public class WirelessRedstoneRelayBlock extends BaseEntityBlock {
   public static final MapCodec<WirelessRedstoneRelayBlock> CODEC = simpleCodec(WirelessRedstoneRelayBlock::new);

   public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

   private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

   public WirelessRedstoneRelayBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any()
         .setValue(FACING, Direction.NORTH)
         .setValue(POWERED, false)
         .setValue(CONNECTED, false));
   }

    




    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

   @Override
   public MapCodec<WirelessRedstoneRelayBlock> codec() { return CODEC; }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, POWERED, CONNECTED);
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
      return new WirelessRedstoneRelayBlockEntity(pos, state);
   }

   @Override
   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return level.isClientSide()
         ? null
         : createTickerHelper(type, ModBlockEntities.WIRELESS_REDSTONE_RELAY.get(), WirelessRedstoneRelayBlockEntity::safetyNetTick);
   }

   @Override
   protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
      super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
      if (level.isClientSide()) return;
      if (level.getBlockEntity(pos) instanceof WirelessRedstoneRelayBlockEntity relay) {
         relay.onNeighborChanged(level, pos, state);
      }
   }

   @Override
   protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (level.getBlockEntity(pos) instanceof WirelessRedstoneRelayBlockEntity relay) {
         relay.applyScheduledOutput(level, pos, state);
      }
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      return handleUse(level, pos, player);
   }

   @Override
   protected InteractionResult useItemOn(net.minecraft.world.item.ItemStack stack, BlockState state, Level level,
         BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hitResult) {
      return handleUse(level, pos, player);
   }

   private InteractionResult handleUse(Level level, BlockPos pos, Player player) {
      if (level.isClientSide()) return InteractionResult.SUCCESS;

      if (level.getBlockEntity(pos) instanceof WirelessRedstoneRelayBlockEntity relay
         && level instanceof ServerLevel serverLevel
         && player instanceof ServerPlayer serverPlayer) {
         if (player.isShiftKeyDown()) {

            if (WirelessRedstoneLinking.getPending(serverPlayer) != null) {
               WirelessRedstoneLinking.cancelPending(serverPlayer);
               return InteractionResult.SUCCESS;
            }

            if (relay.getUpstreamHubs().size() >= 2) {
               relay.cycleMeshCombine(serverLevel);
               String combineText = switch (relay.getMeshCombine()) {
                  case OR -> "Mesh combine set to OR - powered if ANY upstream is powered.";
                  case AND -> "Mesh combine set to AND - powered only if EVERY upstream is powered.";
                  case LATEST -> "Mesh combine set to LATEST - follows whichever upstream changed most recently.";
               };
               serverPlayer.displayClientMessage(net.minecraft.network.chat.Component.literal(combineText), true);
               return InteractionResult.SUCCESS;
            }
            relay.toggleMode(serverLevel);
            String text = switch (relay.getMode()) {
               case MULTI -> "Relay set to MULTI mode - link it to several receivers.";
               case CONNECTION -> "Relay set to CONNECTION mode - a bus station for long chains.";
               case SINGLE -> "Relay set to SINGLE mode.";
            };
            serverPlayer.displayClientMessage(net.minecraft.network.chat.Component.literal(text), true);
            return InteractionResult.SUCCESS;
         }
         WirelessRedstoneLinking.handleInteract(serverLevel, pos, serverPlayer, relay);
         return InteractionResult.SUCCESS;
      }
      return InteractionResult.PASS;
   }

   @Override
   protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
      if (!state.is(newState.getBlock()) && !level.isClientSide()
            && level.getBlockEntity(pos) instanceof WirelessRedstoneRelayBlockEntity relay
            && level instanceof ServerLevel serverLevel) {
         relay.cleanupNetworkGraph(serverLevel);
      }
      super.onRemove(state, level, pos, newState, movedByPiston);
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

   public static Direction inputSide(BlockState state) {
      return state.getValue(FACING).getOpposite();
   }

   @Override
   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if (!state.getValue(CONNECTED)) return;

      if (random.nextInt(4) != 0) return;

      double x = pos.getX() + 0.5;
      double y = pos.getY() + 0.14;
      double z = pos.getZ() + 0.5;
      level.addParticle(ModParticleTypes.WIFI_RING.get(), x, y, z, 0.0, 0.0, 0.0);
   }
}

