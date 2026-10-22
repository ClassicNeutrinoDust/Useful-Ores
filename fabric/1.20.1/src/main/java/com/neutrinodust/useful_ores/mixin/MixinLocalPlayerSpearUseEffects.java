package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.init.SpearTags;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayerSpearUseEffects {
    @Redirect(
        method = "aiStep",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"),
        require = 1
    )
    private boolean usefulOres$ignoreSpearUseInMovement(LocalPlayer player) {
        boolean using = player.isUsingItem();
        if (using && player.getMainHandItem().is(SpearTags.SPEARS) && !player.isSpectator()) {
            return false;
        }
        return using;
    }
}
