package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.lighting.VoidshardDarknessHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Monster.class)
public class MixinMonsterDarkSpawnCheck {

    @Inject(method = "isDarkEnoughToSpawn", at = @At("RETURN"), cancellable = true)
    private static void usefulOres$voidshardDarkOverride(
        ServerLevelAccessor level, BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir
    ) {
        if (cir.getReturnValueZ()) return;
        if (VoidshardDarknessHandler.INSTANCE.isNearVoidshardDarkSource(level, pos)) {
            cir.setReturnValue(true);
        }
    }
}

