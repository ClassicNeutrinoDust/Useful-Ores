package com.neutrinodust.useful_ores.block.rail;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TitaniumControllerRailBlockEntity extends BlockEntity {

    private static final SwitchMode[] TABLE_LOOKUP = {
        SwitchMode.LEFT, SwitchMode.STRAIGHT, SwitchMode.RIGHT,
        SwitchMode.LEFT, SwitchMode.STRAIGHT, SwitchMode.RIGHT,
        SwitchMode.LEFT, SwitchMode.STRAIGHT, SwitchMode.RIGHT,
        SwitchMode.LEFT, SwitchMode.STRAIGHT, SwitchMode.RIGHT,
        SwitchMode.LEFT, SwitchMode.STRAIGHT, SwitchMode.RIGHT,
        SwitchMode.LEFT
    };

    private OperatingMode operatingMode = OperatingMode.CYCLE;
    private int lastSeenPower = 0;

    public TitaniumControllerRailBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TITANIUM_CONTROLLER_RAIL.get(), pos, state);
    }

    public OperatingMode getOperatingMode() {
        return this.operatingMode;
    }

    public OperatingMode toggleOperatingMode(Level level, BlockPos pos, BlockState state) {
        this.operatingMode = (this.operatingMode == OperatingMode.CYCLE) ? OperatingMode.TABLE : OperatingMode.CYCLE;
        this.setChanged();

        this.lastSeenPower = -1;
        this.onRedstoneUpdate(level, pos, level.getBlockState(pos));
        return this.operatingMode;
    }

    public static SwitchMode fromSignalStrength(int power) {
        int clamped = Math.max(0, Math.min(15, power));
        return TABLE_LOOKUP[15 - clamped];
    }

    public void onRedstoneUpdate(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        int power = level.getBestNeighborSignal(pos);
        int previous = this.lastSeenPower;
        this.lastSeenPower = power;
        if (power == previous) return;

        SwitchMode newMode;
        if (this.operatingMode == OperatingMode.CYCLE) {

            boolean wasOn = previous > 0;
            boolean isOn = power > 0;
            if (!isOn || wasOn) return;
            newMode = state.getValue(TitaniumControllerRailBlock.MODE).next();
        } else {

            newMode = fromSignalStrength(power);
        }

        if (newMode == state.getValue(TitaniumControllerRailBlock.MODE)) return;

        this.setChanged();
        TitaniumControllerRailBlock.applySwitch(level, pos, state, newMode);
    }

    @Override
    protected void loadAdditional(CompoundTag input, HolderLookup.Provider registries) {
        super.loadAdditional(input, registries);
        this.lastSeenPower = Math.max(0, Math.min(15, input.getIntOr("last_seen_power", 0)));
        this.operatingMode = input.getBooleanOr("table_mode", false) ? OperatingMode.TABLE : OperatingMode.CYCLE;
    }

    @Override
    protected void saveAdditional(CompoundTag output, HolderLookup.Provider registries) {
        super.saveAdditional(output, registries);
        output.putInt("last_seen_power", this.lastSeenPower);
        output.putBoolean("table_mode", this.operatingMode == OperatingMode.TABLE);
    }
}

