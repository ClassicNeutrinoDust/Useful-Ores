package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.blastproof.BlastproofBlockData;
import com.neutrinodust.useful_ores.blastproof.ExplosionContext;
import com.neutrinodust.useful_ores.lock.ChestLockData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(Explosion.class)
public abstract class MixinExplosion {
    @Shadow @Final private Level level;
    @Shadow public abstract List<BlockPos> getToBlow();

    @Inject(method = "explode", at = @At("HEAD"))
    private void usefulOres$explosionStart(CallbackInfo ci) {
        ExplosionContext.setActive(true);
    }

    @Inject(method = "explode", at = @At("RETURN"))
    private void usefulOres$explosionEnd(CallbackInfo ci) {
        try {
            if (level instanceof ServerLevel serverLevel) {
                BlastproofBlockData blastproofData = BlastproofBlockData.get(serverLevel);
                ChestLockData lockData = ChestLockData.get(serverLevel);
                getToBlow().removeIf(pos -> blastproofData.isBlastproof(pos) || lockData.isLocked(serverLevel, pos));
            }
        } finally {
            ExplosionContext.setActive(false);
        }
    }
}
