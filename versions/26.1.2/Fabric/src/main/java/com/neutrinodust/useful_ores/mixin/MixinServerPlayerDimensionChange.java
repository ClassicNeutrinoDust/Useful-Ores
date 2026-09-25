package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.bioluminescence.BioluminescentSyncEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class MixinServerPlayerDimensionChange {

    @Inject(method = "teleport", at = @At("RETURN"))
    private void usefulOres$syncGlowingBlocksOnDimensionChange(
            TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        BioluminescentSyncEvents.sendFullSync((ServerPlayer) (Object) this);
        com.neutrinodust.useful_ores.lock.ChestLockSyncEvents.sendFullSync((ServerPlayer) (Object) this);
        com.neutrinodust.useful_ores.phosgene.PhosgenePowderSync.sendFullSync((ServerPlayer) (Object) this);
    }
}

