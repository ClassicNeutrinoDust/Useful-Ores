package com.neutrinodust.useful_ores.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Restores Chromite piston-head rendering on the 1.21.3 PistonHeadRenderer.
 *
 * Blocks.PISTON_HEAD/STICKY_PISTON are GETSTATIC reads, so their redirect
 * handlers must be zero-argument. The current moving piston is captured at
 * render entry so those handlers can still make the same state-dependent
 * decision as the original 1.21.4 renderer logic.
 */
@Mixin(PistonHeadRenderer.class)
public class MixinPistonHeadRenderer {
    @Unique
    private PistonMovingBlockEntity usefulOres$currentPiston;

    private static final String RENDER =
        "render(Lnet/minecraft/world/level/block/piston/PistonMovingBlockEntity;F"
            + "Lcom/mojang/blaze3d/vertex/PoseStack;"
            + "Lnet/minecraft/client/renderer/MultiBufferSource;II)V";

    @Inject(method = RENDER, at = @At("HEAD"))
    private void usefulOres$capturePiston(
        PistonMovingBlockEntity blockEntity,
        float partialTicks,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay,
        CallbackInfo ci
    ) {
        this.usefulOres$currentPiston = blockEntity;
    }

    @Inject(method = RENDER, at = @At("RETURN"))
    private void usefulOres$clearPiston(
        PistonMovingBlockEntity blockEntity,
        float partialTicks,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay,
        CallbackInfo ci
    ) {
        this.usefulOres$currentPiston = null;
    }

    @Redirect(
        method = RENDER,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/block/Blocks;PISTON_HEAD:Lnet/minecraft/world/level/block/Block;"
        )
    )
    private Block usefulOres$redirectPistonHead() {
        PistonMovingBlockEntity blockEntity = this.usefulOres$currentPiston;
        if (blockEntity != null && usefulOres$isChromite(blockEntity.getMovedState())) {
            return ModItems.CHROMITE_PISTON_HEAD.get();
        }
        return Blocks.PISTON_HEAD;
    }

    @Redirect(
        method = RENDER,
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/block/Blocks;STICKY_PISTON:Lnet/minecraft/world/level/block/Block;"
        )
    )
    private Block usefulOres$redirectStickyPiston() {
        PistonMovingBlockEntity blockEntity = this.usefulOres$currentPiston;
        if (blockEntity != null
                && blockEntity.getMovedState().getBlock() instanceof ChromiteStickyPistonBlock) {
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
