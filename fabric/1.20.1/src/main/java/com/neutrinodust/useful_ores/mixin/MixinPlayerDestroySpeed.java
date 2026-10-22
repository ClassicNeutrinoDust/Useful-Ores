package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.painite.PainitePower;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayerDestroySpeed {
    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void usefulOres$painiteMiningSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        float multiplier = PainitePower.blockBreakSpeedMultiplier((Player) (Object) this);
        if (multiplier != 1.0F) {
            cir.setReturnValue(cir.getReturnValueF() * multiplier);
        }
    }
}
