package com.neutrinodust.useful_ores.xpjar;

import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jspecify.annotations.Nullable;

public class ArcaniteXpJarBlock extends BaseEntityBlock {

    public static final com.mojang.serialization.MapCodec<ArcaniteXpJarBlock> CODEC =
            simpleCodec(ArcaniteXpJarBlock::new);

    public static final IntegerProperty FILL_STAGE = IntegerProperty.create("fill_stage", 0, 7);

    private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);

    public ArcaniteXpJarBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FILL_STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FILL_STAGE);
    }

    @Override
    public com.mojang.serialization.MapCodec<ArcaniteXpJarBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);

        if (belowState.getBlock() instanceof TrapDoorBlock) {
            boolean open = belowState.getValue(BlockStateProperties.OPEN);
            return !open;
        }

        return Block.isFaceFull(belowState.getCollisionShape(level, below), Direction.UP);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                    net.minecraft.world.level.redstone.Orientation orientation,
                                    boolean movedByPiston) {
        if (!level.isClientSide() && !canSurvive(state, level, pos)) {

            dropAsItemAndRemove(level, pos, state);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FILL_STAGE, 0);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArcaniteXpJarBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                              BlockEntityType<T> type) {

        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                             @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ArcaniteXpJarBlockEntity be) {
            int storedXp = ArcaniteXpJarItem.getStoredXp(itemStack);
            be.setStoredXp(storedXp);
            int stage = stageForXp(storedXp);
            if (stage != 0) {
                level.setBlock(pos, state.setValue(FILL_STAGE, stage), 3);
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ArcaniteXpJarBlockEntity be) {
            ItemStack drop = new ItemStack(ModItems.ARCANITE_XP_JAR.get());
            ArcaniteXpJarItem.setStoredXp(drop, be.getStoredXp());
            Block.popResource(level, pos, drop);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hitResult) {
        return handleInteraction(state, level, pos, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        return handleInteraction(state, level, pos, player);
    }

    public static InteractionResult handleInteraction(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof ArcaniteXpJarBlockEntity be)) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {

            int room = ArcaniteXpJarBlockEntity.MAX_XP - be.getStoredXp();
            if (room <= 0) {
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) sp.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("item.useful_ores.arcanite_xp_jar.full"),
                    true);
                return InteractionResult.CONSUME;
            }
            int playerXp = player.totalExperience;
            if (playerXp <= 0) {
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) sp.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("item.useful_ores.arcanite_xp_jar.no_xp"),
                    true);
                return InteractionResult.CONSUME;
            }
            int toStore = Math.min(room, playerXp);
            player.giveExperiencePoints(-toStore);
            be.setStoredXp(be.getStoredXp() + toStore);
            updateFillStage(level, pos, state, be.getStoredXp());
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.2f);
        } else {

            int stored = be.getStoredXp();
            if (stored <= 0) {
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) sp.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("item.useful_ores.arcanite_xp_jar.empty"),
                    true);
                return InteractionResult.CONSUME;
            }

            boolean mendingRepaired = tryRepairMending(level, pos, player, be);
            if (!mendingRepaired) {

                player.giveExperiencePoints(stored);
                be.setStoredXp(0);
                updateFillStage(level, pos, state, 0);
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
            }
        }
        return InteractionResult.CONSUME;
    }

    private static boolean tryRepairMending(Level level, BlockPos pos, Player player, ArcaniteXpJarBlockEntity be) {

        ItemStack mendingTarget = findMendingTarget(player);
        if (mendingTarget == null || mendingTarget.isEmpty()) return false;

        int stored = be.getStoredXp();

        int maxRepairableXp = (mendingTarget.getDamageValue() + 1) / 2;
        int xpToUse = Math.min(stored, maxRepairableXp);
        int durabilityToRepair = xpToUse * 2;

        mendingTarget.setDamageValue(Math.max(0, mendingTarget.getDamageValue() - durabilityToRepair));
        be.setStoredXp(stored - xpToUse);
        BlockState current = level.getBlockState(pos);
        if (current.getBlock() instanceof ArcaniteXpJarBlock) {
            updateFillStage(level, pos, current, be.getStoredXp());
        }
        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 0.8f);
        return true;
    }

    private static boolean hasMendingEnchant(ItemStack stack) {
        var enc = stack.get(net.minecraft.core.component.DataComponents.ENCHANTMENTS);
        if (enc == null || enc.isEmpty()) return false;
        return enc.keySet().stream().anyMatch(h ->
            h.is(net.minecraft.world.item.enchantment.Enchantments.MENDING));
    }

    private static ItemStack findMendingTarget(Player player) {

        ItemStack main = player.getMainHandItem();
        if (!main.isEmpty() && main.isDamaged() && hasMendingEnchant(main)) {
            return main;
        }
        ItemStack off = player.getOffhandItem();
        if (!off.isEmpty() && off.isDamaged() && hasMendingEnchant(off)) {
            return off;
        }
        return null;
    }

    public static int stageForXp(int xp) {
        int stage = Math.round(7.0F * xp / ArcaniteXpJarBlockEntity.MAX_XP);
        return Math.clamp(stage, 0, 7);
    }

    private static void updateFillStage(Level level, BlockPos pos, BlockState state, int newXp) {
        int newStage = stageForXp(newXp);
        int oldStage = state.getValue(FILL_STAGE);
        if (newStage != oldStage) {
            level.setBlock(pos, state.setValue(FILL_STAGE, newStage), 3);
        }
    }

    private static void dropAsItemAndRemove(Level level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        int storedXp = 0;
        if (be instanceof ArcaniteXpJarBlockEntity jarBE) {
            storedXp = jarBE.getStoredXp();
        }
        ItemStack drop = new ItemStack(ModItems.ARCANITE_XP_JAR.get());
        ArcaniteXpJarItem.setStoredXp(drop, storedXp);
        level.removeBlock(pos, false);
        Block.popResource(level, pos, drop);
        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.6f, 1.0f);
    }
}

