package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.lock.ChestLockData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HopperBlockEntity.class, priority = 500)
public class MixinHopperLockedChest {

    @Inject(method = "suckInItems", at = @At("HEAD"), cancellable = true)
    private static void usefulOres$noPullFromLockedChest(Level level, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        BlockPos above = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
        if (!(level.getBlockState(above).getBlock() instanceof ChestBlock)) return;
        if (ChestLockData.get(serverLevel).isLocked(serverLevel, above)) {
            cir.setReturnValue(false);
        }
    }
}
