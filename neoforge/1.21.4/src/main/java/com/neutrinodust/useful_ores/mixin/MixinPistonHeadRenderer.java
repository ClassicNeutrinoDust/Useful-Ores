package com.neutrinodust.useful_ores.mixin;

import com.neutrinodust.useful_ores.block.ChromitePistonBlock;
import com.neutrinodust.useful_ores.block.ChromitePistonHeadBlock;
import com.neutrinodust.useful_ores.block.ChromiteStickyPistonBlock;
import com.neutrinodust.useful_ores.init.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.PoseStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonHeadRenderer.class)
public class MixinPistonHeadRenderer {

   @Redirect(
      method = "render",
      at = @At(
         value = "FIELD",
         target = "Lnet/minecraft/world/level/block/Blocks;PISTON_HEAD:Lnet/minecraft/world/level/block/Block;"
      )
   )
   private Block usefulOres$redirectPistonHead(
         PistonMovingBlockEntity blockEntity,
         float partialTicks,
         PoseStack poseStack,
         MultiBufferSource bufferSource,
         int packedLight,
         int packedOverlay) {
      if (usefulOres$isChromite(blockEntity.getMovedState())) {
         return ModItems.CHROMITE_PISTON_HEAD.get();
      }
      return Blocks.PISTON_HEAD;
   }

   @Redirect(
      method = "render",
      at = @At(
         value = "FIELD",
         target = "Lnet/minecraft/world/level/block/Blocks;STICKY_PISTON:Lnet/minecraft/world/level/block/Block;"
      )
   )
   private Block usefulOres$redirectStickyPiston(
         PistonMovingBlockEntity blockEntity,
         float partialTicks,
         PoseStack poseStack,
         MultiBufferSource bufferSource,
         int packedLight,
         int packedOverlay) {
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
