package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.farseeker.FarseekerDropCapture;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class MixinServerPlayerGameModeFarseeker {
    @Shadow @Final protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void usefulOres$beginFarseekerCapture(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        FarseekerDropCapture.begin(player);
    }

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void usefulOres$endFarseekerCapture(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        FarseekerDropCapture.end();
    }
}
