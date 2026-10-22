package com.neutrinodust.useful_ores.block;

import com.neutrinodust.useful_ores.particle.ColoredFlameOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredCampfireBlock extends CampfireBlock {

    private final ColoredFlameOptions particle;

    public ColoredCampfireBlock(boolean spawnParticles, int fireDamage, BlockBehaviour.Properties properties, int rgb) {
        super(spawnParticles, fireDamage, properties);
        this.particle = new ColoredFlameOptions(rgb, 1.0F);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {

        super.animateTick(state, level, pos, random);
        if (state.getValue(LIT) && random.nextInt(3) == 0) {
            level.addParticle(particle,
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6,
                    pos.getY() + 0.65 + random.nextDouble() * 0.4,
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6,
                    0.0, 0.03, 0.0);
        }
    }
}

