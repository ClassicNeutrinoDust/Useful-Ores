package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.farseeker.FarseekerBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class MixinServerPlayerGameModeDestroyBlockContext {

    @Shadow public ServerPlayer player;

    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"))
    private void usefulOres$captureBreaker(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        FarseekerBlockEvents.CURRENT_BREAKER.set(this.player);
    }

    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z", at = @At("RETURN"))
    private void usefulOres$clearBreaker(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        FarseekerBlockEvents.CURRENT_BREAKER.remove();
    }
}

