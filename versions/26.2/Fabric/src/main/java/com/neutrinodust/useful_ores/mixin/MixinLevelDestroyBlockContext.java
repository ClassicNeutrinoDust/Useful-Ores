package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.farseeker.FarseekerBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class MixinLevelDestroyBlockContext {

    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;I)Z",
            at = @At("HEAD"))
    private void usefulOres$captureBreaker(BlockPos pos, boolean dropItems, Entity entity, int recursionLeft,
                                            CallbackInfoReturnable<Boolean> cir) {
        FarseekerBlockEvents.CURRENT_BREAKER.set(entity);
    }

    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;I)Z",
            at = @At("RETURN"))
    private void usefulOres$clearBreaker(BlockPos pos, boolean dropItems, Entity entity, int recursionLeft,
                                          CallbackInfoReturnable<Boolean> cir) {
        FarseekerBlockEvents.CURRENT_BREAKER.remove();
    }
}

