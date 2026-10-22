package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.xpjar.ArcaniteXpJarEvents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public class MixinExperienceOrb {

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void useful_ores$onPlayerTouch(Player player, CallbackInfo ci) {
        if (ArcaniteXpJarEvents.onXpPickup((ExperienceOrb) (Object) this, player)) {
            ci.cancel();
        }
    }
}

