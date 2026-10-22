package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.lock.ChestLockData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlock.class)
public class MixinChestBlockLockCleanup {

    @Inject(method = "onRemove", at = @At("HEAD"))
    private void usefulOres$dropLockRecord(BlockState state, Level level, BlockPos pos, BlockState newState,
                                           boolean movedByPiston, CallbackInfo ci) {
        if (state.is(newState.getBlock())) return;
        if (level instanceof ServerLevel serverLevel) {
            ChestLockData.onChestRemoved(serverLevel, pos);
        }
    }
}
