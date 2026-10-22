package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.lock.ChestLockData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class MixinChestBlockLockCleanup {

    @Inject(method = "affectNeighborsAfterRemoval", at = @At("HEAD"))
    private void usefulOres$dropLockRecord(BlockState state, ServerLevel level, BlockPos pos,
                                           boolean movedByPiston, CallbackInfo ci) {
        if (state.getBlock() instanceof ChestBlock) {
            ChestLockData.onChestRemoved(level, pos);
        }
    }
}
