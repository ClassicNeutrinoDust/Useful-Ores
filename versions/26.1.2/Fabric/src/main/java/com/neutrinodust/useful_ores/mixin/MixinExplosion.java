package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.blastproof.BlastproofBlockData;
import com.neutrinodust.useful_ores.blastproof.ExplosionContext;
import com.neutrinodust.useful_ores.lock.ChestLockData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerExplosion.class)
public class MixinExplosion {

    @Shadow @Final private ServerLevel level;

    @Inject(method = "explode", at = @At("HEAD"))
    private void usefulOres$explosionStart(CallbackInfoReturnable<?> cir) {
        ExplosionContext.setActive(true);
    }

    @Inject(method = "explode", at = @At("RETURN"))
    private void usefulOres$explosionEnd(CallbackInfoReturnable<?> cir) {
        ExplosionContext.setActive(false);
    }

    @Inject(method = "calculateExplodedPositions", at = @At("RETURN"), cancellable = true)
    private void usefulOres$filterBlastproof(CallbackInfoReturnable<List<BlockPos>> cir) {
        BlastproofBlockData blastproofData = BlastproofBlockData.get(this.level);
        ChestLockData lockData = ChestLockData.get(this.level);

        List<BlockPos> toBlow = cir.getReturnValue();
        toBlow.removeIf(pos -> blastproofData.isBlastproof(pos) || lockData.isLocked(this.level, pos));
        cir.setReturnValue(toBlow);
    }
}

