package com.neutrinodust.useful_ores.lighting;

import com.neutrinodust.useful_ores.particle.ColoredFlameOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredTorchBlock extends TorchBlock {

    private final ColoredFlameOptions particle;

    public ColoredTorchBlock(BlockBehaviour.Properties properties, int rgb) {
        super(ParticleTypes.FLAME, properties);
        this.particle = new ColoredFlameOptions(rgb, 1.0F);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.7;
        double z = pos.getZ() + 0.5;
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
        level.addParticle(particle, x, y, z, 0.0, 0.0, 0.0);
    }
}

