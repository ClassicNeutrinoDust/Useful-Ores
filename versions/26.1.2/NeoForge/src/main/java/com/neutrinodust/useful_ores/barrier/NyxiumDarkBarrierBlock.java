package com.neutrinodust.useful_ores.barrier;

import com.mojang.serialization.MapCodec;
import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class NyxiumDarkBarrierBlock extends BaseEntityBlock {
    public static final MapCodec<NyxiumDarkBarrierBlock> CODEC = simpleCodec(NyxiumDarkBarrierBlock::new);

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    private static final double COL_LEFT_LO = -0.875,  COL_LEFT_HI = -0.569;
    private static final double COL_RIGHT_LO = 1.572,  COL_RIGHT_HI = 1.878;
    private static final double BEAM_W_LO = -1.028,    BEAM_W_HI = 2.031;
    private static final double BOTTOM_BEAM_Y_LO = 0.006, BOTTOM_BEAM_Y_HI = 0.465;
    private static final double TOP_BEAM_Y_LO = 1.535,    TOP_BEAM_Y_HI = 1.994;
    private static final double COLUMN_Y_LO = 0.388,      COLUMN_Y_HI = 1.612;
    private static final double BEAM_DEPTH_LO = 0.271,    BEAM_DEPTH_HI = 0.729;
    private static final double COLUMN_DEPTH_LO = 0.347,  COLUMN_DEPTH_HI = 0.653;

    private static final VoxelShape[] COLLISION_SHAPES = new VoxelShape[4];
    private static volatile boolean shapesBuilt = false;

    private static VoxelShape buildCollisionShape(Direction facing) {
        boolean zAxis = facing.getAxis() == Direction.Axis.Z;

        VoxelShape bottomBeam = frameBox(BEAM_W_LO, BEAM_W_HI, BOTTOM_BEAM_Y_LO, BOTTOM_BEAM_Y_HI, BEAM_DEPTH_LO, BEAM_DEPTH_HI, zAxis);
        VoxelShape topBeam    = frameBox(BEAM_W_LO, BEAM_W_HI, TOP_BEAM_Y_LO, TOP_BEAM_Y_HI, BEAM_DEPTH_LO, BEAM_DEPTH_HI, zAxis);
        VoxelShape leftCol    = frameBox(COL_LEFT_LO, COL_LEFT_HI, COLUMN_Y_LO, COLUMN_Y_HI, COLUMN_DEPTH_LO, COLUMN_DEPTH_HI, zAxis);
        VoxelShape rightCol   = frameBox(COL_RIGHT_LO, COL_RIGHT_HI, COLUMN_Y_LO, COLUMN_Y_HI, COLUMN_DEPTH_LO, COLUMN_DEPTH_HI, zAxis);

        return Shapes.or(bottomBeam, topBeam, leftCol, rightCol).optimize();
    }

    private static VoxelShape frameBox(double wLo, double wHi, double yLo, double yHi, double dLo, double dHi, boolean zAxis) {
        return zAxis
                ? Shapes.box(wLo, yLo, dLo, wHi, yHi, dHi)
                : Shapes.box(dLo, yLo, wLo, dHi, yHi, wHi);
    }

    private static VoxelShape collisionShape(Direction facing) {
        if (!shapesBuilt) {
            synchronized (NyxiumDarkBarrierBlock.class) {
                if (!shapesBuilt) {
                    for (Direction d : new Direction[]{
                            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                        COLLISION_SHAPES[d.get2DDataValue()] = buildCollisionShape(d);
                    }
                    shapesBuilt = true;
                }
            }
        }
        return COLLISION_SHAPES[facing.get2DDataValue()];
    }

    public NyxiumDarkBarrierBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<NyxiumDarkBarrierBlock> codec() { return CODEC; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // The visual frame extends beyond the one-block footprint. A simple
        // same-facing neighbour check was insufficient because differently
        // rotated barriers can still occupy the same physical space.
        AABB candidate = worldCollisionBounds(state, pos);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    BlockPos neighbourPos = pos.offset(dx, dy, dz);
                    BlockState neighbour = level.getBlockState(neighbourPos);
                    if (!(neighbour.getBlock() instanceof NyxiumDarkBarrierBlock)) continue;
                    if (candidate.intersects(worldCollisionBounds(neighbour, neighbourPos))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static AABB worldCollisionBounds(BlockState state, BlockPos pos) {
        VoxelShape shape = collisionShape(state.hasProperty(FACING) ? state.getValue(FACING) : Direction.NORTH);
        AABB local = shape.bounds();
        return new AABB(
                local.minX + pos.getX(), local.minY + pos.getY(), local.minZ + pos.getZ(),
                local.maxX + pos.getX(), local.maxY + pos.getY(), local.maxZ + pos.getZ());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                     BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos,
                                     BlockState neighbourState, RandomSource random) {
        if (neighbourState.getBlock() instanceof NyxiumDarkBarrierBlock) {
            if (worldCollisionBounds(state, pos).intersects(worldCollisionBounds(neighbourState, neighbourPos))) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.hasProperty(FACING) ? state.getValue(FACING) : Direction.NORTH;
        return collisionShape(facing);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.hasProperty(FACING) ? state.getValue(FACING) : Direction.NORTH;
        return collisionShape(facing);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {

        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NyxiumDarkBarrierBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide()
                ? null
                : createTickerHelper(type, ModBlockEntities.NYXIUM_DARK_BARRIER.get(),
                        NyxiumDarkBarrierBlockEntity::serverTick);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        level.playSound(null, pos, ModBarrierSounds.NYXIUM_DARK_BARRIER_PLACE.get(),
                SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}

