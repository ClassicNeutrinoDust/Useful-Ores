package com.neutrinodust.useful_ores.xpjar;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ArcaniteXpJarBlockEntity extends BlockEntity {

    public static final int MAX_XP = 1395;

    private int storedXp = 0;

    public ArcaniteXpJarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARCANITE_XP_JAR, pos, state);
    }

    public int getStoredXp() {
        return storedXp;
    }

    public void setStoredXp(int xp) {
        this.storedXp = Math.clamp(xp, 0, MAX_XP);
        setChanged();

        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("StoredXp", storedXp);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        storedXp = input.getIntOr("StoredXp", 0);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }
}

