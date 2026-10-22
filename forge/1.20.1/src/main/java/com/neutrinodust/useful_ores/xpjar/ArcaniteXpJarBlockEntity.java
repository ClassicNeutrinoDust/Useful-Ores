package com.neutrinodust.useful_ores.xpjar;

import com.neutrinodust.useful_ores.util.NbtCompat;

import com.neutrinodust.useful_ores.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ArcaniteXpJarBlockEntity extends BlockEntity {

    public static final int MAX_XP = 1395;

    private int storedXp = 0;

    public ArcaniteXpJarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARCANITE_XP_JAR.get(), pos, state);
    }

    public int getStoredXp() {
        return storedXp;
    }

    public void setStoredXp(int xp) {
        this.storedXp = Math.max(0, Math.min(MAX_XP, xp));
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag output) {
        super.saveAdditional(output);
        output.putInt("StoredXp", storedXp);
    }

    @Override
    public void load(CompoundTag input) {
        super.load(input);
        storedXp = NbtCompat.getIntOr(input, "StoredXp", 0);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }
}

