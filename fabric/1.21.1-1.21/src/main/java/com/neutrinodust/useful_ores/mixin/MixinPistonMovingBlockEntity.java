package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.block.ChromitePistonBlock;
import com.neutrinodust.useful_ores.block.ChromitePistonHeadBlock;
import com.neutrinodust.useful_ores.block.ChromiteStickyPistonBlock;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonMovingBlockEntity.class)
public class MixinPistonMovingBlockEntity {

   @Shadow
   private BlockState movedState;

   @Redirect(
      method = {"getCollisionRelatedBlockState", "getCollisionShape"},
      at = @At(
         value = "FIELD",

         target = "Lnet/minecraft/world/level/block/Blocks;PISTON_HEAD:Lnet/minecraft/world/level/block/Block;"
      )
   )
   private Block usefulOres$redirectPistonHead() {
      if (usefulOres$isChromite(this.movedState)) {
         return ModItems.CHROMITE_PISTON_HEAD.get();
      }
      return Blocks.PISTON_HEAD;
   }

   @Redirect(
      method = "getCollisionRelatedBlockState",
      at = @At(
         value = "FIELD",

         target = "Lnet/minecraft/world/level/block/Blocks;STICKY_PISTON:Lnet/minecraft/world/level/block/Block;"
      )
   )
   private Block usefulOres$redirectStickyPiston() {
      if (this.movedState.getBlock() instanceof ChromiteStickyPistonBlock) {
         return ModItems.CHROMITE_STICKY_PISTON.get();
      }
      return Blocks.STICKY_PISTON;
   }

   private static boolean usefulOres$isChromite(BlockState state) {

      return state.getBlock() instanceof ChromitePistonBlock
         || state.getBlock() instanceof ChromiteStickyPistonBlock
         || state.getBlock() instanceof ChromitePistonHeadBlock;
   }
}

