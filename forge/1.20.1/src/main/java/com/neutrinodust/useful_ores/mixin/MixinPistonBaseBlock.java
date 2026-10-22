package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.block.ChromitePistonBlock;
import com.neutrinodust.useful_ores.block.ChromiteStickyPistonBlock;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBaseBlock.class)
public class MixinPistonBaseBlock {

   @Redirect(
      method = "moveBlocks",
      at = @At(
         value = "FIELD",

         target = "Lnet/minecraft/world/level/block/Blocks;PISTON_HEAD:Lnet/minecraft/world/level/block/Block;"
      )
   )
   private Block usefulOres$redirectPistonHead() {
      Object self = this;
      if (self instanceof ChromitePistonBlock || self instanceof ChromiteStickyPistonBlock) {
         return ModItems.CHROMITE_PISTON_HEAD.get();
      }
      return Blocks.PISTON_HEAD;
   }

   @Redirect(
      method = "triggerEvent",
      at = @At(
         value = "FIELD",
         target = "Lnet/minecraft/world/level/block/Blocks;STICKY_PISTON:Lnet/minecraft/world/level/block/Block;"
      )
   )
   private Block usefulOres$redirectStickyPistonIdentity() {
      Object self = this;
      if (self instanceof ChromiteStickyPistonBlock) {
         return ModItems.CHROMITE_STICKY_PISTON.get();
      }
      return Blocks.STICKY_PISTON;
   }

   @Redirect(
      method = "triggerEvent",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;isPushable(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;ZLnet/minecraft/core/Direction;)Z"
      )
   )
   private boolean usefulOres$redirectRetractIsPushable(
      BlockState state, Level level, BlockPos pos, Direction direction, boolean allowDestroyable, Direction connectionDirection
   ) {

      Object self = this;
      boolean chromite = self instanceof ChromitePistonBlock || self instanceof ChromiteStickyPistonBlock;

      if (!chromite || !(state.is(Blocks.OBSIDIAN) || state.is(Blocks.CRYING_OBSIDIAN))) {
         return PistonBaseBlock.isPushable(state, level, pos, direction, allowDestroyable, connectionDirection);
      }

      if (pos.getY() < level.getMinBuildHeight() || pos.getY() >= level.getMaxBuildHeight() || !level.getWorldBorder().isWithinBounds(pos)) {
         return false;
      }
      if (direction == Direction.DOWN && pos.getY() == level.getMinBuildHeight()) return false;
      if (direction == Direction.UP && pos.getY() == level.getMaxBuildHeight() - 1) return false;
      if (state.getDestroySpeed(level, pos) == -1.0F || state.hasBlockEntity()) return false;

      return true;
   }
}

