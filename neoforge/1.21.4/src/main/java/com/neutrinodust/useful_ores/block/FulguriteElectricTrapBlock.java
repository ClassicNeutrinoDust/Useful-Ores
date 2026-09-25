package com.neutrinodust.useful_ores.block;

import com.neutrinodust.useful_ores.util.NbtCompat;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import com.neutrinodust.useful_ores.init.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class FulguriteElectricTrapBlock extends BaseEntityBlock {
   public static final MapCodec<FulguriteElectricTrapBlock> CODEC = simpleCodec(FulguriteElectricTrapBlock::new);

   public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

   public static final BooleanProperty ACTIVE = BlockStateProperties.LIT;

   private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);

   private static final String TAG_UNLOCKED = "useful_ores_fulgurite_trap_unlocked";

   public FulguriteElectricTrapBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any()
         .setValue(FACING, Direction.NORTH)
         .setValue(ACTIVE, false));
   }

   @Override
   public MapCodec<FulguriteElectricTrapBlock> codec() { return CODEC; }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, ACTIVE);
   }

   @Override
   public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
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
   protected RenderShape getRenderShape(BlockState state) {

      return RenderShape.MODEL;
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new FulguriteElectricTrapBlockEntity(pos, state);
   }

   @Override
   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return level.isClientSide()
         ? null
         : createTickerHelper(type, ModBlockEntities.FULGURITE_ELECTRIC_TRAP.get(), FulguriteElectricTrapBlockEntity::serverTick);
   }

   @Override
   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      if (level.isClientSide()) return;
      CustomData data = stack.get(DataComponents.CUSTOM_DATA);
      boolean unlocked = data != null && NbtCompat.getBooleanOr(data.copyTag(), TAG_UNLOCKED, false);
      if (unlocked && level.getBlockEntity(pos) instanceof FulguriteElectricTrapBlockEntity trap) {
         trap.setLightningUnlocked(true);
      }
   }

   @Override
   public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
      if (!level.isClientSide() && !player.isCreative()
         && level.getBlockEntity(pos) instanceof FulguriteElectricTrapBlockEntity trap) {
         ItemStack stack = new ItemStack(ModItems.FULGURITE_ELECTRIC_TRAP.get());
         if (trap.isLightningUnlocked()) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(TAG_UNLOCKED, true);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         }
         popResource(level, pos, stack);
      }
      return super.playerWillDestroy(level, pos, state, player);
   }

   @Override
   protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
      if (level.isClientSide()) return InteractionResult.SUCCESS;
      if (!(level.getBlockEntity(pos) instanceof FulguriteElectricTrapBlockEntity trap)) return InteractionResult.PASS;

      if (stack.is(ModItems.FULGURITE_ITEMS.get(1).get())) {
         if (trap.getRange() >= FulguriteElectricTrapBlockEntity.MAX_RANGE) {
            sendMessage(player, Component.translatable("message.useful_ores.fulgurite_electric_trap.max_range"));
            return InteractionResult.SUCCESS;
         }
         trap.setRange(trap.getRange() + 1);
         if (!player.isCreative()) stack.shrink(1);
         level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 0.6F, 1.6F);
         sendMessage(player, Component.translatable("message.useful_ores.fulgurite_electric_trap.range", trap.getRange()));
         return InteractionResult.SUCCESS;
      }

      sendMessage(player, trap.isLightningUnlocked()
         ? Component.translatable("message.useful_ores.fulgurite_electric_trap.status_active", trap.getRange())
         : Component.translatable("message.useful_ores.fulgurite_electric_trap.status_dormant"));
      return InteractionResult.SUCCESS;
   }

   private static void sendMessage(Player player, Component message) {
      if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
         serverPlayer.displayClientMessage(message, true);
      }
   }

}

