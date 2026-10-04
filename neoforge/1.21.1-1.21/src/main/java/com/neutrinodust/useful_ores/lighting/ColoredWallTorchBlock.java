package com.neutrinodust.useful_ores.lighting;

import com.neutrinodust.useful_ores.particle.ColoredFlameOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ColoredWallTorchBlock extends WallTorchBlock {

    private static final double WALL_HORIZONTAL_OFFSET = 0.27;
    private static final double WALL_VERTICAL_LIFT = 0.22;

    private final ColoredFlameOptions particle;

    public ColoredWallTorchBlock(BlockBehaviour.Properties properties, int rgb) {
        super(ParticleTypes.FLAME, properties);
        this.particle = new ColoredFlameOptions(rgb, 1.0F);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction opposite = facing.getOpposite();
        double x = pos.getX() + 0.5 + WALL_HORIZONTAL_OFFSET * opposite.getStepX();
        double y = pos.getY() + 0.7 + WALL_VERTICAL_LIFT;
        double z = pos.getZ() + 0.5 + WALL_HORIZONTAL_OFFSET * opposite.getStepZ();
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
        level.addParticle(particle, x, y, z, 0.0, 0.0, 0.0);
    }
}

