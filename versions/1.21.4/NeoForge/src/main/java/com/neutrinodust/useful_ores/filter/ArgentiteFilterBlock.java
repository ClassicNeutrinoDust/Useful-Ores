package com.neutrinodust.useful_ores.filter;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class ArgentiteFilterBlock extends BaseEntityBlock {
    public static final MapCodec<ArgentiteFilterBlock> CODEC = simpleCodec(ArgentiteFilterBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING_HOPPER;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    private final Function<BlockState, VoxelShape> shapes;
    private final Map<Direction, VoxelShape> interactionShapes;

    @Override
    public MapCodec<ArgentiteFilterBlock> codec() {
        return CODEC;
    }

    public ArgentiteFilterBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN).setValue(ENABLED, true));
        VoxelShape inside = columnShape(12.0, 11.0, 16.0);
        this.shapes = makeShapes(inside);
        VoxelShape interactionSpout = Shapes.or(inside, boxZShape(4.0, 8.0, 10.0, 0.0, 4.0));
        this.interactionShapes = ImmutableMap.<Direction, VoxelShape>builderWithExpectedSize(5)
                .put(Direction.NORTH, rotateQuarterTurnHorizontal(interactionSpout, Direction.NORTH))
                .put(Direction.EAST, rotateQuarterTurnHorizontal(interactionSpout, Direction.EAST))
                .put(Direction.SOUTH, rotateQuarterTurnHorizontal(interactionSpout, Direction.SOUTH))
                .put(Direction.WEST, rotateQuarterTurnHorizontal(interactionSpout, Direction.WEST))
                .put(Direction.DOWN, inside)
                .build();
    }

    private static Function<BlockState, VoxelShape> makeShapes(VoxelShape inside) {
        VoxelShape spoutlessOutline = Shapes.or(columnShape(16.0, 10.0, 16.0), columnShape(8.0, 4.0, 10.0));
        VoxelShape spoutless = Shapes.join(spoutlessOutline, inside, BooleanOp.ONLY_FIRST);
        VoxelShape baseSpout = boxZShape(4.0, 4.0, 8.0, 0.0, 8.0);
        Map<Direction, VoxelShape> spouts = Map.of(
                Direction.NORTH, rotateAroundPivot(baseSpout, Direction.NORTH, 8.0 / 16.0, 6.0 / 16.0, 8.0 / 16.0),
                Direction.EAST, rotateAroundPivot(baseSpout, Direction.EAST, 8.0 / 16.0, 6.0 / 16.0, 8.0 / 16.0),
                Direction.SOUTH, rotateAroundPivot(baseSpout, Direction.SOUTH, 8.0 / 16.0, 6.0 / 16.0, 8.0 / 16.0),
                Direction.WEST, rotateAroundPivot(baseSpout, Direction.WEST, 8.0 / 16.0, 6.0 / 16.0, 8.0 / 16.0),
                Direction.UP, rotateAroundPivot(baseSpout, Direction.UP, 8.0 / 16.0, 6.0 / 16.0, 8.0 / 16.0),
                Direction.DOWN, rotateAroundPivot(baseSpout, Direction.DOWN, 8.0 / 16.0, 6.0 / 16.0, 8.0 / 16.0));
        return state -> Shapes.or(spoutless, Shapes.join(spouts.get(state.getValue(FACING)), Shapes.block(), BooleanOp.AND));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapes.apply(state);
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return this.interactionShapes.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace().getOpposite();
        return this.defaultBlockState()
                .setValue(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction)
                .setValue(ENABLED, true);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArgentiteFilterBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null
                : createTickerHelper(type, ModBlockEntities.ARGENTITE_FILTER.get(), ArgentiteFilterBlockEntity::pushItemsTick);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock())) {
            checkPoweredState(level, pos, state);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ArgentiteFilterBlockEntity filter) {
            player.openMenu(filter);
            player.awardStat(Stats.INSPECT_HOPPER);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        checkPoweredState(level, pos, state);
    }

    private void checkPoweredState(Level level, BlockPos pos, BlockState state) {
        boolean shouldBeOn = !level.hasNeighborSignal(pos);
        if (shouldBeOn != state.getValue(ENABLED)) {
            level.setBlock(pos, state.setValue(ENABLED, shouldBeOn), 2);
        }
    }

    private static VoxelShape columnShape(double sizeXZ, double minY, double maxY) {
        double min = (16.0 - sizeXZ) * 0.5;
        double max = (16.0 + sizeXZ) * 0.5;
        return Block.box(min, minY, min, max, maxY, max);
    }

    private static VoxelShape boxZShape(double sizeX, double minY, double maxY, double minZ, double maxZ) {
        double minX = (16.0 - sizeX) * 0.5;
        double maxX = (16.0 + sizeX) * 0.5;
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static VoxelShape rotateQuarterTurnHorizontal(VoxelShape shape, Direction direction) {
        return rotateAroundPivot(shape, direction, 0.5, 0.5, 0.5);
    }

    /**
     * Rotates an axis-aligned VoxelShape by quarter turns around the supplied pivot.
     * Minecraft's Shapes.rotateHorizontal/rotateAll helpers used by the newer source
     * are not present in the 1.21.4 mappings, so the same quarter-turn transforms are
     * applied directly to each AABB.
     */
    private static VoxelShape rotateAroundPivot(VoxelShape shape, Direction direction, double pivotX, double pivotY, double pivotZ) {
        if (direction == Direction.NORTH) {
            return shape;
        }

        final VoxelShape[] rotated = {Shapes.empty()};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double[] x = {minX, maxX};
            double[] y = {minY, maxY};
            double[] z = {minZ, maxZ};
            double outMinX = Double.POSITIVE_INFINITY;
            double outMinY = Double.POSITIVE_INFINITY;
            double outMinZ = Double.POSITIVE_INFINITY;
            double outMaxX = Double.NEGATIVE_INFINITY;
            double outMaxY = Double.NEGATIVE_INFINITY;
            double outMaxZ = Double.NEGATIVE_INFINITY;

            for (double cx : x) {
                for (double cy : y) {
                    for (double cz : z) {
                        double rx = cx;
                        double ry = cy;
                        double rz = cz;
                        double dx = cx - pivotX;
                        double dy = cy - pivotY;
                        double dz = cz - pivotZ;

                        switch (direction) {
                            case EAST -> {
                                rx = pivotX - dz;
                                rz = pivotZ + dx;
                            }
                            case SOUTH -> {
                                rx = pivotX - dx;
                                rz = pivotZ - dz;
                            }
                            case WEST -> {
                                rx = pivotX + dz;
                                rz = pivotZ - dx;
                            }
                            case UP -> {
                                ry = pivotY - dz;
                                rz = pivotZ + dy;
                            }
                            case DOWN -> {
                                ry = pivotY + dz;
                                rz = pivotZ - dy;
                            }
                            default -> {
                                // NORTH is returned above; no other direction is expected here.
                            }
                        }

                        outMinX = Math.min(outMinX, rx);
                        outMinY = Math.min(outMinY, ry);
                        outMinZ = Math.min(outMinZ, rz);
                        outMaxX = Math.max(outMaxX, rx);
                        outMaxY = Math.max(outMaxY, ry);
                        outMaxZ = Math.max(outMaxZ, rz);
                    }
                }
            }

            rotated[0] = Shapes.or(rotated[0], Block.box(
                    outMinX * 16.0, outMinY * 16.0, outMinZ * 16.0,
                    outMaxX * 16.0, outMaxY * 16.0, outMaxZ * 16.0));
        });

        return rotated[0];
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ArgentiteFilterBlockEntity filter) {
                Containers.dropContents(level, pos, filter);
            }
            level.updateNeighborsAt(pos, this);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ArgentiteFilterBlockEntity filter) {
            ArgentiteFilterBlockEntity.entityInside(level, pos, state, entity, filter);
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}

