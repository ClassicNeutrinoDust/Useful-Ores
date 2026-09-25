package com.neutrinodust.useful_ores.block.rail;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

public class EnderiumRailBlock extends BaseRailBlock implements EntityBlock, SimpleWaterloggedBlock {

    public static final MapCodec<EnderiumRailBlock> CODEC = simpleCodec(EnderiumRailBlock::new);
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;

    public EnderiumRailBlock(Properties properties) {
        super(true, properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(SHAPE, RailShape.NORTH_SOUTH)
            .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<EnderiumRailBlock> codec() {
        return CODEC;
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, WATERLOGGED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean waterlogged = context.getLevel().getFluidState(context.getClickedPos())
            .is(net.minecraft.world.level.material.Fluids.WATER);
        Direction facing = context.getHorizontalDirection();
        boolean eastWest = facing == Direction.EAST || facing == Direction.WEST;
        return this.defaultBlockState()
            .setValue(SHAPE, eastWest ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH)
            .setValue(WATERLOGGED, waterlogged);
    }

    @Override
    protected BlockState updateState(BlockState state, Level level, BlockPos pos, boolean movedByPiston) {
        return state;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnderiumRailBlockEntity(pos, state);
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(
        BlockState state, Level level, BlockPos pos,
        net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hitResult
    ) {
        return handleUse(state, level, pos, player);
    }

    @Override
    protected net.minecraft.world.InteractionResult useItemOn(
        net.minecraft.world.item.ItemStack stack, BlockState state, Level level, BlockPos pos,
        net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand,
        net.minecraft.world.phys.BlockHitResult hitResult
    ) {
        return handleUse(state, level, pos, player);
    }

    private net.minecraft.world.InteractionResult handleUse(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player) {
        if (level.isClientSide()) return net.minecraft.world.InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            if (EnderiumRailLinking.getPending(serverPlayer) != null) {
                EnderiumRailLinking.cancelPending(serverPlayer);
                return net.minecraft.world.InteractionResult.CONSUME;
            }
            if (level.getBlockEntity(pos) instanceof EnderiumRailBlockEntity rail) {
                Direction newDirection = rail.cycleExitDirection(state);
                serverPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable(
                        "message.useful_ores.enderium_rail.exit_direction",
                        net.minecraft.network.chat.Component.translatable("direction.useful_ores." + newDirection.getSerializedName())
                    ), true);
            }
            return net.minecraft.world.InteractionResult.CONSUME;
        }

        if (level.getBlockEntity(pos) instanceof EnderiumRailBlockEntity rail) {
            EnderiumRailLinking.handleInteract(serverLevel, pos, serverPlayer, rail);
        }
        return net.minecraft.world.InteractionResult.CONSUME;
    }

    public static Direction[] axisDirections(BlockState state) {
        RailShape shape = state.getValue(SHAPE);
        return switch (shape) {
            case EAST_WEST, ASCENDING_EAST, ASCENDING_WEST -> new Direction[]{Direction.EAST, Direction.WEST};
            default -> new Direction[]{Direction.NORTH, Direction.SOUTH};
        };
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (level.isClientSide()) return;
        if (!(entity instanceof AbstractMinecart cart)) return;
        tryTeleport(level, pos, cart);
    }

    public static void tryTeleport(Level level, BlockPos pos, AbstractMinecart cart) {
        if (!(level.getBlockEntity(pos) instanceof EnderiumRailBlockEntity rail)) return;

        GlobalPos linked = rail.getLinkedPos();
        if (linked == null) return;
        if (!(level instanceof ServerLevel serverLevel) || !serverLevel.dimension().equals(linked.dimension())) return;

        long gameTime = level.getGameTime();

        if (EnderiumRailBlockEntity.isOnCooldown(cart.getUUID(), pos, gameTime)) return;
        if (!(serverLevel.getBlockEntity(linked.pos()) instanceof EnderiumRailBlockEntity destinationRail)) return;

        BlockPos destination = linked.pos();
        Vec3 velocity = cart.getDeltaMovement();
        Direction exitDirection = destinationRail.getExitDirection(serverLevel.getBlockState(destination));

        double exitSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

        Direction launchDirection = exitDirection;

        if (EnderiumRailLaunchEnforcer.queueTeleport(serverLevel, cart.getUUID(), destination, launchDirection, exitSpeed, velocity.y)) {
            cart.setDeltaMovement(Vec3.ZERO);
        }
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return false;
    }
}

