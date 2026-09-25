package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.block.ChromitePistonBlock;
import com.neutrinodust.useful_ores.block.ChromiteStickyPistonBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonStructureResolver.class)
public class MixinPistonStructureResolver {

   @Unique
   private boolean usefulOres$chromite = false;

   @Inject(method = "<init>", at = @At("TAIL"))
   private void usefulOres$tagChromite(
      Level level, BlockPos pistonPos, Direction direction, boolean extending, CallbackInfo ci
   ) {
      Object block = level.getBlockState(pistonPos).getBlock();
      this.usefulOres$chromite =
         block instanceof ChromitePistonBlock || block instanceof ChromiteStickyPistonBlock;
   }

   @Redirect(
      method = { "resolve", "addBlockLine" },
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;isPushable(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;ZLnet/minecraft/core/Direction;)Z"
      )
   )
   private boolean usefulOres$redirectIsPushable(
      BlockState state, Level level, BlockPos pos, Direction direction, boolean allowDestroyable, Direction connectionDirection
   ) {
      if (!this.usefulOres$chromite || !(state.is(Blocks.OBSIDIAN) || state.is(Blocks.CRYING_OBSIDIAN))) {
         return PistonBaseBlock.isPushable(state, level, pos, direction, allowDestroyable, connectionDirection);
      }

      if (pos.getY() < level.getMinY() || pos.getY() > level.getMaxY() || !level.getWorldBorder().isWithinBounds(pos)) {
         return false;
      }
      if (direction == Direction.DOWN && pos.getY() == level.getMinY()) return false;
      if (direction == Direction.UP && pos.getY() == level.getMaxY()) return false;
      if (state.getDestroySpeed(level, pos) == -1.0F) return false;

      return switch (state.getPistonPushReaction()) {
         case BLOCK -> false;
         case DESTROY -> allowDestroyable;
         case PUSH_ONLY -> direction == connectionDirection;
         default -> !state.hasBlockEntity();
      };
   }

   @ModifyConstant(method = "addBlockLine", constant = @Constant(intValue = 12))
   private int usefulOres$widenPushDepth(int original) {
      return this.usefulOres$chromite ? 20 : original;
   }
}

