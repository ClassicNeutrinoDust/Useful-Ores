package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.block.ChromitePistonBlock;
import com.neutrinodust.useful_ores.block.ChromitePistonHeadBlock;
import com.neutrinodust.useful_ores.block.ChromiteStickyPistonBlock;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.client.renderer.blockentity.state.PistonHeadRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonHeadRenderer.class)
public class MixinPistonHeadRenderer {

   @Redirect(
      method = "extractRenderState",
      at = @At(
         value = "FIELD",

         target = "Lnet/minecraft/world/level/block/Blocks;PISTON_HEAD:Lnet/minecraft/world/level/block/Block;"
      )
   )
   private Block usefulOres$redirectPistonHead(
         PistonMovingBlockEntity blockEntity,
         PistonHeadRenderState state,
         float partialTicks,
         Vec3 cameraPosition,
         ModelFeatureRenderer.CrumblingOverlay breakProgress) {
      if (usefulOres$isChromite(blockEntity.getMovedState())) {
         return ModItems.CHROMITE_PISTON_HEAD.get();
      }
      return Blocks.PISTON_HEAD;
   }

   @Redirect(
      method = "extractRenderState",
      at = @At(
         value = "FIELD",

         target = "Lnet/minecraft/world/level/block/Blocks;STICKY_PISTON:Lnet/minecraft/world/level/block/Block;"
      )
   )
   private Block usefulOres$redirectStickyPiston(
         PistonMovingBlockEntity blockEntity,
         PistonHeadRenderState state,
         float partialTicks,
         Vec3 cameraPosition,
         ModelFeatureRenderer.CrumblingOverlay breakProgress) {
      if (blockEntity.getMovedState().getBlock() instanceof ChromiteStickyPistonBlock) {
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

