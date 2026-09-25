package com.neutrinodust.useful_ores.block.rail;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

public class TitaniumControllerRailBlock extends BaseRailBlock implements EntityBlock, SimpleWaterloggedBlock {

    public static final MapCodec<TitaniumControllerRailBlock> CODEC = simpleCodec(TitaniumControllerRailBlock::new);

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<SwitchMode> MODE = EnumProperty.create("mode", SwitchMode.class);
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final EnumProperty<RailShape> SHAPE = EnumProperty.create(
        "shape", RailShape.class,
        RailShape.NORTH_SOUTH, RailShape.EAST_WEST,
        RailShape.NORTH_EAST, RailShape.NORTH_WEST, RailShape.SOUTH_EAST, RailShape.SOUTH_WEST
    );

    public TitaniumControllerRailBlock(Properties properties) {

        super(false, properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(MODE, SwitchMode.STRAIGHT)
            .setValue(SHAPE, RailShape.NORTH_SOUTH)
            .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<TitaniumControllerRailBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(FACING, MODE, SHAPE, WATERLOGGED);
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        boolean waterlogged = context.getLevel().getFluidState(context.getClickedPos())
            .is(net.minecraft.world.level.material.Fluids.WATER);
        return this.defaultBlockState()
            .setValue(FACING, facing)
            .setValue(MODE, SwitchMode.STRAIGHT)
            .setValue(SHAPE, computeShape(facing, SwitchMode.STRAIGHT))
            .setValue(WATERLOGGED, waterlogged);
    }

    public static RailShape computeShape(Direction facing, SwitchMode mode) {
        return switch (facing) {
            case NORTH -> switch (mode) {
                case STRAIGHT -> RailShape.NORTH_SOUTH;
                case LEFT -> RailShape.SOUTH_WEST;
                case RIGHT -> RailShape.SOUTH_EAST;
            };
            case SOUTH -> switch (mode) {
                case STRAIGHT -> RailShape.NORTH_SOUTH;
                case LEFT -> RailShape.NORTH_EAST;
                case RIGHT -> RailShape.NORTH_WEST;
            };
            case EAST -> switch (mode) {
                case STRAIGHT -> RailShape.EAST_WEST;
                case LEFT -> RailShape.NORTH_WEST;
                case RIGHT -> RailShape.SOUTH_WEST;
            };
            case WEST -> switch (mode) {
                case STRAIGHT -> RailShape.EAST_WEST;
                case LEFT -> RailShape.SOUTH_EAST;
                case RIGHT -> RailShape.NORTH_EAST;
            };
            default -> RailShape.NORTH_SOUTH;
        };
    }

    public static SwitchMode applySwitch(Level level, BlockPos pos, BlockState state, SwitchMode mode) {
        Direction facing = state.getValue(FACING);
        RailShape targetShape = computeShape(facing, mode);

        RailShape straightShape = (facing == Direction.EAST || facing == Direction.WEST)
            ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH;
        level.setBlockAndUpdate(pos, state.setValue(MODE, mode).setValue(SHAPE, straightShape));

        BlockPos neighborPos = pos.relative(facing);
        BlockState neighborState = level.getBlockState(neighborPos);
        if (neighborState.getBlock() instanceof BaseRailBlock railBlock) {
            Property<RailShape> shapeProperty = railBlock.getShapeProperty();
            if (shapeProperty.getPossibleValues().contains(targetShape)) {
                level.setBlockAndUpdate(neighborPos, neighborState.setValue(shapeProperty, targetShape));
            }
        }
        return mode;
    }

    public static @Nullable RailShape getControllerCommandedShape(Level level, BlockPos pos) {
        for (Direction fromNeighbor : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(fromNeighbor);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (!(neighborState.getBlock() instanceof TitaniumControllerRailBlock)) continue;
            Direction controllerFacing = neighborState.getValue(FACING);

            if (controllerFacing != fromNeighbor.getOpposite()) continue;
            return computeShape(controllerFacing, neighborState.getValue(MODE));
        }
        return null;
    }

    public static void enforceControllerPriority(Level level, BlockPos pos) {
        RailShape commanded = getControllerCommandedShape(level, pos);
        if (commanded == null) return;

        BlockState hereState = level.getBlockState(pos);
        if (!(hereState.getBlock() instanceof BaseRailBlock railBlock)) return;
        Property<RailShape> shapeProperty = railBlock.getShapeProperty();
        if (!shapeProperty.getPossibleValues().contains(commanded)) return;
        if (hereState.getValue(shapeProperty) == commanded) return;

        level.setBlock(pos, hereState.setValue(shapeProperty, commanded), 3);
    }

    @Override
    protected BlockState updateState(BlockState state, Level level, BlockPos pos, boolean movedByPiston) {
        return state;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                    Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (level.isClientSide()) return;
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        BlockState current = level.getBlockState(pos);
        if (current.getBlock() instanceof TitaniumControllerRailBlock) {
            Direction facing = current.getValue(FACING);
            RailShape straightShape = (facing == Direction.EAST || facing == Direction.WEST)
                ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH;
            if (current.getValue(SHAPE) != straightShape) {
                level.setBlock(pos, current.setValue(SHAPE, straightShape), 3);
            }
        }

        if (level.getBlockEntity(pos) instanceof TitaniumControllerRailBlockEntity controller) {
            controller.onRedstoneUpdate(level, pos, level.getBlockState(pos));
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TitaniumControllerRailBlockEntity(pos, state);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return false;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
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

    private net.minecraft.world.InteractionResult handleUse(
        BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player
    ) {
        if (level.isClientSide()) return net.minecraft.world.InteractionResult.SUCCESS;

        if (player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof TitaniumControllerRailBlockEntity controller) {
            OperatingMode newMode = controller.toggleOperatingMode(level, pos, level.getBlockState(pos));
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                String text = switch (newMode) {
                    case CYCLE -> "Controller rail set to CYCLE mode - each lever flick / button press advances the switch.";
                    case TABLE -> "Controller rail set to TABLE mode - switch follows redstone signal strength directly.";
                };
                serverPlayer.displayClientMessage(net.minecraft.network.chat.Component.literal(text), true);
            }
            return net.minecraft.world.InteractionResult.CONSUME;
        }

        applySwitch(level, pos, state, state.getValue(MODE).next());
        return net.minecraft.world.InteractionResult.CONSUME;
    }
}

