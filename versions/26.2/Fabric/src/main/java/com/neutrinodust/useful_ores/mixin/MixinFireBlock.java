package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.blastproof.BlastproofBlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class MixinFireBlock {

   @Inject(method = "checkBurnOut", at = @At("HEAD"), cancellable = true)
   private void usefulOres$skipBlastproof(
         Level level, BlockPos pos, int chance, RandomSource random, int age, CallbackInfo ci) {
      if (level instanceof ServerLevel serverLevel && BlastproofBlockData.get(serverLevel).isBlastproof(pos)) {
         ci.cancel();
      }
   }
}

