package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.init.ModOreFireBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseFireBlock.class)
public class MixinBaseFireBlock {

   @Inject(method = "getState", at = @At("HEAD"), cancellable = true)
   private static void usefulOres$coloredOreFireState(
         BlockGetter level, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
      BlockState below = level.getBlockState(pos.below());
      DeferredBlock<Block> fire = ModOreFireBlocks.IGNITABLE_TO_FIRE.get(below.getBlock());
      if (fire != null) {
         cir.setReturnValue(fire.get().defaultBlockState());
      }
   }
}

