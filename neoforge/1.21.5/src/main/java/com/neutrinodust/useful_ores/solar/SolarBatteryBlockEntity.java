package com.neutrinodust.useful_ores.solar;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SolarBatteryBlockEntity extends BlockEntity {

    private int energy;

    public SolarBatteryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_BATTERY.get(), pos, state);
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(SolarBatteryItem.MAX_ENERGY, energy));
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SolarBatteryBlockEntity be) {
        if (level.isClientSide()) return;
        if (be.energy >= SolarBatteryItem.MAX_ENERGY) return;

        boolean isDaytime = (level.getDayTime() % 24000L) < 12000L;
        if (isDaytime && !level.isRaining() && level.canSeeSky(pos.above())) {
            int oldStage = SolarBatteryBlock.stageForEnergy(be.energy);
            be.energy = Math.min(SolarBatteryItem.MAX_ENERGY, be.energy + 1);
            be.setChanged();

            int newStage = SolarBatteryBlock.stageForEnergy(be.energy);
            if (newStage != oldStage) {
                level.setBlock(pos, state.setValue(SolarBatteryBlock.CHARGE, newStage), 3);
            }

            if (level instanceof ServerLevel serverLevel && level.getGameTime() % 5 == 0) {
                spawnSunPullParticles(serverLevel, pos);
            }
        }
    }

    private static void spawnSunPullParticles(ServerLevel serverLevel, BlockPos origin) {
        RandomSource random = serverLevel.getRandom();
        double topY = origin.getY() + 1.0 + 0.3 + random.nextDouble() * 0.4;
        double px = origin.getX() + 0.25 + random.nextDouble() * 0.5;
        double pz = origin.getZ() + 0.25 + random.nextDouble() * 0.5;
        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                px, topY, pz, 0, 0.0, -0.05, 0.0, 1.0);
    }

    @Override
    protected void saveAdditional(CompoundTag output, HolderLookup.Provider registries) {
        super.saveAdditional(output, registries);
        output.putInt("Energy", energy);
    }

    @Override
    protected void loadAdditional(CompoundTag input, HolderLookup.Provider registries) {
        super.loadAdditional(input, registries);
        energy = input.getIntOr("Energy", 0);
    }
}

