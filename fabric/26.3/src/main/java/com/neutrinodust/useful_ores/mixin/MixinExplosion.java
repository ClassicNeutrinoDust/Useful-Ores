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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerExplosion.class)
public class MixinExplosion {

    @Shadow @Final private ServerLevel level;

    @Inject(method = "explode", at = @At("HEAD"))
    private void usefulOres$explosionStart(CallbackInfoReturnable<Integer> cir) {
        ExplosionContext.setActive(true);
    }

    @Inject(method = "explode", at = @At("RETURN"))
    private void usefulOres$explosionEnd(CallbackInfoReturnable<Integer> cir) {
        ExplosionContext.setActive(false);
    }

    /**
     * Filter the final block list at the call site in explode(), after
     * calculateExplodedPositions() has completed.
     *
     * This is intentionally a ModifyArg on explode() rather than an injection
     * into calculateExplodedPositions() or interactWithBlocks(). Lithium's
     * block_raycast optimization transforms the calculation path, so hooking
     * the final argument passed to the block-interaction stage leaves Lithium
     * free to perform its optimized raycast and only applies Useful Ores'
     * protection policy to the resulting targets.
     */
    @ModifyArg(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/ServerExplosion;interactWithBlocks(Ljava/util/List;)V"
            ),
            index = 0
    )
    private List<BlockPos> usefulOres$filterProtectedBlocks(List<BlockPos> targetBlocks) {
        BlastproofBlockData blastproofData = BlastproofBlockData.get(this.level);
        ChestLockData lockData = ChestLockData.get(this.level);

        // Do not mutate Lithium's/vanilla's list in place. Returning a fresh
        // mutable list also keeps interactWithBlocks() free to shuffle it.
        List<BlockPos> filtered = new ArrayList<>(targetBlocks.size());
        for (BlockPos pos : targetBlocks) {
            if (!blastproofData.isBlastproof(pos) && !lockData.isLocked(this.level, pos)) {
                filtered.add(pos);
            }
        }
        return filtered;
    }
}

