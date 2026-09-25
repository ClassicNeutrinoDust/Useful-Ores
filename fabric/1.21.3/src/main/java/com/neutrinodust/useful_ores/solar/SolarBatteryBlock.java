package com.neutrinodust.useful_ores.solar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import org.jetbrains.annotations.Nullable;

public class SolarBatteryBlock extends BaseEntityBlock {

    public static final com.mojang.serialization.MapCodec<SolarBatteryBlock> CODEC = simpleCodec(SolarBatteryBlock::new);

    public static final IntegerProperty CHARGE = IntegerProperty.create("charge", 0, 7);

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public SolarBatteryBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(CHARGE, 0).setValue(FACING, Direction.NORTH));
    }

    




    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CHARGE, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(CHARGE, 0);
    }

    private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 13.0, 12.0);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public static int stageForEnergy(int energy) {
        int stage = Math.round(7.0F * energy / SolarBatteryItem.MAX_ENERGY);
        return Math.max(0, Math.min(7, stage));
    }

    @Override
    public com.mojang.serialization.MapCodec<SolarBatteryBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarBatteryBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, com.neutrinodust.useful_ores.init.ModBlockEntities.SOLAR_BATTERY,
                SolarBatteryBlockEntity::tick);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SolarBatteryBlockEntity be) {
            int energy = SolarBatteryItem.getEnergy(itemStack);
            be.setEnergy(energy);

            BlockState current = level.getBlockState(pos);
            level.setBlock(pos, current.setValue(CHARGE, stageForEnergy(energy)), 3);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SolarBatteryBlockEntity be) {
            ItemStack drop = new ItemStack(com.neutrinodust.useful_ores.init.ModItems.SOLAR_BATTERY.get());
            SolarBatteryItem.setEnergy(drop, be.getEnergy());
            Block.popResource(level, pos, drop);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}

